package org.cybercat.report.model;

public class TimeSeries {

    private String testId;
    private String name;
    private int index = 0;

    public TimeSeries(int index, String testId, String name) {
        this.index = index;
        this.testId = testId;
        this.name = name;
    }

    public TimeSeries() {
        super();
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
