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
package org.cybercat.automation.addons.media.events;

import org.cybercat.automation.addons.common.ScreenshotProvider;
import org.cybercat.automation.events.Event;
import org.cybercat.automation.events.EventManager.EventTypes;


public class TakeScreenshotEvent extends Event {

    public enum EffectType {
        RESIZ_BY_WIDTH, INCREMENTAL_RESIZING, NORMALIZATION, CROP
    };

    private byte[] frame;
    private EffectType effect;
    private ScreenshotProvider screenshotProvider;
    private int centerX = 0;
    private int centerY = 0;

    public TakeScreenshotEvent(ScreenshotProvider screenshotProvider, EffectType effect, int centerX, int centerY) {
        this(screenshotProvider, effect);
        this.centerX = centerX;
        this.centerY = centerY;
        
    }

    public TakeScreenshotEvent(ScreenshotProvider screenshotProvider, EffectType effect) {
        super();
        this.screenshotProvider = screenshotProvider;
        this.effect = effect;
    }

    @Override
    public EventTypes getType() {
        return EventTypes.ADD_SCREENSHOT;
    }

    public byte[] getFrame() {
        if (frame == null) {
            frame = screenshotProvider.getScreen();
        }
        return frame;
    }

    public EffectType getEffect() {
        return effect;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }
    
}
