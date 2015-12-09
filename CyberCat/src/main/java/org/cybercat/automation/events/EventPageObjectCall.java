package org.cybercat.automation.events;

import org.cybercat.automation.events.EventManager.EventTypes;

public class EventPageObjectCall extends Event {

    private String methodName;

    public EventPageObjectCall(String methodName) {
        super();
        this.methodName = methodName;
    }

    @Override
    public EventTypes getType() {
        return EventTypes.PAGE_OBJECT_CALL;
    }

    public String getMethodName() {
        return methodName;
    }


}
