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

import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.annotations.CCPageURL;
import org.cybercat.automation.components.AbstractPageObject;
import org.cybercat.automation.components.AbstractPageObject.PageState;
import org.cybercat.automation.events.EventPageObjectCall;
import org.cybercat.automation.test.AbstractEntryPoint;

@Aspect
public class PageObjectStateControlAspect {

    Logger log = Logger.getLogger(PageObjectStateControlAspect.class);

    private PageFactoryImpl pFactory;

    public PageObjectStateControlAspect(PageFactoryImpl pFactory) {
        this.pFactory = pFactory;
    }

    @Before("target(bean) && !@annotation(java.beans.Transient)")
    public void beforeNotTransientMethod(JoinPoint jp, Object bean) throws Throwable {
        if (bean instanceof AbstractPageObject) {
            AbstractPageObject pageObject = (AbstractPageObject) bean;
            Class<? extends AbstractEntryPoint> test = AutomationMain.getMainFactory().getConfigurationManager().getTestClass();
            AutomationMain.getEventManager().notify(new EventPageObjectCall(pageObject.getClass().getSimpleName() + "." + jp.getSignature().getName(), test));
            if (pageObject.getState() == PageState.CREATED)
                processPageURLAnnotation(pageObject);
            try{
            	pFactory.initPage(pageObject);
            }catch(Exception e){
            	log.error(e.getMessage(), e);
            	throw new AutomationFrameworkException(e.getMessage(), e);
            }
        }
    }

    private void processPageURLAnnotation(AbstractPageObject pageObject) throws Throwable {
        for (Field field : pageObject.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.getAnnotation(CCPageURL.class) != null) {
                    String pageUrl = field.get(pageObject).toString();
                    if (StringUtils.isBlank(pageUrl)) {
                        log.error("Field value is empty. Field name: " + field.getName() + " class: " + pageObject.getClass().getSimpleName() + " Thread ID:"
                                + Thread.currentThread().getId());
                        continue;
                    }
                    pageObject.setPageUrl(pageUrl);
                }
            } catch (Exception e) {
            	log.error(e.getMessage(), e);
                throw new AutomationFrameworkException("Set page url filed exception. Field value is empty." + " field name: " + field.getName() + " class: "
                        + pageObject.getClass().getSimpleName() + " Thread ID:" + Thread.currentThread().getId());
            }

        }
    }

}
