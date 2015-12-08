/**
 * 
 */
package org.cybercat.external.addon.timer;

import org.cybercat.automation.events.Event;
import org.cybercat.automation.events.EventManager.EventTypes;

/**
 * @author ubegun
 *
 */
public class EventStartTimer extends Event {


  private Timer timer;

  public EventStartTimer(String timerName) {
    super();
    this.timer = new Timer(timerName);
  }

  @Override
  public EventTypes getType() {
    return EventTypes.TIMER_TASK;
  }

  public Timer getTimer() {
    return timer;
  }
  
}
