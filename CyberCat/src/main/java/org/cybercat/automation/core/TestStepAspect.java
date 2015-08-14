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
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.annotations.CCRedirectionStep;
import org.cybercat.automation.annotations.CCTestStep;
import org.cybercat.automation.events.EventStartTestStep;
import org.cybercat.automation.events.EventStopTestStep;
import org.cybercat.automation.test.AbstractFeature;
import org.cybercat.automation.test.AbstractTestCase;

/**
 * @author Ubegun
 *
 */
@Aspect
public class TestStepAspect {

    public TestStepAspect() {
        super();
    }

    @SuppressWarnings("unchecked")
    @Around("target(bean) && @annotation(testStep)")
    public Object stepNotification(ProceedingJoinPoint pjp, Object bean, CCTestStep testStep) throws AutomationFrameworkException {
        Class<? extends AbstractTestCase> test = AutomationMain.getMainFactory().getConfigurationManager().getTestClass();
        AutomationMain.getEventManager().notify(
                new EventStartTestStep(test, (Class<? extends AbstractFeature>) bean.getClass(), testStep.value(), pjp.getSignature().getName()));
        try {
            Object retVal = pjp.proceed();
            AutomationMain.getEventManager().notify(
                    new EventStopTestStep(test, (Class<? extends AbstractFeature>) bean.getClass(), testStep.value(), pjp.getSignature().getName()));
            return retVal;
        } catch (Throwable e) {
            throw new AutomationFrameworkException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Before("target(bean) && @annotation(redirectionStep)")
    public void redirectionstep(JoinPoint jp, Object bean, CCRedirectionStep redirectionStep) throws AutomationFrameworkException {
        Class<? extends AbstractTestCase> test = AutomationMain.getMainFactory().getConfigurationManager().getTestClass();
        Browser.getCurrentBrowser().get(redirectionStep.url());
        AutomationMain.getEventManager().notify(
                new EventStartTestStep(test, (Class<? extends AbstractFeature>) bean.getClass(), redirectionStep.desctiption(), jp.getSignature().getName()));
    }

}
