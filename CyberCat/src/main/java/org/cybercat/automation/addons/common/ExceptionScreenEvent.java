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

import org.cybercat.automation.addons.common.MakeScreenshotEvent.ImageFormat;
import org.cybercat.automation.events.Event;
import org.cybercat.automation.events.EventManager.EventTypes;


public class ExceptionScreenEvent extends Event {

    private Path path;
    private Throwable exception;
    private String fileName;
    private ImageFormat format;
    private Class<?> testClass;
    
    public ExceptionScreenEvent(Class<?> testClass, Path path, Throwable exception, String fileName, ImageFormat format) {
        super();
        this.testClass = testClass;
        this.path = path;
        this.exception = exception;
        this.fileName = fileName;
        this.format = format;
    }

    @Override
    public EventTypes getType() {
        return EventTypes.EXCEPTION;
    }

    public Path getPath() {
        return path;
    }

    public Throwable getException() {
        return exception;
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
