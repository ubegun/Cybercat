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

package org.cybercat.automation.testng;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.core.AutomationMain;
import org.cybercat.automation.events.EventTestFail;
import org.cybercat.automation.persistence.TestArtifactManager;
import org.cybercat.automation.persistence.model.TestCase;
import org.cybercat.automation.persistence.model.TestCase.STATUS;
import org.cybercat.automation.test.AbstractEntryPoint;
import org.cybercat.automation.utils.CommonUtils;
import org.cybercat.automation.utils.WorkFolder;

/**
 * @author Ubegun
 *
 */
public class TestListner {

    private static final Logger log = Logger.getLogger(TestListner.class);
    
    public void onTestStart(Class<? extends AbstractEntryPoint> testClass, String methodName, String testDescription) throws AutomationFrameworkException {
        log.info("\n\n##### " + testClass.getName()+ " test, " + methodName+ "method with description: \"" + testDescription + "\" has been started ######\n\n");
    }
    
    public void onFailure(Class<?> testClass, String method, Throwable error) throws AutomationFrameworkException{
        AutomationMain.getEventManager().notify(new EventTestFail(testClass, method, error));
        
        this.createLogOnError(testClass, method, error);
    }
    
    private void createLogOnError(Class<?> testClass, String method, Throwable error) {
        Path shortLog = Paths.get(WorkFolder.Screenshots.getPath().toString(),  CommonUtils.getCurrentDate() +  ".log");
        try {
            BufferedWriter writer = Files.newBufferedWriter(shortLog, Charset.defaultCharset());
            StackTraceElement[] stackTrace = error.getStackTrace();
            writer.write(error.getMessage() + "\n");
            for (int i = 0; i < stackTrace.length; i++) {
                writer.write(stackTrace[i].toString() + "\n\t");
            }
            writer.close();
            TestCase test = new TestCase(testClass.getName());
            test.setShortLog(shortLog.toString());

            //Update test status 
            TestArtifactManager.getInstance().getThisTestRun().setTestStatus(STATUS.Failed);
            TestArtifactManager.updateTestRunInfo(test);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
}