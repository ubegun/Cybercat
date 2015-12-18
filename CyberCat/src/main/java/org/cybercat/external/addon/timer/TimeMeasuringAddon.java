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
import org.cybercat.automation.events.EventTestFail;
import org.cybercat.external.addon.timer.Timer.Status;

public class TimeMeasuringAddon implements ExteranlAddon {

  public final static String F_TIMER = "Time measuring";
  private Date buildGuid;
  private PersistenceManager pm = null;
  private List<Timer> timers;
  private TestCaseTimers tcTimers;
  private String testGuid;
  private Map<Timer, TimerDemon> timerTasks;

  @Override
  public String[] getSupportedFeatures() {
    return new String[] { F_TIMER };
  }

  @Override
  public Collection<EventListener<?>> createListeners(TestContext config) {
    List<EventListener<?>> ls = new ArrayList<EventListener<?>>();
    if (!config.hasFeature(F_TIMER))
      return ls;

    buildGuid = config.getBuildGuid();
    testGuid = config.getTestGuid();
    timers = new ArrayList<Timer>();
    timerTasks = new HashMap<Timer, TimerDemon>();
    tcTimers = new TestCaseTimers();
    tcTimers.setBuildGuid(buildGuid);
    try {
      pm = AutomationMain.getMainFactory().getPersistenceManager();
    } catch (AutomationFrameworkException e) {
      throw new RuntimeException(e);
    }

    ls.add(new EventListener<EventTimerTask>(EventTimerTask.class, 100) {

      @Override
      public void doActon(EventTimerTask event) throws Exception {
        TimerDemon demon = new TimerDemon(event.getTimerName(), event.getTimeout());
        demon.start();
        Timer timer = new Timer(testGuid, event.getTimerName());
        timer.setTimeLabel(demon.getStartTime());
        timer.setBuildGuid(buildGuid);
        timerTasks.put(timer, demon);
      }
    });

    ls.add(new EventListener<EventStartTimer>(EventStartTimer.class, 100) {

      @Override
      public void doActon(EventStartTimer event) throws Exception {
        Timer timer = event.getTimer();
        timer.setTestGuid(testGuid);
        timer.setBuildGuid(buildGuid);
        timers.add(timer);
      }

    });

    ls.add(new EventListener<EventStopTimer>(EventStopTimer.class, 100) {

      @Override
      public void doActon(EventStopTimer event) throws Exception {
        Timer timer = new Timer(testGuid, event.getTimerName());
        if(timerTasks.containsKey(timer)){
          TimerDemon demon = timerTasks.remove(timer);
          demon.disarm();
          timer.setTimeLabel(demon.getStartTime());
          timer.setBuildGuid(buildGuid);
          timer.setDuration(System.currentTimeMillis() - timer.getTimeLabel().getTime());
          tcTimers.addTimer(timer);
          pm.save(tcTimers);
          return;
        }
        int index = -1;
        if ((index = timers.indexOf(timer)) >= 0){
          Timer t = timers.get(index);
          timers.remove(timer);
          t.setDuration(System.currentTimeMillis() - t.getTimeLabel().getTime());
          tcTimers.addTimer(t);
          pm.save(tcTimers);
          return;
        }
      }
    });

    ls.add(new EventListener<EventTestFail>(EventTestFail.class, 100){

      @Override
      public void doActon(EventTestFail event) throws Exception {
        for(Timer timer: timers){
          timer.setDuration(System.currentTimeMillis() - timer.getTimeLabel().getTime());
          timer.setStatus(Status.FAILED);
          tcTimers.addTimer(timer);
        }
        for(Timer timer :timerTasks.keySet()){
          timer.setDuration(System.currentTimeMillis() - timer.getTimeLabel().getTime());
          timer.setStatus(Status.FAILED);
          tcTimers.addTimer(timer);          
        }
        pm.save(tcTimers);
      }
      
    });
    
    return ls;
  } 

  private class TimerDemon extends Thread {

    private long startTime;
    private boolean isActive = true;
    private long timeOut;
    private String timerName;

    public TimerDemon(String timerName, long timeOut) {
      super();
      this.startTime = System.currentTimeMillis();
      this.timeOut = timeOut;
      this.timerName = timerName;
    }

    public Date getStartTime() {
      return new Date(startTime);
    }

    public void disarm() {
      this.isActive = false;
    }

    @Override
    public void run() {
      do {
        yield();
      } while (System.currentTimeMillis() - startTime < timeOut);
      if (isActive) {
        //TODO: neponatno kek budet sebja vesti - uidet li soobshenie na slack
        throw new RuntimeException("Time is out for " + timerName + " timer. Time limit = " + timeOut);
      }
    }
  }

}
