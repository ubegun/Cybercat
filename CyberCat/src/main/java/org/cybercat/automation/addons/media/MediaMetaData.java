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

public class MediaMetaData {
    
    private int fps = 5 ; // frame per second 
    private int height = 480 ; 
    private int width = 640;
    private int maxFadeIn = 4; //seconds  
    
    public MediaMetaData(int fps, int height, int width, int maxFadeIn) {
        this.fps = fps;
        this.height = height;
        this.width = width;
        this.maxFadeIn = maxFadeIn;
    }

    public int getFps() {
        return fps;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getMaxFadeIn() {
        return maxFadeIn;
    }

}
