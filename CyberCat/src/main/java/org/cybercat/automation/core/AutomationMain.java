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

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.PageFactory;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.PersistenceManager;
import org.cybercat.automation.ResourceManager;
import org.cybercat.automation.events.EventManager;
import org.cybercat.automation.persistence.SourceType;
import org.cybercat.automation.persistence.model.Identity;
import org.cybercat.automation.persistence.model.PageModelException;
import org.cybercat.automation.rest.AbstractRestService;
import org.cybercat.automation.rest.RestServiceException;
import org.cybercat.automation.soap.SoapService;
import org.cybercat.automation.soap.SoapServiceException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class AutomationMain {

    // Single tone instance of AutomationMain class
    private static AutomationMain automationMain;

    private static final String DEFAULT_PROPERTY_FILE = "test.properties";

    // Spring application context
    private ApplicationContext context;

    /**
     * Application settings defined at start up from command line such as staging.properties, hpqa.properties etc
     */
    private Properties configProperties;

    /**
     * Main class constructor Below is example of running automation test from command line as maven test scope mvn
     * clean test -Dconfig.properties=test.properties
     * 
     * @param context
     *            - Spring application context
     * @throws PageModelException
     * @see org.springframework.context.ApplicationContext
     */
    private AutomationMain(ApplicationContext context, Boolean... xmlRepositoryFlag) throws PageModelException {
        this.context = context;
        configProperties = (Properties) context.getBean("configProperties");
        // application initialization block
        if (xmlRepositoryFlag.length != 0 && xmlRepositoryFlag[0] != false) {
            getConfigurationManager().initXmlRepository();
        }
    }

    protected ConfigurationManager getConfigurationManager() {
        ConfigurationManager config = context.getBean(ConfigurationManager.class);
        config.setup(context);
        return config;
    }

    /**
     * Returns factory for pages
     * @throws PageObjectException 
     */
    public PageFactory getPageFactory() throws AutomationFrameworkException {
        PageFactoryImpl result = (PageFactoryImpl) context.getBean("pageFactory");
        result.setup(this, context);
        return result;
    }
    
    /**
     * Returns property by name
     * 
     * @param name
     *            - property name
     * @return
     * @throws AutomationFrameworkException 
     */
    public static String getProperty(String name) throws AutomationFrameworkException {
        return StringUtils.trim(getMainFactory().configProperties.getProperty(name));
    }

    /**
     * Returns property by name
     * 
     * @param name
     *            - property name
     * @return
     */
    public static URL getPropertyUrl(String name) throws PageObjectException {
        URL result;
        try {
            result = new URL(getMainFactory().configProperties.getProperty(name));
        } catch (Exception e) {
            throw new PageObjectException(e);
        }
        return result;
    }

    /**
     * Returns property by name as long variable
     * 
     * @param name
     *            - property name
     * @return
     * @throws AutomationFrameworkException 
     * @throws  
     */
    public static long getPropertyLong(String name) throws AutomationFrameworkException {
        return Long.parseLong(getMainFactory().configProperties.getProperty(name));
    }

    public static boolean getPropertyBoolean(String name) throws AutomationFrameworkException {
        return Boolean.parseBoolean(getMainFactory().configProperties.getProperty(name, "false"));
    }

    /**
     * Returns javascript file as string Example: automationMain.getJavaScript("clean_target.js"); returns - JS file as
     * simple string object
     * 
     * @param scriptName
     *            - name of file containing script
     * @throws PageObjectException
     */
    public String getJavaScript(String scriptName) throws PageObjectException {
        return ResourceManager.getJsAsString(scriptName);
    }

    /**
     * Initializes and returns instance of main factory class
     * 
     * @param String
     *            - name of property file with environment settings
     * 
     * @throws PageObjectException
     */
    public final static synchronized AutomationMain getMainFactory(String env_properties, Boolean... xmlRepositoryFlag)
            throws AutomationFrameworkException {
        if (automationMain == null) {
            String propertyFileName = System.getProperty("config.properties");
            System.out.println("===============================Runtime properties===============================");
            System.out.println("environment propery file:\t" + propertyFileName);
            String basicArtifactsDir = System.getProperty("config.basicArtifactsDir");
            System.out.println("artifacts directory:\t" + basicArtifactsDir);
            System.out.println("Report title:\t\t"
                    + System.getProperty("org.uncommons.reportng.title", "Cybercat sample"));
            System.out.println("================================================================================");
            if (StringUtils.isEmpty(propertyFileName)) {
                System.setProperty("config.properties", env_properties);
            }
            try {
                ConfigurationManager.initWorkDirectories(basicArtifactsDir);
            } catch (Exception e) {
                throw new AutomationFrameworkException("Model initialization exception.", e);
            }
            try {
                ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "rootContext.xml" });
                automationMain = new AutomationMain(context, xmlRepositoryFlag);

            } catch (Exception e) {
                throw new AutomationFrameworkException("Spring context initialization exception.", e);
            }

        }
        return automationMain;
    }

    /**
     * Init main factory
     */
    public final static AutomationMain getMainFactory() throws AutomationFrameworkException {
        return getMainFactory(DEFAULT_PROPERTY_FILE);
    }

    /**
     * Returns resource bundle containing parameters hashmap described in MetaData.properties
     * 
     * @return
     * @throws AutomationFrameworkException 
     */
    public static final ResourceBundle getTestMetaData() throws AutomationFrameworkException {
        getMainFactory();
        return ResourceManager.getTestMetaData();
    }

    /**
     * Rerurns parameter value by name from MetaData.properties file
     * 
     * @param key
     *            - property name
     * @throws AutomationFrameworkException 
     */
    public String getTestMetaData(String key) throws AutomationFrameworkException {
        return getTestMetaData().getString(key);
    }

    /**
     * Returns string bundle for defined parameter by property name, provided that they are in property file as
     * comma-separated string
     * 
     * @param key
     *            - property name
     * @throws AutomationFrameworkException 
     */
    public String[] getTestMetaDataArray(String key) throws AutomationFrameworkException {
        return getTestMetaData(key).split("\\s*,\\s*");
    }

    /**
     * Returns class which manages loading and saving of test model like
     * <code>org.cybercat.automation.persistence.model.User</code>
     * 
     */
    public PersistenceManager getPersistenceManager() {
        return context.getBean(PersistenceManager.class);
    }

    public DataWorker getDataProvider(SourceType dataSource) throws PageObjectException {
        return (DataWorker) context.getBean(dataSource.toString());
    }

    /**
     * Use CCIntegrationService annotation  
     */
    @Deprecated
    public <T extends AbstractRestService> T createRestService(Class<T> clazz, Identity user) throws RestServiceException {
        T service = context.getBean(clazz);
        service.createNewSession(user);
        return service;
    }

        
    /**
     * Use CCIntegrationService annotation  
     */
    @Deprecated
    public <T extends SoapService> T getSoapService(Class<T> clazz, Identity user) throws SoapServiceException {
        T service = context.getBean(clazz);
        service.createNewSession(user);
        return service;
    }

    @Deprecated
    public <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static final EventManager getEventManager() throws AutomationFrameworkException {
        return AutomationMain.getMainFactory().getConfigurationManager().getEventManager();
    }

    protected ApplicationContext getContext(){
        return this.context;
    }
}
