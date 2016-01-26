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

package org.cybercat.automation.events;

import java.util.Date;

import org.cybercat.automation.events.EventManager.EventTypes;

/**
 * @author Ubegun
 *
 */
public class EventTestFail extends Event {

    private Class<?> testClass;
    private String methodName;
    private Throwable exception;
    private Date stopped;
    
    public EventTestFail(Class<?> testClass, String methodName, Throwable exception) {
        super();
        this.testClass = testClass;
        this.methodName = methodName;
        this.exception = exception;
    }

    /* (non-Javadoc)
     * @see org.cybercat.automation.events.Event#getType()
     */
    @Override
    public EventTypes getType() {
        return EventTypes.TEST_FAIL;
    }

    public Class<?> getTestClass() {
        return testClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public Throwable getException() {
        return exception;
    }

    public Date getStopped() {
        return stopped;
    }
    
}
