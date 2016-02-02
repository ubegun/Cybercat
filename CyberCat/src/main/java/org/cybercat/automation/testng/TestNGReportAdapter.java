package org.cybercat.automation.testng;

import java.util.List;

import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.persistence.model.TestRun;
import org.cybercat.report.VelocityAdapter;
import org.cybercat.report.model.ReportFile;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

public abstract class TestNGReportAdapter extends VelocityAdapter implements IReporter {

  @Override
  public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
     try {
      super.execute();
    } catch (AutomationFrameworkException e) {
      throw new RuntimeException(e);
    }
  }

}
