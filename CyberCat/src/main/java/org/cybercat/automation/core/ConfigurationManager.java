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
package org.cybercat.automation.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.Configuration;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.PersistenceManager;
import org.cybercat.automation.ResourceManager;
import org.cybercat.automation.addons.AddonProvider;
import org.cybercat.automation.addons.common.ScreenshotManager;
import org.cybercat.automation.addons.common.TestLoggerAddon;
import org.cybercat.automation.addons.jira.JiraReportManager;
import org.cybercat.automation.addons.media.MediaController;
import org.cybercat.automation.addons.yslow.PerformanceReportManager;
import org.cybercat.automation.browsers.RemoteServerProvider;
import org.cybercat.automation.core.Browser.Browsers;
import org.cybercat.automation.events.EventChangeTestConfig;
import org.cybercat.automation.events.EventListener;
import org.cybercat.automation.events.EventManager;
import org.cybercat.automation.events.EventStartTest;
import org.cybercat.automation.persistence.TestArtifactManager;
import org.cybercat.automation.persistence.model.Identity;
import org.cybercat.automation.persistence.model.PageModelException;
import org.cybercat.automation.persistence.model.TestCase;
import org.cybercat.automation.test.AbstractTestCase;
import org.cybercat.automation.utils.WorkFolder;
import org.springframework.context.ApplicationContext;

/**
 * Application initial configuration class.
 * scope: thread
 */
public class ConfigurationManager implements AddonContainer {

    private static Logger log = Logger.getLogger(ConfigurationManager.class);

    private ApplicationContext context;
    private EventManagerImpl eventManager;
    private List<AddonContainer> holders;
    private Browser browser = null;
    private ScreenshotManager screenshotManager;
    private JiraReportManager jiraReportManager;
    private PerformanceReportManager performanceReportManager;
    private Class<? extends AbstractTestCase> testClass;


    public ConfigurationManager() {
        super();
    }

    public void setup(ApplicationContext context) {
        if (this.context != null) // this instance has been already initialized
            return;
        this.context = context;
        eventManager = context.getBean(EventManagerImpl.class);
        createHolders();
        eventManager.setupListeners(holders);
    }

    /**
     * Initializes listener holders. This is the main method for creating holders that control listeners` life cycle.
     * 
     */
    private void createHolders() {
        try {
            screenshotManager = new ScreenshotManager();
        } catch (AutomationFrameworkException e) {
            log.error("Screenshot manager initialisation exception.", e);
        }
        jiraReportManager = new JiraReportManager();
        performanceReportManager = new PerformanceReportManager();
        TestLoggerAddon logggerAddon = new TestLoggerAddon();
        holders = new ArrayList<AddonContainer>();
        holders.add(context.getBean(MediaController.class));
        holders.add(logggerAddon);
        holders.add(this);
        holders.add(screenshotManager);
        holders.add(jiraReportManager);
        holders.add(performanceReportManager);
        holders.add(eventManager);
        try{
            AddonProvider provider = new AddonProvider();
            holders.addAll(provider.getAddons());
        }catch(Exception e){
            log.error("External addon initialization exception.", e);
        }
    }

    /**
     * Creates default users described in MetaData.properties file.
     * 
     * @throws PageModelException
     */
    public void initXmlRepository() throws PageModelException {
        PersistenceManager persistence = context.getBean(PersistenceManager.class);
        ResourceBundle resource = ResourceManager.getTestMetaData();
        List<Identity> users = persistence.load(Identity.class);
        Enumeration<String> keys = resource.getKeys();
        String key;
        while (keys.hasMoreElements()) {
            key = keys.nextElement();
            if (key.contains("registred.users") && (!users.contains(Identity.parseFromString(resource.getString(key))))) {
                persistence.save(Identity.parseFromString(resource.getString(key)));
            }
        }
    }

    /**
     * Initializes work directories that store screenshots, logs, xml models etc.
     * 
     * @throws PageModelException
     */
    public static String initWorkDirectories(String basicDirPath) throws PageModelException {

        // set basic directory to store artifacts
        String baseDir = WorkFolder.initWorkFolders(basicDirPath);

        WorkFolder[] folders = WorkFolder.values();
        for (int i = 0; i < folders.length; i++) {
            try {
                folders[i].md();
            } catch (IOException e) {
                throw new PageModelException("Model initialization exception.", e);
            }
        }
        return baseDir;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public Collection<EventListener<?>> createListeners(Configuration config) {
        ArrayList<EventListener<?>> listeners = new ArrayList<EventListener<?>>();
        listeners.add(new EventListener<EventStartTest>(EventStartTest.class, 1000) {

            @Override
            public void doActon(EventStartTest event) throws Exception {
                testClass = event.getTestClass();
                TestCase test = new TestCase(testClass.getName());
                test.setQtName(event.getDescription());
                TestArtifactManager.updateTestRunInfo(test);
            }
        });

        listeners.add(new EventListener<EventChangeTestConfig>(EventChangeTestConfig.class, 1000) {

            @Override
            public void doActon(EventChangeTestConfig event) throws Exception {
                eventManager.release();
                AutomationMain.getMainFactory().getPageFactory().release();
                eventManager.setupListeners(holders);
            }

        });
        return listeners;
    }

    private final static String REMOTE_SERVER = "remote.server";
    
    /**
     * Returns browser instance by name Additional info in selenium documentation for Webdriver
     * 
     * @param browser
     *            - browser name
     * @return - Webdriver
     * @throws PageObjectException
     */
    protected Browser getBrowser() throws AutomationFrameworkException {
        if(browser != null && !browser.isClosed())
            return browser;
        Browsers browserType = Browsers.valueOf(AutomationMain.getProperty("browser.name"));
        if (AutomationMain.getPropertyBoolean(REMOTE_SERVER)) {
            browser = getRemoteBrowser(browserType);
        }else{ 
            browser = getLocalBrowser(browserType);
        }
        return browser;
    }    
    
    private Browser getLocalBrowser(Browsers browserType) throws PageObjectException {
        try {
            browser = (Browser) context.getBean(browserType.name());
        } catch (Exception e) {
        	e.printStackTrace();
            log.error(browser.toString() + " browser is unsupported by your system.");
            throw new PageObjectException(e);
        }
        browser.callMaximize();
        browser = setupBrowser(browser);
        return browser;
    }
    
    private Browser getRemoteBrowser(Browsers browserType) throws PageObjectException {
        try {
            RemoteServerProvider rsp = context.getBean(RemoteServerProvider.class);
            browser = rsp.createRemoteWebDriver(browserType);
            browser = setupBrowser(browser);
            return browser;
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(e.getStackTrace());
            log.error(browserType.toString() + " remote browser is unsupported by your system.");
            throw new PageObjectException(e.getLocalizedMessage());
        }

    }
    
    private Browser setupBrowser(Browser browser){
        browser.callMaximize();
        browser.setEventManager(getEventManager());
        screenshotManager.setScreenshotProvider(browser);
        eventManager.setupListener(browser);
        return browser;
    }    

    protected Class<? extends AbstractTestCase> getTestClass() {
        return testClass;
    }

    /**
     * Returns undefined value. Null value displays this module is running as default
     */
    @Override
    public String[] getSupportedFeatures() {
        return null;
    }

}