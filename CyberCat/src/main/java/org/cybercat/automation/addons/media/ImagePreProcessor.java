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

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.cybercat.automation.addons.media.events.TakeScreenshotEvent;
import org.cybercat.automation.addons.media.events.UpdateMediaStream;
import org.cybercat.automation.events.EventListener;
import org.cybercat.automation.events.EventManager;

import com.sun.media.Log;

public class ImagePreProcessor extends EventListener<TakeScreenshotEvent> {

    private int resultFrameCounts;
    private MediaMetaData metaData;
    private EventManager eventManager;
    private int centerX = -1;
    private int centerY = -1;

    public ImagePreProcessor(MediaMetaData metaData, EventManager eventManager) {
        super(TakeScreenshotEvent.class, 100);
        this.metaData = metaData;
        this.resultFrameCounts = metaData.getMaxFadeIn() * metaData.getFps();
        this.eventManager = eventManager;
    }

    /**
     * Calculates proportional rate for resize source image
     */
    private float calcResizeRate(BufferedImage source) {
        float hRate = (float) metaData.getHeight() / source.getHeight();
        float wRate = (float) metaData.getWidth() / source.getWidth();
        if (hRate > 1)
            return hRate;
        if (wRate > 1)
            return wRate;
        if (hRate > wRate)
            return hRate;
        return wRate;
    }

    public BufferedImage[] zoom(byte[] frame) throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream(frame);
        return zoom(ImageIO.read(stream));
    }

    /**
     * @param frame
     * @param causesFrame
     * @return
     * @throws Exception
     */
    public BufferedImage[] zoom(BufferedImage frame) throws Exception {
        BufferedImage[] images = new BufferedImage[resultFrameCounts];
        float baseFR = calcResizeRate(frame);
        for (int i = 0; i < resultFrameCounts - 1; i++) {
            // makes incremental speed of resize image to normal frame size
            float fadeInRate = 1 - baseFR / (resultFrameCounts - i);
            images[i] = resize(frame, fadeInRate, fadeInRate);
        }
        images[resultFrameCounts - 1] = resize(frame, 1 - baseFR, 1 - baseFR);
        return images;
    }

    public BufferedImage crop(byte[] source) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(source);
        return crop(ImageIO.read(stream), centerX * 1, centerY * 1);
    }

    public BufferedImage[] normalize(byte[] source) throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream(source);
        return normalize(ImageIO.read(stream));
    }

    public BufferedImage[] resizByWidth(byte[] source) throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream(source);
        BufferedImage image = ImageIO.read(stream);
        float baseFR = calcResizeRate(image);
        return new BufferedImage[] { resize(image, baseFR, baseFR) };
    }

    int preHeight = 0;
    int preWidth = 0;

    public BufferedImage[] normalize(BufferedImage source) throws Exception {
        BufferedImage[] pImage;
        if (preHeight != 0 && preHeight != source.getHeight()) {
            pImage = zoom(source);
        } else {
            float resizeRate = calcResizeRate(source);
            pImage = new BufferedImage[] { resize(source, resizeRate, resizeRate) };
        }
        preHeight = source.getHeight();
        preWidth = source.getWidth();

        return pImage;
    }

    public BufferedImage resize(BufferedImage source, float widthRate, float heightRate) {
        AffineTransformOp aTransformOp = new AffineTransformOp(AffineTransform.getScaleInstance(widthRate, heightRate),
                AffineTransformOp.TYPE_BILINEAR);
        return crop(aTransformOp.filter(source, null), (int) (widthRate * this.centerX),
                (int) (heightRate * this.centerY));
    }

    public BufferedImage crop(BufferedImage source, int centerX, int centerY) {
        int x = 0;
        int y = 0;
        if (centerX > metaData.getWidth()) {
            x = centerX - metaData.getWidth() / 2;
            if (centerX + metaData.getWidth() / 2 > source.getWidth()) {
                x = source.getWidth() - metaData.getWidth();
            }
        }
        if (centerY > metaData.getHeight()) {
            y = centerY - metaData.getHeight() / 2;
            if (centerY + metaData.getHeight() / 2 > source.getHeight()) {
                y = source.getHeight() - metaData.getHeight();
            }
        }
        try {
            return source.getSubimage(x, y, metaData.getWidth(), metaData.getHeight());
        } catch (Exception e) {
            Log.error("source {" + source.getWidth() + ";" + source.getHeight() + "}  decination {" + x + ";" + y + ";"
                    + metaData.getWidth() + ";" + metaData.getHeight() + "}");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doActon(TakeScreenshotEvent newFrame) throws Exception {
        centerX = newFrame.getCenterX();
        centerY = newFrame.getCenterY();
        switch (newFrame.getEffect()) {
        case INCREMENTAL_RESIZING:
            eventManager.notify(new UpdateMediaStream(this.zoom(newFrame.getFrame())));
            break;
        case NORMALIZATION:
            eventManager.notify(new UpdateMediaStream(this.normalize(newFrame.getFrame())));
            break;
        case RESIZ_BY_WIDTH:
            eventManager.notify(new UpdateMediaStream(this.resizByWidth(newFrame.getFrame())));
            break;
        case CROP:
            eventManager.notify(new UpdateMediaStream(new BufferedImage[]{this.crop( newFrame.getFrame())}));
            break;
        default:
            break;
        }
    }

}
