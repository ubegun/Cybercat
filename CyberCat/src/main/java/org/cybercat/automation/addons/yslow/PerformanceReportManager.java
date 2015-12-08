/**Copyright 2013 The Cybercat project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cybercat.automation.addons.yslow;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.TestContext;
import org.cybercat.automation.PageFactory;
import org.cybercat.automation.components.AbstractPageObject;
import org.cybercat.automation.components.Button;
import org.cybercat.automation.components.PageElement;
import org.cybercat.automation.components.StatefulElement;
import org.cybercat.automation.components.TextContainer;
import org.cybercat.automation.core.AddonContainer;
import org.cybercat.automation.core.AutomationMain;
import org.cybercat.automation.core.Browser;
import org.cybercat.automation.events.EventListener;
import org.cybercat.automation.events.EventStartTest;
import org.cybercat.automation.persistence.TestArtifactManager;
import org.cybercat.automation.persistence.model.TestCase;
import org.cybercat.automation.utils.WorkFolder;

/**
 * User: Oleh_Kovalyshyn Date: 4/8/13 Time: 3:03 PM
 */
public class PerformanceReportManager implements AddonContainer {

    private static final Logger log = Logger.getLogger(PerformanceReportManager.class);
    public final static String PERFOMANCE_REPORT = "Perfomance report";
    private YSlow ySlow;
    private String testGuid;

    @Override
    public Collection<EventListener<?>> createListeners(TestContext config) {
        if (!config.hasFeature(PERFOMANCE_REPORT)) {
            return new ArrayList<EventListener<?>>();
        }
        testGuid = config.getTestGuid();
        ArrayList<EventListener<?>> listeners = new ArrayList<EventListener<?>>();

        listeners.add(new EventListener<GetPerformanceReportEvent>(GetPerformanceReportEvent.class, 100) {

            @Override
            public void doActon(GetPerformanceReportEvent event) throws Exception {
                log.debug("Performance measuring finished");
                PageFactory pageFactory = AutomationMain.getMainFactory().getPageFactory();
                init();
                Browser.getCurrentBrowser().switchToFrame("YSLOW-bookmarklet");
                ySlow = pageFactory.createPage(YSlow.class);
                ySlow.run();
                try{
                    Path yslowFile = Paths.get(WorkFolder.Har.toString(), event.getFileName());
                    Files.write(yslowFile, ySlow.getReportSource().getBytes());
                    TestCase test = new TestCase(testGuid);
                    test.putArtifact("YSLOW#" + event.getPageDescription(),TestCase.getRelativePath(yslowFile.toString()));
                    TestArtifactManager.updateTestRunInfo(test);
                }catch(Exception e){
                    log.error(e);
                }
                Browser.getCurrentBrowser().switchToDefaultContent();
            }
        });
        return listeners;
    }

    private void init() throws AutomationFrameworkException {
        Browser.getCurrentBrowser()
          .executeScript(
                        "javascript:(function(y,p,o){p=y.body.appendChild(y.createElement('iframe'));"
                                + "p.id='YSLOW-bookmarklet';"
                                + "p.style.cssText='display:none';"
                                + "o=p.contentWindow.document;"
                                + "o.open().write('<head><body onload=\"YUI_config={win:window.parent,doc:window.parent.document};"
                                + "var d=document;"
                                + "d.getElementsByTagName(\\'head\\')[0].appendChild(d.createElement(\\'script\\')).src=\\'http://yslow.org/yslow-bookmarklet.js\\'\">');"
                                + "o.close()}(document))");
    }

    public static class YSlow extends AbstractPageObject {


        @Override
        protected void initPageElement() {
            addElement(new Button("runTest_button", PathType.byId, "runtest-btn"));
            addElement(new TextContainer("progbar_text", PathType.byId, "progbar2"));
        }

        @Override
        protected PageElement getUniqueElement() throws AutomationFrameworkException {
            return getElementByName("runTest_button");
        }

        public String getReportSource() throws AutomationFrameworkException {
            validateElement("progbar_text", StatefulElement.PresentStatus.PRESENT_NOT_VISIBLE, 60);
            return getPageSource();

        }

        public boolean exist() throws AutomationFrameworkException {
            return validateElement("runTest_button", StatefulElement.PresentStatus.VISIBLE);
        }

        public void run() throws AutomationFrameworkException {
            getButton("runTest_button").click();
        }
    }

    @Override
    public String[] getSupportedFeatures() {
        return new String[] { PERFOMANCE_REPORT };
    }

}
