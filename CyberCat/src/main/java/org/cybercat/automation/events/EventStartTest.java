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
<<<<<<< HEAD

public class EventStartTest extends Event {
    
    private String description;
    private String[] bugIDs;

    public EventStartTest(String description, String[] bugIDs) {
        this(description);
        this.bugIDs = bugIDs;
    }

    public EventStartTest(String description){
=======
import org.cybercat.automation.test.AbstractEntryPoint;

public class EventStartTest extends Event {
    
    public Class<? extends AbstractEntryPoint> getTestClass() {
        return testClass;
    }

    private Class<? extends AbstractEntryPoint> testClass;
    private String description;
    private String[] bugIDs;

    public EventStartTest(Class<? extends AbstractEntryPoint> testClass, String description, String[] bugIDs) {
        this(testClass, description);
        this.bugIDs = bugIDs;
    }

    public EventStartTest(Class<? extends AbstractEntryPoint> testClass, String description){
>>>>>>> 9791950d3370025567af91ddfddda2be34151956
        super();
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
