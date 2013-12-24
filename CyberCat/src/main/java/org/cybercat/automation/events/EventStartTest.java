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

import org.cybercat.automation.events.EventManager.EventTypes;
import org.cybercat.automation.test.AbstractTestCase;

public class EventStartTest extends Event {
    
    public Class<? extends AbstractTestCase> getTestClass() {
        return testClass;
    }

    private Class<? extends AbstractTestCase> testClass;
    private String description;
    private String[] bugIDs;

    public EventStartTest(Class<? extends AbstractTestCase> testClass, String description, String[] bugIDs) {
        this(testClass, description);
        this.bugIDs = bugIDs;
    }

    public EventStartTest(Class<? extends AbstractTestCase> testClass, String description){
        super();
        this.testClass = testClass;
        this.description = description;
    }

    @Override
    public EventTypes getType() {
        return EventTypes.START_TEST;
    }

    public String getDescription() {
        return description;
    }

    public String[] getBugIDs() {
        return bugIDs;
    }
}
