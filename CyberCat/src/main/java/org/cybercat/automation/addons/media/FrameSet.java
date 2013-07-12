/**Copyright 2013 The Cybercat project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cybercat.automation.addons.media;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import org.cybercat.automation.addons.media.events.UpdateMediaStream;
import org.cybercat.automation.events.EventAddSubtitles;
import org.cybercat.automation.events.EventListener;
import org.cybercat.automation.events.EventStartTestStep;


public class FrameSet {

    private long frameIndex = 0;
    private LinkedList<BufferedImage> stack = new  LinkedList<BufferedImage>();
    private byte[] lastFrame;
    private int bottomOffset;
    private Font font;
    private int lineOffset; // px
    private Color fontColor;
    private String[] subtitle;
    private long threadId;
    private boolean hasInterrupted = false;

    
    public FrameSet(long threadId) {
        bottomOffset = 70;
        lineOffset = 10;
        font = new Font("Arial", Font.PLAIN, 24);
        fontColor = Color.RED;
        this.threadId = threadId;
    }

    public boolean hasNext() {
        return lastFrame != null || stack.size() > 0;
    }

    public synchronized byte[] next() {
        if (stack.size() > 0) {
            try {
                frameIndex = stack.size();
                lastFrame = imageToByteArray(stack.pollLast());
                frameIndex--;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        log("JMF frame index:" + frameIndex );
        return lastFrame;
    }
    
    public int getStackSize(){
        return stack.size();
    }
    
    public void setFrameIndex(long frameIndex) {
        this.frameIndex = frameIndex;
    }

    public long getFrameIndex() {
        return frameIndex;
    }

    public static synchronized BufferedImage toBufferedImage(Image src) {
        int w = src.getWidth(null);
        int h = src.getHeight(null);
        int type = BufferedImage.TYPE_INT_RGB; // other options
        BufferedImage dest = new BufferedImage(w, h, type);
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage(src, 0, 0, null);
        g2.dispose();
        return dest;
    }
    
    private byte[] imageToByteArray(BufferedImage frame) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BufferedImage image = toBufferedImage(frame);
        if(subtitle != null){
            image = applySubs(image, subtitle);
        }
        ImageIO.write(image, "JPG", stream);
        return stream.toByteArray();
    }
    
    private BufferedImage applySubs(BufferedImage image, String[] subs) {
        Graphics2D g2 = image.createGraphics();
        g2.setFont(font);

        int height = image.getHeight();
        int width = image.getWidth();

        g2.setColor(fontColor);

        FontMetrics fMetrics = g2.getFontMetrics();
        int lineHeight = fMetrics.getHeight();
        int lineWidth;
        for (int i = subs.length; i > 0; i--) {
            lineWidth = fMetrics.stringWidth(subs[i - 1]);
            int y = height - bottomOffset - ((subs.length - i) * (lineHeight + lineOffset));
            int x = (width / 2) - (lineWidth / 2);
            g2.drawString(subs[i - 1], x, y);
        }
        g2.dispose();
        return image;
    }

    public Collection<EventListener<?>> createListeners() {
        Collection<EventListener<?>> listeners = new ArrayList<EventListener<?>>();
        
        listeners.add(new EventListener<UpdateMediaStream>(UpdateMediaStream.class, 100){
            @Override
            public synchronized void doActon(UpdateMediaStream event) throws Exception {
                for (int i = 0; i < event.getImages().length; i++){
                  if(!hasInterrupted && event.getImages()[i] != null)  
                      stack.push(event.getImages()[i]);
                }  
                log("Added frames:" + event.getImages().length + " Stack size: " + stack.size());
            }
            
        });
        
        listeners.add(new EventListener<EventAddSubtitles>(EventAddSubtitles.class, 100){

            @Override
            public void doActon(EventAddSubtitles event) throws Exception {
                subtitle = event.getSubtitles();
            }
            
        });
        
        listeners.add(new EventListener<EventStartTestStep>(EventStartTestStep.class, 100){
            @Override
            public synchronized void doActon(EventStartTestStep event) throws Exception {
                subtitle = event.getSubtitles().split("\n");
            }
            
        });
        
        
        return listeners;
    }

    public void finalize(){
        stack = new  LinkedList<BufferedImage>();
        hasInterrupted = true;
    }

        
    private void log(String message){
        System.err.println("Video Log --> Thread#" + threadId + " \t" + message); 
    }
}
