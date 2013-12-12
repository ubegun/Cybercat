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
package org.cybercat.automation;

import org.apache.commons.lang3.StringUtils;
import org.cybercat.automation.addons.common.ScreenshotManager;
import org.cybercat.automation.addons.common.TestLoggerAddon;
import org.cybercat.automation.annotations.CCTestCase;

public class Configuration {

    private String[] features;
    
    public Configuration() {
        features = new String[]{ScreenshotManager.EXCEPTION_SCREENSHOT, TestLoggerAddon.FULL_LOG};
    }

    public Configuration(CCTestCase testAnnotation) {
        super();
        if(testAnnotation != null) 
            this.features = testAnnotation.features();
    }


    public String[] getFeatures() {
        return features;
    }

    public void setFeatures(String[] features) {
        this.features = features;
    }

    public boolean hasFeature(String featureName){
        for(int i = 0; i < features.length; i++){
            if(StringUtils.equals(features[i], featureName))
                return true;
        }
        return false;
    }
}
