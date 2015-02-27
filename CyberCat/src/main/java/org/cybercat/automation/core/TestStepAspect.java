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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.addons.common.logging.provider.HtmlLogHelper;
import org.cybercat.automation.addons.common.logging.provider.LogLevel;
import org.cybercat.automation.annotations.CCRedirectionStep;
import org.cybercat.automation.annotations.CCTestStep;
import org.cybercat.automation.events.EventStartTestStep;
import org.cybercat.automation.test.AbstractFeature;
import org.cybercat.automation.test.AbstractTestCase;

/**
 * @author Ubegun
 *
 */
@Aspect
public class TestStepAspect {

    private Logger log = LogManager.getLogger(TestStepAspect.class);
    
    public TestStepAspect(){
        super();
    }

    @SuppressWarnings("unchecked")
    @Before("target(bean) && @annotation(testStep)")
    public void stepNotification(JoinPoint jp, Object bean,  CCTestStep testStep) throws AutomationFrameworkException{
        Class<? extends AbstractTestCase> test = AutomationMain.getMainFactory().getConfigurationManager().getTestClass();
        AutomationMain.getEventManager().notify(new EventStartTestStep(test, (Class<? extends AbstractFeature>) bean.getClass() ,testStep.value(), jp.getSignature().getName()));
        log.log(LogLevel.STEP_START, HtmlLogHelper.makeBold("TEST STEP STARTED: ") + HtmlLogHelper.makeUnderline(" Name: ") + jp.getSignature().getName()
                + HtmlLogHelper.makeUnderline("; Description:") + testStep.value());
    }


    @SuppressWarnings("unchecked")
    @Before("target(bean) && @annotation(redirectionStep)")
    public void redirectionstep(JoinPoint jp, Object bean,  CCRedirectionStep redirectionStep) throws AutomationFrameworkException{
        Class<? extends AbstractTestCase> test = AutomationMain.getMainFactory().getConfigurationManager().getTestClass();
        Browser.getCurrentBrowser().get(redirectionStep.url());
        AutomationMain.getEventManager().notify(new EventStartTestStep(test ,(Class<? extends AbstractFeature>) bean.getClass(),redirectionStep.desctiption(), jp.getSignature().getName()));
        log.log(LogLevel.STEP_START, HtmlLogHelper.makeBold("TEST STEP STARTED: ") + HtmlLogHelper.makeUnderline(" Name: ") + jp.getSignature().getName() + " [redirection step]"
                + HtmlLogHelper.makeUnderline("; Description: ") + redirectionStep.desctiption()+"; URL: "+redirectionStep.url());

    }

}
