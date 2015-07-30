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

public interface EventManager {

  public enum EventTypes {
    START_STREAM,
    STOP_STREAM,
    UPDATE_STREAM,
    ADD_SCREENSHOT,
    START_TEST,
    STOP_TEST,
    CLOSE_MEDIA_DEMON_THREAD,
    REASSIGN_TEST_LISTENERS,
    MAKE_SCREENSHOT,
    NEXT_TEST_STEP,
    EXCEPTION,
    ADD_SUBTITLE,
    INTERRUPT_VIDEO,
    GET_PERFORMANCE_REPORT,
    START_PERFORMANCE_MEASURE,
    TEST_FAIL,
    INIT_PROXY_SERVER,
    PAGE_OBJECT_CALL,
    
  }

  public <T extends Event> void notify(T event);
  
  public boolean unsubscribe(EventListener<?> listener);
}