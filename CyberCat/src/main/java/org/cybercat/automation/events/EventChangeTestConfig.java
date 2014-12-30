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

import org.cybercat.automation.Configuration;
import org.cybercat.automation.events.EventManager.EventTypes;
import org.cybercat.automation.test.AbstractTestCase;


public class EventChangeTestConfig extends Event {

    private Configuration configuration;
    private AbstractTestCase thisTestCase;
    
    public EventChangeTestConfig(AbstractTestCase thisTestCase, Configuration configuration) {
        super();
        this.thisTestCase = thisTestCase;
        this.configuration = configuration;
    }

    @Override
    public EventTypes getType() {
        return EventTypes.REASSIGN_TEST_LISTENERS;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public AbstractTestCase getThisTestCase() {
        return thisTestCase;
    }
    
}
