/**
 * 
 */
package org.cybercat.report;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.persistence.model.TestRun;

/**
 * @author ubegun
 *
 */
public abstract class VelocityAdapter {
  
  private static final String ENCODING = "UTF-8";
  protected static final String TEMPLATE_EXTENSION = ".vm";
  
  
  private Path reportFolder;
  private VelocityContext context;
  protected ReportService reportService;

  protected void init() throws AutomationFrameworkException {
    context = new VelocityContext();
    reportService = new ReportService();
  }

  @SuppressWarnings("unchecked")
  public void execute() throws AutomationFrameworkException {
    init();
    ArrayList toRemove = new ArrayList();
    ReportIndex rIndex = reportService.getReportIndex();
    for (ReportFile rf : rIndex.getReportFiles()) {
      if (this.getReportName().equals(rf.getGroupLabel())){
        reportService.delete(rf);
        toRemove.add(rf);
      }  
    }
    rIndex.getReportFiles().removeAll(toRemove);
    // do report
    List<ReportFile> newReportFiles = createReport(reportService.getLastBuildInfo());
    rIndex.getReportFiles().addAll(newReportFiles);
    reportService.saveIndex(rIndex);
  }

  public abstract String getReportName();

  public abstract List<ReportFile> createReport(TestRun buildInfo) throws AutomationFrameworkException;

}
