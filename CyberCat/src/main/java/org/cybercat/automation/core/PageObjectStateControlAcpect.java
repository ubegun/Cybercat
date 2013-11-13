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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.cybercat.automation.annotations.CCPageURL;
import org.cybercat.automation.components.AbstractPageObject;
import org.cybercat.automation.components.AbstractPageObject.PageState;

@Aspect
public class PageObjectStateControlAcpect {

    private PageFactoryImpl pFactory;

    public PageObjectStateControlAcpect(PageFactoryImpl pFactory){
        this.pFactory = pFactory;    
    }

    @Before("target(bean) && !@annotation(java.beans.Transient)")
    public void beforeNotTransientMethod(JoinPoint jp, Object bean) throws Throwable {
        if (bean instanceof AbstractPageObject) {
            AbstractPageObject pageObject = (AbstractPageObject) bean;
            if(pageObject.getState() == PageState.CREATED)
                processPageURLAnnotation(pageObject);
                pFactory.initPage(pageObject);
        }
    }
    
    private void processPageURLAnnotation(AbstractPageObject pageObject) throws Throwable{        
        Field[] fields = pageObject.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            if (fields[i].getAnnotation(CCPageURL.class) != null) {
                String pageUrl = fields[i].get(pageObject).toString();
                pageObject.setPageUrl(pageUrl);
            }
        }
    }
    
}
