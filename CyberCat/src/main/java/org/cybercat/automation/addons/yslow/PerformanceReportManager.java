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

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.Configuration;
import org.cybercat.automation.PageFactory;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.addons.common.StartPerformanceMeasureEvent;
import org.cybercat.automation.components.AbstractPageObject;
import org.cybercat.automation.components.Button;
import org.cybercat.automation.components.PageElement;
import org.cybercat.automation.components.StatefulElement;
import org.cybercat.automation.components.TextContainer;
import org.cybercat.automation.core.AddonContainer;
import org.cybercat.automation.events.EventListener;

/**
 * User: Oleh_Kovalyshyn Date: 4/8/13 Time: 3:03 PM
 */
public class PerformanceReportManager implements AddonContainer {

    private static final Logger log = Logger.getLogger(PerformanceReportManager.class);
    public final static String PERFOMANCE_REPORT = "Perfomance report";
    private YSlow ySlow;

    @Override
    public Collection<EventListener<?>> createListeners(Configuration config) {
        if (!config.hasFeature(PERFOMANCE_REPORT)) {
            return new ArrayList<EventListener<?>>();
        }

        ArrayList<EventListener<?>> listeners = new ArrayList<EventListener<?>>();

        listeners.add(new EventListener<GetPerformanceReportEvent>(GetPerformanceReportEvent.class, 100) {

            @Override
            public void doActon(GetPerformanceReportEvent event) throws Exception {

                PageFactory pageFactory = event.getPageFactory();

                log.debug("Performance measuring finished");
                pageFactory.getBrowser().switchToFrame("YSLOW-bookmarklet");
                BufferedWriter writer = Files.newBufferedWriter(
                        Paths.get(event.getPath().toString(), event.getFileName()), Charset.defaultCharset());
                writer.write(ySlow.getReportSource());
                pageFactory.getBrowser().switchToDefaultContent();

            }
        });

        listeners.add(new EventListener<StartPerformanceMeasureEvent>(StartPerformanceMeasureEvent.class, 100) {

            @Override
            public void doActon(StartPerformanceMeasureEvent event) throws Exception {

                PageFactory pageFactory = event.getPageFactory();

                log.debug("Performance measuring started");
                init(event.getPageFactory());
                pageFactory.getBrowser().switchToFrame("YSLOW-bookmarklet");
                ySlow = pageFactory.createPage(YSlow.class);
                ySlow.run();
                pageFactory.getBrowser().switchToDefaultContent();

            }
        });

        return listeners;
    }

    private void init(PageFactory pageFactory) {
        pageFactory
                .getBrowser()
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

        public YSlow(String pageUrl) {
            super(pageUrl);
        }

        @Override
        protected void initPageElement() {
            addElement(new Button("runTest_button", PathType.byId, "runtest-btn"));
            addElement(new TextContainer("progbar_text", PathType.byId, "progbar2"));
        }

        @Override
        protected PageElement getUniqueElement() throws PageObjectException {
            return getElementByName("runTest_button");
        }

        public String getReportSource() {
            validateElement("progbar_text", StatefulElement.PresentStatus.PRESENT_NOT_VISIBLE, 60);

            return getPageSource();

        }

        public boolean exist() {
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
