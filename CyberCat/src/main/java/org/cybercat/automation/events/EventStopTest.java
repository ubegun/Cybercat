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


public class EventStopTest extends Event {
    
    private Date stopped = new Date();
    private String fileName;
    private String dirName;

    public EventStopTest(String fileName, String dirName){
        super();
        this.fileName = fileName;
        this.dirName = dirName;
    }

    @Override
    public EventTypes getType() {
        return EventTypes.STOP_TEST;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public String getDirName() {
        return dirName;
    }

    public Date getStopped() {
        return stopped;
    }
}
