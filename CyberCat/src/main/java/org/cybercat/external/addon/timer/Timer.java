package org.cybercat.external.addon.timer;

import java.util.Date;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Timer")
public class Timer {

  private String testGuid;
  private String name;
  private Date timeLabel = new Date();
  private long duration;
  
  public Timer() {
    super();
  }

  public Timer(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Date getTimeLabel() {
    return timeLabel;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setTimeLabel(Date timeLabel) {
    this.timeLabel = timeLabel;
  }

  public String getTestGuid() {
    return testGuid;
  }

  public void setTestGuid(String testGuid) {
    this.testGuid = testGuid;
  }

  public Timer(String testGuid, Date timeLabel) {
    super();
    this.testGuid = testGuid;
    this.timeLabel = timeLabel;
  }

  
}
