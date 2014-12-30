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
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.annotations.CCDataProvider;

@Aspect
public class DataProviderAspect {

    public Object applyData(ProceedingJoinPoint pjp) throws AutomationFrameworkException {
        String methodName = pjp.getSignature().getName();
        Method[] methods = pjp.getTarget().getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(methodName)) {
                System.out.println("found");
                try {
                    Object[] arg = createData(methods[i], pjp.getArgs());
                    return pjp.proceed(arg);
                } catch (Throwable e) {
                    throw new PageObjectException(e);
                }
            }
        }
        return null;
    }

    private Object[] createData(Method method, Object[] args) throws AutomationFrameworkException {
        CCDataProvider provider = method.getAnnotation(CCDataProvider.class);
        DataWorker worker = AutomationMain.getMainFactory().getDataProvider(provider.dataSource());
        return worker.getData(provider.actionType(), method, args);
    }

}
