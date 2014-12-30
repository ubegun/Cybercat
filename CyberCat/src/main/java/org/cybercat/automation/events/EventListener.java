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



public abstract class EventListener<T extends Event> {

    private Class<T> eventType;
    private int taskPriority; 

    public EventListener(Class<T> eventType, int taskPriority) {
        this.eventType = eventType;
        this.taskPriority = taskPriority;
    }

    /**
     * Returns supported event type.
     */
    public Class<T> getEventType() {
        return eventType;
    }

    public int getPriority(){
        return taskPriority;
    }
    
    /**
     * The method is handle a given event
     */
    public abstract void doActon(T event) throws Exception;

    @Override
    public String toString() {
        return "EventListener [eventType=" + eventType + ", taskPriority=" + taskPriority + "]";
    }
    
}
