package org.cybercat.unit.tests;

import org.cybercat.report.FreeMarkerAdapter;
import org.junit.Test;

public class FMSample {

    
    @Test
    public void testLocalReport(){
        System.setProperty("config.properties", "test.properties");
        FreeMarkerAdapter fAdapter = new FreeMarkerAdapter();
        fAdapter.generateReport(null, null, null);
    }
   
}
