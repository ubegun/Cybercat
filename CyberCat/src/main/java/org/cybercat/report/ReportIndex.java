package org.cybercat.report;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.cybercat.automation.persistence.model.Entity;

@XmlRootElement(name = "ReportIndex")
public class ReportIndex implements Entity{

  private List<ReportFile> reportFiles = new ArrayList<ReportFile>();

  public List<ReportFile> getReportFiles() {
    return reportFiles;
  }

  public void setReportFiles(List<ReportFile> reportFiles) {
    this.reportFiles = reportFiles;
  }

  @Override
  @Transient
  public void setId(long id) {
    // do nothing 
  }

  @Override
  @Transient
  public long getId() {
    return 0;
  }
}
