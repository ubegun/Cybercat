package org.cybercat.automation.events;

import org.cybercat.automation.events.EventManager.EventTypes;

public class EventPageObjectCall extends Event {

    private String methodName;
    private Class<?> testClass;

    public EventPageObjectCall(String methodName, Class<?> testClass) {
        super();
        this.methodName = methodName;
        this.testClass = testClass;
    }

    @Override
    public EventTypes getType() {
        return EventTypes.PAGE_OBJECT_CALL;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?> getTestClass() {
        return testClass;
    }

}
