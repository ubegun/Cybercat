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

package org.cybercat.automation.core.integration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author Ubegun
 * 
 */

@Aspect
public class IntegrationServiceAspect {

    private SessionManager sManager;
    private boolean hasSession;

    public IntegrationServiceAspect(boolean hasSession) {
        super();
        this.hasSession = hasSession; 
        this.sManager = new SessionManager();
    }

    public Object invoke(ProceedingJoinPoint pjp) throws Throwable {
        if (pjp.getTarget() instanceof IIntegrationService && hasSession) {
            sManager.putCookieSnapshot();
            Object result = pjp.proceed();
            sManager.makeCookieSnapshot();
            return result;
        } else {
            return pjp.proceed();
        }
    }

}
