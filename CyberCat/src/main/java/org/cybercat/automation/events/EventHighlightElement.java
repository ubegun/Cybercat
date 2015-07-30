package org.cybercat.automation.events;

import org.cybercat.automation.addons.common.ScreenshotProvider;
import org.cybercat.automation.addons.media.events.TakeScreenshotEvent;

public class EventHighlightElement extends TakeScreenshotEvent {

    private String methodName;
    private Class<?> testClass;

    public EventHighlightElement(String methodName, Class<?> testClass, ScreenshotProvider screenshotProvider, EffectType effect) {
        super(screenshotProvider, effect);
        this.methodName = methodName;
        this.testClass = testClass;
    }

    public EventHighlightElement(String methodName, Class<?> testClass, ScreenshotProvider screenshotProvider, EffectType effect, int centerX, int centerY) {
        super(screenshotProvider, effect, centerX, centerY);
        this.methodName = methodName;
        this.testClass = testClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?> getTestClass() {
        return testClass;
    }
    
}
