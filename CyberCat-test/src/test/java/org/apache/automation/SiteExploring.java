package org.apache.automation;

import org.apache.automation.features.IApacheHomeFeature;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.addons.common.ScreenshotManager;
import org.cybercat.automation.addons.common.TestLoggerAddon;
import org.cybercat.automation.addons.media.MediaController;
import org.cybercat.automation.annotations.CCFeature;
import org.cybercat.automation.annotations.CCTestCase;
import org.cybercat.automation.core.AutomationMain;
import org.cybercat.automation.core.EventManagerImpl;
import org.cybercat.automation.testng.TestNGTestCase;
import org.cybercat.external.addon.timer.EventStartTimer;
import org.cybercat.external.addon.timer.EventStopTimer;
import org.cybercat.external.addon.timer.TimeMeasuringAddon;
import org.testng.annotations.Test;

@CCTestCase(description="Apache sample test" , features={TimeMeasuringAddon.F_TIMER, ScreenshotManager.EXCEPTION_SCREENSHOT, TestLoggerAddon.FULL_LOG})
public class SiteExploring extends TestNGTestCase {

    
    @CCFeature
    private IApacheHomeFeature sampleFeature;
    
    @Test
    public void navigateToTomcatProject() throws AutomationFrameworkException {
        AutomationMain.getEventManager().notify(new EventStartTimer("Time consuming for site exploring."));
        sampleFeature
            .runIntegrationTest()
            .gotoAdvertise()
            .gotoApache()
            .sampleNavigate("Tomcat");
        AutomationMain.getEventManager().notify(new EventStopTimer("Time consuming for site exploring."));
    }

    /* (non-Javadoc)
     * @see org.cybercat.automation.test.AbsractTestCase#setup()
     */
    @Override
    public void setup() throws AutomationFrameworkException {
        // TODO Auto-generated method stub
    }

}
