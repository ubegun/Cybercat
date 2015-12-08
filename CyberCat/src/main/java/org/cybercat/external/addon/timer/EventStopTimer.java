package org.cybercat.external.addon.timer;

import org.cybercat.automation.events.Event;
import org.cybercat.automation.events.EventManager.EventTypes;

public class EventStopTimer extends Event {

  private String timerName;

  public EventStopTimer(String timerName) {
    super();
    this.timerName = timerName;
  }

  public String getTimerName() {
    return timerName;
  }

  @Override
  public EventTypes getType() {
    return EventTypes.TIMER_TASK;
  }

}
