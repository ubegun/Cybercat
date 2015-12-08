package org.cybercat.external.addon.timer;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.cybercat.automation.persistence.model.Entity;

@XmlRootElement(name = "TestCaseTimers")
public class TestCaseTimers implements Entity{

  private Date buildGuid;
  private List<Timer> timers = new ArrayList<Timer>();
  
  public Date getBuildGuid() {
    return buildGuid;
  }

  public void setBuildGuid(Date buildGuid) {
    this.buildGuid = buildGuid;
  }

  @Transient
  @Override
  public void setId(long id) {
    //nothing 
  }
  
  @Transient
  @Override
  public long getId() {
    return buildGuid.getTime();
  }

  public List<Timer> getTimers() {
    return timers;
  }

  public void setTimers(List<Timer> timers) {
    this.timers = timers;
  }
  
  @Transient
  public void addTimer(Timer timer){
    timers.add(timer);
  }
}
