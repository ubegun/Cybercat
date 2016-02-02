package org.cybercat.report.model;

import org.cybercat.automation.persistence.model.TestCase;

public class TCReport {

    private final static String prefix = "../";
    private String testGUID;
    private String qtName;
    private String images;
    private String exceptionImage;
    private String video;
    private String fullLog;
    private String shortLog;
    private String cookies;
    private STATUS testStatus;
    private Long duration = new Long(0);
    private String timeSeries;

    public enum STATUS {
        Success("alert-success"), Failed("alert-danger"), Muted("alert-warning");
        String style;

        STATUS(String style) {
            this.style = style;
        }

        public String getStyle() {
            return style;
        }

    };

    public TCReport(TestCase tc) {
        this.testGUID = tc.getTestGUID();
        if (tc.getImages() != null)
            for (String img : tc.getImages()) {
                this.images = (this.images != null ? this.images + "," : "") + prefix + img;
            }
        this.qtName = (tc.getQtName() == null? tc.getTestGUID() : tc.getQtName());
        this.exceptionImage = (tc.getExceptionImage() != null ? prefix + tc.getExceptionImage() : null);
        this.fullLog = (tc.getFullLog() != null ? prefix + tc.getFullLog() : null);
        this.shortLog = (tc.getShortLog() != null ? prefix + tc.getShortLog() : null);
        this.cookies = (tc.getCookies() != null ? prefix + tc.getCookies() : null);
        this.testStatus = STATUS.valueOf(tc.getTestStatus().toString());
        if (tc.getStopped() != null && tc.getStarted() != null) {
            this.duration = tc.getStopped().getTime() - tc.getStarted().getTime();
        }
    }

    public String getTestGUID() {
        return testGUID;
    }

    public String getExceptionImage() {
        return exceptionImage;
    }

    public String getFullLog() {
        return fullLog;
    }

    public String getShortLog() {
        return shortLog;
    }

    public STATUS getTestStatus() {
        return testStatus;
    }

    public String getImages() {
        return images;
    }

    public Long getDuration() {
        return duration;
    }

    public String getCookies() {
        return cookies;
    }

    public String getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(String timeSeries) {
        this.timeSeries = timeSeries;
    }

    public String getQtName() {
        return qtName;
    }

}
