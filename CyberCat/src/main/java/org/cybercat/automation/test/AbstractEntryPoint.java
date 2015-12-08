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
package org.cybercat.automation.test;

import java.util.Date;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.TestContext;
import org.cybercat.automation.TestCaseInitializationException;
import org.cybercat.automation.annotations.AnnotationBuilder;
import org.cybercat.automation.annotations.CCTestCase;
import org.cybercat.automation.core.AutomationMain;
import org.cybercat.automation.events.EventChangeTestConfig;
import org.cybercat.automation.events.EventStartTest;
import org.cybercat.automation.events.EventStopTest;
<<<<<<< HEAD:CyberCat/src/main/java/org/cybercat/automation/test/AbstractEntryPoint.java
import org.cybercat.automation.persistence.TestArtifactManager;
import org.cybercat.automation.persistence.model.TestRun;
=======
import org.testng.ITestContext;
>>>>>>> 9791950d3370025567af91ddfddda2be34151956:CyberCat/src/main/java/org/cybercat/automation/test/AbstractEntryPoint.java

public abstract class AbstractEntryPoint {

    public ResourceBundle testData;
    private String errorMessage;
    

    public void beforeTest() throws AutomationFrameworkException {
        testData = AutomationMain.getTestMetaData();
        setupTestConfiguration();
        setup();
        AnnotationBuilder.processCCFeature(this);
    }

    public void afterTest() throws AutomationFrameworkException {
      AutomationMain.getEventManager().notify(new EventStopTest(this.getClass().getSimpleName(), this.getClass().getName(), new Date()));
    }

    public void setInitializationFail(String errorMessage){
        this.errorMessage = errorMessage;
    }
    
    public abstract void setup() throws AutomationFrameworkException;

    private void setupTestConfiguration() throws AutomationFrameworkException {
        CCTestCase tAnnotation = this.getClass().getAnnotation(CCTestCase.class);
        TestContext config = new TestContext(tAnnotation);
        TestRun thisRun = TestArtifactManager.getInstance().getThisTestRun();
        //Test id`s setup 
        config.setBuildGuid(thisRun.getStarted());
        config.setTestGuid(this.getClass().getName());
        
        String subtitles = "";
        if (tAnnotation != null) {
            subtitles = tAnnotation.description();
        }
        AutomationMain.getEventManager().notify(new EventChangeTestConfig(this, config));
        if(StringUtils.isNotBlank(errorMessage))
            throw new TestCaseInitializationException(errorMessage);
        AutomationMain.getEventManager().notify(new EventStartTest(subtitles, getBugIDsFromAnnotation()));
    }

    private String[] getBugIDsFromAnnotation() {
        CCTestCase qCenterAnnotation = this.getClass().getAnnotation(CCTestCase.class);
        if (qCenterAnnotation == null) {
            return new String[] {};
        } else {
            return qCenterAnnotation.bugs();
        }
    }
   
}