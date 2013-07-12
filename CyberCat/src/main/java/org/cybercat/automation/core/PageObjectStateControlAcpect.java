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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.cybercat.automation.components.AbstractPageObject;
import org.cybercat.automation.components.AbstractPageObject.PageState;

@Aspect
public class PageObjectStateControlAcpect {

    private PageFactoryImpl pFactory;

    public PageObjectStateControlAcpect(PageFactoryImpl pFactory){
        this.pFactory = pFactory;    
    }

    public Object invoke(ProceedingJoinPoint pjp) throws Throwable {
        if (pjp.getTarget() instanceof AbstractPageObject) {
            AbstractPageObject pageObject = (AbstractPageObject) pjp.getTarget();
            if(pageObject.getState() == PageState.CREATED)
                pFactory.initPage(pageObject);
            Object result = pjp.proceed();            
            return result;
        }
        return pjp.proceed();
    }
    
    
}
