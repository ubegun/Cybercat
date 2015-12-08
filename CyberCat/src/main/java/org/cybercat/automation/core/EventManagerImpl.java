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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.cybercat.automation.TestContext;
import org.cybercat.automation.events.EventChangeTestConfig;
import org.cybercat.automation.events.Event;
import org.cybercat.automation.events.EventListener;
import org.cybercat.automation.events.EventManager;

@SuppressWarnings("rawtypes")
public class EventManagerImpl implements EventManager, AddonContainer {

  private static Logger log = Logger.getLogger(EventManagerImpl.class);

  private Map<Class<?>, LinkedList<EventListener>> listeners;
  private TestContext configuration;

  public EventManagerImpl() {
    configuration = new TestContext();
    listeners = new HashMap<Class<?>, LinkedList<EventListener>>();
  }

  public synchronized void setupListeners(List<AddonContainer> holders) {
    for (AddonContainer holder : holders) {
      if (holder != null)
        setupListener(holder);
    }
  }

  public synchronized void setupListener(AddonContainer holder) {
    try {
      Collection<EventListener<?>> listeners = holder.createListeners(configuration);
      for (EventListener<?> listener : listeners) {
        addListener(listener);
      }
    } catch (Exception e) {
      log.error(holder.getClass().getSimpleName() + " initialisation failed.", e);
    }
  }

  @Override
  public <T extends Event> void notify(T event) {
    System.err.println("local event :" + event.getClass().getSimpleName());
    if (!listeners.containsKey(event.getClass()) || Thread.currentThread().getId() != event.getThreadId()) {
      log.info(event.getClass().getSimpleName() + "[DEBUG] event has been rejected.  Current thread id:" + Thread.currentThread().getId()
          + "\t Event thread id:" + event.getThreadId());
      return;
    }

    Collection<EventListener> cListeners = listeners.get(event.getClass());
    for (EventListener<T> listener : cListeners) {
      try {
        listener.doActon(event);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  protected EventListener addListener(EventListener listener) {
    LinkedList<EventListener> cListeners = listeners.get(listener.getEventType());
    if (cListeners == null)
      cListeners = new LinkedList<EventListener>();
    int priority = listener.getPriority();
    int index = 0;
    for (EventListener cListener : cListeners) {
      if (cListener.getPriority() >= priority) {
        continue;
      }
      index++;
    }
    cListeners.add(index, listener);
    listeners.put(listener.getEventType(), cListeners);
    return listener;
  }

  public void release() {
    listeners = new HashMap<Class<?>, LinkedList<EventListener>>();
  }

  @Override
  public boolean unsubscribe(EventListener<?> listener) {
    boolean result = false;
    for (Entry<Class<?>, LinkedList<EventListener>> entry : listeners.entrySet()) {
      result = result || entry.getValue().remove(listener);
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.cybercat.automation.events.AddonContainer#getSupportedFeatures()
   */
  @Override
  public String[] getSupportedFeatures() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.cybercat.automation.events.AddonContainer#createListeners(org.cybercat.automation.Configuration)
   */
  @Override
  public Collection<EventListener<?>> createListeners(TestContext config) {
    ArrayList<EventListener<?>> listeners = new ArrayList<EventListener<?>>();
    listeners.add(new EventListener<EventChangeTestConfig>(EventChangeTestConfig.class, 0) {

      @Override
      public void doActon(EventChangeTestConfig event) throws Exception {
        configuration = event.getConfiguration();
      }

    });
    return listeners;
  }
}
