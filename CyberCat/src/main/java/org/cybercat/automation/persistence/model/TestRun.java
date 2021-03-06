package org.cybercat.automation.persistence.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.annotation.XmlType;

import org.cybercat.automation.persistence.model.TestCase.STATUS;
import org.cybercat.automation.utils.CommonUtils;

@XmlType(name = "TestRun")
public class TestRun implements Comparable<TestRun> {

    private final static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        
    private List<TestCase> tests = new CopyOnWriteArrayList<TestCase>();
    private Date started = new Date(); // Using like build GUID
    private Date completed;
    private String htmlReport;
    private STATUS testStatus = STATUS.Muted;
    private Date deleted;

    public List<TestCase> getTests() {
        return tests;
    }

    public void setTests(List<TestCase> tests) {
        this.tests = tests;
    }

    public String getStartedAsLabel() {
        return formatter.format(started);
    }
    
    public String getStartedAsFileName() {
        return CommonUtils.dateToString(started);
    }
    
    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getCompleted() {
        return completed;
    }

    public void setCompleted(Date completed) {
        this.completed = completed;
    }

    @Override
    public int compareTo(TestRun o) {
        if (o == null)
            throw new NullPointerException();
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        TestRun testRun = (TestRun) o;
        if (this.started.before(testRun.started))
            return BEFORE;
        if (this.started.after(testRun.started))
            return AFTER;
        return EQUAL;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public void setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
    }

    public STATUS getTestStatus() {
      return testStatus;
    }

    public void setTestStatus(STATUS testStatus) {
      this.testStatus = testStatus;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((started == null) ? 0 : started.hashCode());
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
        TestRun other = (TestRun) obj;
        if (started == null) {
            if (other.started != null)
                return false;
        } else if (!started.equals(other.started))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TestRun [tests=" + tests + ", started=" + started + ", completed=" + completed + ",status=" + testStatus + " , htmlReport="
                + htmlReport + "]";
    }

    /**
     * @return the deleted
     */
    public Date getDeleted() {
        return deleted;
    }

    /**
     * @param deleted the deleted to set
     */
    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

}
