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

package org.apache.automation.features;

import org.apache.automation.pages.WelcomePage;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.annotations.CCIntegrationService;
import org.cybercat.automation.annotations.CCPageObject;
import org.cybercat.automation.annotations.CCRedirectionStep;
import org.cybercat.automation.annotations.CCTestStep;
import org.cybercat.automation.test.AbstractFeature;

/**
 * @author Ubegun
 *
 */
public class ApacheHomeSampleFeature extends AbstractFeature implements IApacheHomeFeature {

    @CCPageObject
    private WelcomePage apacheWelcome;
    
    
    @CCIntegrationService
    private ISampleIntegrationService integrationService; 
    
    
    @Override
    @CCTestStep("Run integration test")
    public IApacheHomeFeature runIntegrationTest() throws AutomationFrameworkException{
        integrationService.doSomething();
        return this;
    }
    
    /* (non-Javadoc)
     * @see org.apache.automation.features.IApacheHomeFeature#sampleNavigate(java.lang.String)
     */
    @Override
    @CCTestStep("Navigation step")
    public IApacheHomeFeature sampleNavigate(String toProject) throws AutomationFrameworkException{ 
        apacheWelcome.selectProject(toProject);
        return this;        
    }

    /* (non-Javadoc)
     * @see org.apache.automation.features.IApacheHomeFeature#gotoApache()
     */
    @Override
    @CCRedirectionStep(desctiption = "Navigation to stat page", url = "http://apache.org/")
    public IApacheHomeFeature gotoApache() throws AutomationFrameworkException {
        apacheWelcome.validateTopFragment();
        return this;
    }

    /* (non-Javadoc)
     * @see org.apache.automation.features.IApacheHomeFeature#gotoAdvertise()
     */
    @Override
    @CCRedirectionStep(desctiption = "Navigation to ad. page", url = "https://www.facebook.com/af.cybercat/")
    public IApacheHomeFeature gotoAdvertise() throws AutomationFrameworkException {
        return this;
    }
    
}
