package org.cybercat.unit.tests;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cybercat.automation.persistence.model.TestCase;
import org.cybercat.automation.persistence.model.TestRun;
import org.cybercat.report.FreeMarkerAdapter;
import org.junit.Test;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class FMSample {

    
    @Test
    public void testLocalReport(){
        System.setProperty("config.properties", "test.properties");
        FreeMarkerAdapter fAdapter = new FreeMarkerAdapter();
        fAdapter.generateReport(null, null, null);
    }
    
    
    //@Test
    public void testCreateSample() throws IOException, URISyntaxException, TemplateException{
        
        String[] builds = new String[]{"01-01", "01-02", "01-03", "01-04"};
        List<String> lBuilds = Arrays.asList(builds);
        
        /* ------------------------------------------------------------------------ */    
        /* You should do this ONLY ONCE in the whole application life-cycle:        */    
    
        /* Create and adjust the configuration singleton */
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        URL template = ClassLoader.getSystemResource("FMReport/");
        cfg.setDirectoryForTemplateLoading(new File(template.toURI()));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);

        
        /* ------------------------------------------------------------------------ */    
        /* You usually do these for MULTIPLE TIMES in the application life-cycle:   */    
        List<TestRun> tr = new ArrayList<TestRun>();
        List<TestCase> tc = new ArrayList<TestCase>();
        
        TestCase tCase = new TestCase("test guid");
        tc.add(tCase);
        
        
        TestRun test1 = new TestRun();
        test1.setStarted(new Date());
        
        test1.setTests(tc);
        tr.add(test1);
        
        /* Create a data-model */
        Map root = new HashMap();
        root.put("builds", lBuilds);
        root.put("testRuns", tr);
        Map latest = new HashMap();
        root.put("latestProduct", latest);
        latest.put("url", "products/greenmouse.html");
        latest.put("name", "green mouse");

        /* Get the template (uses cache internally) */
        Template temp = cfg.getTemplate("sample.html.fmt");

        /* Merge data-model with template */
        Writer out = new OutputStreamWriter(System.out);
        temp.process(root, out);
        // Note: Depending on what `out` is, you may need to call `out.close()`.
        // This is usually the case for file output, but not for servlet output.
    }
    
    
   
}
