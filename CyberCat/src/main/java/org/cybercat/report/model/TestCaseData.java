package org.cybercat.report.model;

import java.util.List;

public class TestCaseData {

    int numberOfSeries;
    //String testNameId
    List<TimeSeries> series;
    List<RowData> rowDatas;
    
    public TestCaseData(int numberOfSeries, List<TimeSeries> series, List<RowData> rowDatas) {
        super();
        this.numberOfSeries = numberOfSeries;
        this.series = series;
        this.rowDatas = rowDatas;
    }

    public int getNumberOfSeries() {
        return numberOfSeries;
    }

    public void setNumberOfSeries(int numberOfSeries) {
        this.numberOfSeries = numberOfSeries;
    }

    public List<TimeSeries> getSeries() {
        return series;
    }

    public void setSeries(List<TimeSeries> series) {
        this.series = series;
    }

    public List<RowData> getRowDatas() {
        return rowDatas;
    }

    public void setRowDatas(List<RowData> rowDatas) {
        this.rowDatas = rowDatas;
    }
    
}
