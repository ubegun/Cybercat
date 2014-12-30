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
package org.cybercat.automation.events;

import org.cybercat.automation.addons.common.MakeScreenshotEvent.ImageFormat;
import org.cybercat.automation.events.EventManager.EventTypes;
import org.cybercat.automation.test.AbstractFeature;
import org.cybercat.automation.test.AbstractTestCase;
import org.cybercat.automation.utils.CommonUtils;


public class EventStartTestStep extends Event {
    
    private Class<? extends AbstractTestCase> testClass;
    private String subtitles;
    private String methodName;
    private ImageFormat format;
    private String startStepTime;
    private Class<? extends AbstractFeature> featureClass;
    
    public EventStartTestStep(Class<? extends AbstractTestCase> testClass, Class<? extends AbstractFeature> featureClass,   String subtitles, String methodName) {
        this(testClass, featureClass, subtitles, methodName, ImageFormat.JPEG);
    }
    
    public EventStartTestStep(Class<? extends AbstractTestCase>  testClass, Class<? extends AbstractFeature> featureClass, String subtitles, String methodName, ImageFormat format) {
        super();
        this.testClass = testClass;
        this.featureClass = featureClass;
        this.subtitles = subtitles;
        this.methodName = methodName;
        this.format = format;
        this.startStepTime = CommonUtils.getCurrentDate();
        
    }

    @Override
    public EventTypes getType() {
        return EventTypes.NEXT_TEST_STEP;
    }

    public String getSubtitles() {
        return subtitles;
    }


    public String getMethodName() {
        return methodName;
    }

    public ImageFormat getFormat() {
        return format;
    }

    public Class<?> getTestClass() {
        return testClass;
    }

    public String getStartStepTime() {
        return startStepTime;
    }

    public Class<? extends AbstractFeature> getFeatureClass() {
        return featureClass;
    }

    public void setFeatureClass(Class<? extends AbstractFeature> featureClass) {
        this.featureClass = featureClass;
    }
    
}
