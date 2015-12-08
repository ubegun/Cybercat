package org.cybercat.external.addon.timer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.PersistenceManager;
import org.cybercat.automation.TestContext;
import org.cybercat.automation.addons.ExteranlAddon;
import org.cybercat.automation.core.AutomationMain;
import org.cybercat.automation.events.EventListener;

public class TimeMeasuringAddon implements ExteranlAddon{

  public final static String F_TIMER = "TIME_MEASURING";
  private Date buildGUID;
  private PersistenceManager pm = null;
  private Map<String, Timer> timers;
  private TestCaseTimers tcTimers;
  private String testGuid;
  
  @Override
  public String[] getSupportedFeatures() {
    return new String[] {F_TIMER};
  }

  @Override
  public Collection<EventListener<?>> createListeners(TestContext config){
    List<EventListener<?>> ls = new ArrayList<EventListener<?>>();
    if(!config.hasFeature(F_TIMER))
      return ls;
    
    buildGUID = config.getBuildGuid();
    testGuid = config.getTestGuid();
    timers = new HashMap<String, Timer>();
    tcTimers = new TestCaseTimers();
    try {
      pm = AutomationMain.getMainFactory().getPersistenceManager();
    } catch (AutomationFrameworkException e) {
      throw new RuntimeException(e);
    }
    
    
    
    ls.add(new EventListener<EventStartTimer>(EventStartTimer.class, 100) {

      @Override
      public void doActon(EventStartTimer event) throws Exception {
        Timer timer = event.getTimer();
        timer.setTestGuid(testGuid);
        timers.put(timer.getName(), timer);
      }
      
    });
    ls.add(new EventListener<EventStopTimer>(EventStopTimer.class , 100){

      @Override
      public void doActon(EventStopTimer event) throws Exception {
        Timer timer = timers.get(event.getTimerName());
        timers.remove(timer);
        long duration = System.currentTimeMillis() - timer.getTimeLabel().getTime();
        timer.setDuration(duration);
        tcTimers.addTimer(timer);
        pm.save(tcTimers);
      }
      
    });
    
    return ls;
  }

}
