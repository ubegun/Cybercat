package org.cybercat.external.addon.timer;

import java.util.Date;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Timer")
public class Timer {

  private Date buildGuid;
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

  public Timer(String testGuid, String name) {
    super();
    this.testGuid = testGuid;
    this.name = name;
  }

  public Date getBuildGuid() {
    return buildGuid;
  }

  public void setBuildGuid(Date buildGuid) {
    this.buildGuid = buildGuid;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((testGuid == null) ? 0 : testGuid.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Timer other = (Timer) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (testGuid == null) {
      if (other.testGuid != null)
        return false;
    } else if (!testGuid.equals(other.testGuid))
      return false;
    return true;
  }
  
}
