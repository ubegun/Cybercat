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
package org.cybercat.automation.addons.common;

import java.nio.file.Path;

import org.cybercat.automation.events.Event;
import org.cybercat.automation.events.EventManager.EventTypes;


public class MakeScreenshotEvent extends Event {

    public enum ImageFormat {
        PNG("png"), JPEG("jpg");
        
        private String name;

        ImageFormat(String name){
            this.name = name;
        }
        
        public String getName(){
            return name; 
        }
    }

    private Path path;
    private String subtitles;
    private String fileName;
    private ImageFormat format;
    private Class<?> testClass;

    public MakeScreenshotEvent(Class<?> testClass,  Path path, String fileName, ImageFormat format, String[] subtitles) {
        this(testClass, path, fileName, format);
        StringBuffer desc = new StringBuffer();
        for (int i = 0; i < subtitles.length; i++) {
            desc.append(subtitles[i]).append("\n");
        }
        this.subtitles = desc.toString();
    }

    public MakeScreenshotEvent(Class<?> testClass, Path path, String fileName, ImageFormat format) {
        this.testClass = testClass;
        this.format = format;
        this.fileName = fileName;
        this.path = path;
    }

    @Override
    public EventTypes getType() {
        return EventTypes.MAKE_SCREENSHOT;
    }
    
    public String getSubtitles() {
        return subtitles;
    }

    public Path getPath() {
        return path ;
    }

    public String getFileName() {
        return fileName;
    }

    public ImageFormat getFormat() {
        return format;
    }

    public Class<?> getTestClass() {
        return testClass;
    }

    public void setTestClass(Class<?> testClass) {
        this.testClass = testClass;
    }
    
}
