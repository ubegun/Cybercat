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

package org.cybercat.automation.core;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.annotations.CCRedirectionStep;
import org.cybercat.automation.annotations.CCTestStep;
import org.cybercat.automation.events.EventStartTestStep;

/**
 * @author Ubegun
 *
 */
@Aspect
public class TestStepAspect {
    
    public TestStepAspect(){
        super();
    }

    @Before("target(bean) && @annotation(testStep)")
    public void stepNotification(JoinPoint jp, Object bean,  CCTestStep testStep) throws AutomationFrameworkException{
        AutomationMain.getEventManager().notify(new EventStartTestStep(bean.getClass(),testStep.value(), jp.getSignature().getName())); 
    }


    @Before("target(bean) && @annotation(redirectionStep)")
    public void redirectionstep(JoinPoint jp, Object bean,  CCRedirectionStep redirectionStep) throws AutomationFrameworkException{
        Browser.getCurrentBrowser().get(redirectionStep.url());
        AutomationMain.getEventManager().notify(new EventStartTestStep(bean.getClass(),redirectionStep.desctiption(), jp.getSignature().getName())); 
    }

}
