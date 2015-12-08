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

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.cybercat.automation.addons.common.ScreenshotManager;
import org.cybercat.automation.addons.common.TestLoggerAddon;
import org.cybercat.automation.annotations.CCTestCase;

public class TestContext {

  private String[] features;
  private String testDescription;
  private Date buildGuid;
  private String testGuid; 
    
  public TestContext() {
    features = new String[] { ScreenshotManager.EXCEPTION_SCREENSHOT, TestLoggerAddon.FULL_LOG };
  }

  public TestContext(CCTestCase testAnnotation) {
    super();
    if (testAnnotation == null)
      return;
    this.testDescription = testAnnotation.description();
    this.features = testAnnotation.features();
  }

  public String getTestDescription() {
    return testDescription;
  }

  public void setTestDescription(String testDescription) {
    this.testDescription = testDescription;
  }

  public String[] getFeatures() {
    return features;
  }

  public void setFeatures(String[] features) {
    this.features = features;
  }

  public boolean hasFeature(String featureName) {
    for (int i = 0; i < features.length; i++) {
      if (StringUtils.equals(features[i], featureName))
        return true;
    }
    return false;
  }

  public Date getBuildGuid() {
    return buildGuid;
  }

  public void setBuildGuid(Date buildGuid) {
    this.buildGuid = buildGuid;
  }

  public String getTestGuid() {
    return testGuid;
  }

  public void setTestGuid(String testGuid) {
    this.testGuid = testGuid;
  }
  
}
