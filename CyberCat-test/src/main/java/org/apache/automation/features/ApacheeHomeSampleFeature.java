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
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.annotations.CCPageObject;
import org.cybercat.automation.annotations.CCTestStep;
import org.cybercat.automation.test.AbstractFeature;

/**
 * @author Ubegun
 *
 */
public class ApacheeHomeSampleFeature extends AbstractFeature {

    @CCPageObject
    private WelcomePage apacheWelcome;
    
    @CCTestStep("Navigation step")
    public ApacheeHomeSampleFeature sampleNavigate(String toProject) throws PageObjectException{ 
        apacheWelcome.validateTopFragment();
        apacheWelcome.selectProject(toProject);
        return this;
    }
    
}
