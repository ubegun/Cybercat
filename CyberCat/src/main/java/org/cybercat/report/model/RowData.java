package org.cybercat.report.model;

public class RowData {
    
    private Long timeLabel;
    private long[] series;
    
    public RowData(long timeLabel, long[] series) {
        super();
        this.timeLabel = timeLabel;
        this.series = series;
    }

    public Long getTimeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(long timeLabel) {
        this.timeLabel = timeLabel;
    }

    public long[] getSeries() {
        return series;
    }

    public void setSeries(long[] series) {
        this.series = series;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((timeLabel == null) ? 0 : timeLabel.hashCode());
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
        RowData other = (RowData) obj;
        if (timeLabel == null) {
            if (other.timeLabel != null)
                return false;
        } else if (!timeLabel.equals(other.timeLabel))
            return false;
        return true;
    }
    
}
