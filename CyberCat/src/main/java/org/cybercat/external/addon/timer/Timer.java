package org.cybercat.external.addon.timer;

import java.util.Date;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Timer")
public class Timer {

    public static enum Status {
        STOPPED, FAILED, TIME_IS_UP
    }

    private Date buildGuid;
    private String testGuid;
    private String name;
    private Date timeLabel = new Date();
    private long duration;
    private Status status = Status.STOPPED;

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
