package org.cybercat.external.addon.slack;

import org.cybercat.automation.events.Event;
import org.cybercat.automation.events.EventManager.EventTypes;

public class EventPostMessage extends Event {

  private String message;
  private String chennalName; // can be null

  public EventPostMessage() {
    super();
  }

  public EventPostMessage(String message, String chennalName) {
    super();
    this.message = message;
    this.chennalName = chennalName;
  }

  public EventPostMessage(String message) {
    super();
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public String getChennalName() {
    return chennalName;
  }

  @Override
  public EventTypes getType() {
    return EventTypes.EXTERNAL_EVENT;
  }

}
