package org.cybercat.automation.events;

import org.cybercat.automation.addons.common.ScreenshotProvider;
import org.cybercat.automation.addons.media.events.TakeScreenshotEvent;

public class EventHighlightElement extends TakeScreenshotEvent {

    private String methodName;

    public EventHighlightElement(String methodName, ScreenshotProvider screenshotProvider, EffectType effect) {
        super(screenshotProvider, effect);
        this.methodName = methodName;
    }

    public EventHighlightElement(String methodName, ScreenshotProvider screenshotProvider, EffectType effect, int centerX, int centerY) {
        super(screenshotProvider, effect, centerX, centerY);
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

   
}
