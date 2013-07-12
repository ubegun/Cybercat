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

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.annotations.CCDataProvider;
import org.cybercat.automation.soap.SoapService;
import org.cybercat.automation.soap.SoapSession;

@Aspect
public class SoapServicesAspect {

    public Object invoke(ProceedingJoinPoint pjp) throws Throwable {
        if (pjp.getTarget() instanceof SoapService) {
            SoapService service = (SoapService) pjp.getTarget();
            SoapSession session = service.getSoapSession();
            //set Session 
            session.putCookieSnapshot();
            Object result = pjp.proceed(this.before(this.getMethod(pjp), pjp.getArgs(), pjp.getTarget()));
            //get Session
            session.makeCookieSnapshot();
            return result;
        } else {
            return pjp.proceed();
        }
    }

    private Method getMethod(ProceedingJoinPoint pjp) {
        String methodName = pjp.getSignature().getName();
        Method[] methods = pjp.getTarget().getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(methodName)) {
                return methods[i];
            }
        }
        return null;
    }

    public Object[] before(Method m, Object[] args, Object target) throws Throwable {
        try {
            CCDataProvider provider = m.getAnnotation(CCDataProvider.class);
            if (provider == null) {
                return args;
            }

            DataWorker worker = AutomationMain.getMainFactory().getDataProvider(provider.dataSource());
            return worker.getData(provider.actionType(), m, args);

        } catch (Throwable e) {
            throw new PageObjectException(e);
        }
    }

}
