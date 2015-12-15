package org.cybercat.external.addon.timer;

import org.cybercat.automation.events.Event;
import org.cybercat.automation.events.EventManager.EventTypes;

public class EventTimerTask extends Event {

  private long timeout; // ms 
  private String timerName;

  public EventTimerTask(long timeout, String timerName) {
    super();
    this.timeout = timeout;
    this.timerName = timerName;
  }

  @Override
  public EventTypes getType() {
    return EventTypes.TIMER_TASK;
  }

  public long getTimeout() {
    return timeout;
  }

  public String getTimerName() {
    return timerName;
  }

}
