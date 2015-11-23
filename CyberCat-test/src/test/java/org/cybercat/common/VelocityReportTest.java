package org.cybercat.common;

import org.junit.Test;
import org.testng.ISuite;
import org.testng.SuiteRunner;
import org.testng.internal.Configuration;
import org.testng.internal.IConfiguration;
import org.testng.xml.XmlSuite;

public class VelocityReportTest {

    /**
     * this method generates report from the index file of artifacts with proviso that was executed at least one run of
     * the test cases, is successfully.
     */
    //@Test
    public void reportRun_Ok() {
        XmlSuite xsuite = new XmlSuite();
        String outputDir = "/home/ubegun/cyberTest/Cybercat/Report/html";
        IConfiguration conf = new Configuration();
        ISuite suite = new SuiteRunner(conf , xsuite, outputDir);  
        //TODO:
    }

}
