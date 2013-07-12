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

import org.apache.log4j.Logger;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.test.TestListner;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestNGListener extends TestListenerAdapter {

    private static final Logger log = Logger.getLogger(TestNGListener.class);
    private int stepCount;
    private TestListner testListner;
    
    
    public TestNGListener() {
        super();
        testListner = new TestListner();
    }

    @Override
    public void onStart(ITestContext testContext) {
        super.onStart(testContext);
        log.info("\n\n *********** Test -> " + testContext.getCurrentXmlTest().getName()+" ***********\n\n");
        stepCount = 0;
    }

    @Override
    public void onFinish(ITestContext testContext) {
        super.onFinish(testContext);
    }

    @Override
    public void onTestStart(ITestResult tr) {
        super.onTestStart(tr);
        try {
            testListner.onTestStart(tr.getTestClass().getRealClass(), tr.getMethod().getMethodName(), tr.getMethod().getDescription());
        } catch (AutomationFrameworkException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        log.info("\n\nFinished Step  #" + stepCount + " -> " + tr.getInstanceName() + "." + tr.getMethod().getMethodName()
                + ", description: \"" + tr.getMethod().getDescription() + "\"\n\n");
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        log.info("Test skipped: "+tr.getInstanceName() + "." + tr.getMethod().getMethodName()
                + ", description: \"" + tr.getMethod().getDescription());
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        log.info("\n\nTest failed: "+tr.getInstanceName() + "." + tr.getMethod().getMethodName()
                + ", description: \"" + tr.getMethod().getDescription()+"\n\n");
        onFailure(tr);
    }

    @Override
    public void onConfigurationFailure(ITestResult tr) {
        super.onConfigurationFailure(tr);
        log.info("\n\nConfiguration failed: "+tr.getInstanceName() + "." + tr.getMethod().getMethodName()
                + ", description: \"" + tr.getMethod().getDescription()+"\n\n");
        onFailure(tr);
    }

    private void onFailure(ITestResult tr) {
         try {
            testListner.onFailure(tr.getTestClass().getRealClass(), tr.getMethod().getMethodName(), tr.getThrowable());
        } catch (AutomationFrameworkException e) {
            e.printStackTrace();
        }
    }

}
