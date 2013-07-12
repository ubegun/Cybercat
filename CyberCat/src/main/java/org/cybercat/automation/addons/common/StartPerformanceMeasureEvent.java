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
package org.cybercat.automation.addons.common;

import org.cybercat.automation.PageFactory;
import org.cybercat.automation.events.Event;
import org.cybercat.automation.events.EventManager;


/**
 * User: Oleh_Kovalyshyn
 * Date: 4/8/13
 * Time: 4:12 PM
 */
public class StartPerformanceMeasureEvent extends Event {

    PageFactory pageFactory;

    public StartPerformanceMeasureEvent(PageFactory pageFactory) {
        this.pageFactory = pageFactory;
    }

    @Override
    public EventManager.EventTypes getType() {
        return EventManager.EventTypes.START_PERFORMANCE_MEASURE;
    }

    public PageFactory getPageFactory() {
        return pageFactory;
    }


}
