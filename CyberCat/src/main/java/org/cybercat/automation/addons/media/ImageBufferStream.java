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

import java.awt.Dimension;
import java.io.IOException;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferStream;

import org.cybercat.automation.addons.media.events.StartMediaEvent;
import org.cybercat.automation.addons.media.events.StopMediaEvent;
import org.cybercat.automation.events.EventListener;


public class ImageBufferStream implements PullBufferStream {

    private int height, width;
    private boolean isEOM = false;
    private VideoFormat format;
    private int frameRate;
    private FrameSet frameSet;
    private long threadId;

    public ImageBufferStream(MediaMetaData metaData, FrameSet frameSet, long threadId) {
        this.height = metaData.getHeight();
        this.width = metaData.getWidth();
        this.frameRate = metaData.getFps();
        this.format = new VideoFormat(VideoFormat.JPEG, new Dimension(width, height), Format.NOT_SPECIFIED,
                Format.byteArray, (float) frameRate);
        this.frameSet = frameSet;
        this.frameSet.setFrameIndex(metaData.getFps());
        this.threadId = threadId;
    }

    @Override
    public boolean endOfStream() {
        return isEOM;
    }

    @Override
    public ContentDescriptor getContentDescriptor() {
        return new ContentDescriptor(ContentDescriptor.RAW);
    }

    @Override
    public long getContentLength() {
        return 0;
    }

    @Override
    public Object getControl(String arg0) {
        return null;
    }

    @Override
    public Object[] getControls() {
        return new Object[0];
    }

    @Override
    public Format getFormat() {
        return format;
    }

    @Override
    public void read(Buffer buf) throws IOException {
       try{ 
        if (isEOM) {
            log("firing EOM event");
            buf.setEOM(true);
            buf.setOffset(0);
            buf.setLength(0);
            return;
        }
        waitForNextFrame(30);
        byte[] data = frameSet.next();
        buf.setOffset(0);
        buf.setLength(data.length);
        buf.setData(data);
        buf.setFormat(format);
        buf.setFlags(buf.getFlags() | Buffer.FLAG_KEY_FRAME);
       }catch(Exception e){  
           e.printStackTrace();
       }
    }

    private void waitForNextFrame(int second) {
        synchronized (this) {
            int i = second * frameRate;
            while (i-- > 0 && frameSet.getStackSize() == 0) {
                try {
                    wait(1000 / frameRate);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // this.frameSet.setFrameIndex(frameRate);
            }
        }
    }

    @Override
    public boolean willReadBlock() {
        return false;
    }

    public EventListener<?>[] createListeners() {
        EventListener<StartMediaEvent> startListener = new EventListener<StartMediaEvent>(StartMediaEvent.class, 100) {

            @Override
            public void doActon(StartMediaEvent event) throws Exception {
                isEOM = false;
            }
        };

        EventListener<StopMediaEvent> stopListener = new EventListener<StopMediaEvent>(StopMediaEvent.class, 100) {

            @Override
            public void doActon(StopMediaEvent event) throws Exception {
                isEOM = true;
            }
        };
        return new EventListener<?>[] { startListener, stopListener };
    }


    private void log(String message){
        System.err.println("Video Log --> Thread#" + threadId + " \t" + message); 
    }
 }
