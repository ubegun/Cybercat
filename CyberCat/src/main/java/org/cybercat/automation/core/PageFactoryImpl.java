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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.PageFactory;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.annotations.AnnotationBuilder;
import org.cybercat.automation.annotations.CCProperty;
import org.cybercat.automation.components.AbstractPageObject;
import org.cybercat.automation.components.AbstractPageObject.PathType;
import org.cybercat.automation.components.processor.AbstractProcessor;
import org.junit.internal.builders.AnnotatedBuilder;
import org.openqa.selenium.Cookie;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.context.ApplicationContext;

/**
 * Creates and initializes page classes that extend AbstractPageObject
 */
public class PageFactoryImpl implements PageFactory {

    private static Logger LOG = Logger.getLogger(PageFactoryImpl.class);

    /**
     * Spring application context
     */
    private ApplicationContext context;
    /**
     * Basic url with which page classes work Can be changed through property file and/or Page.Object.Context.xml See
     * block of creating page objects in Page.Object.Context.xml
     */
    private String baseUrl;

    /**
     * Instance of current browser
     */
    
    public PageFactoryImpl(String baseUrl) throws AutomationFrameworkException {
        super();
        this.baseUrl = baseUrl;
    }

    protected void setup(AutomationMain mainFactory, ApplicationContext context) throws AutomationFrameworkException {
        if (this.context == null) {
            this.context = context;
            if (StringUtils.isNotBlank(this.baseUrl)) {
                Browser.getCurrentBrowser().get(baseUrl);
            }
        }
    }

    /**
     * Initializes and returns page class instance
     * 
     * @param page
     *            - page class
     * @throws PageObjectException
     */
    protected <T extends AbstractPageObject> T initPage(T page) throws PageObjectException {
        // get locators
        Locale locale;
        try {
            locale = new Locale(AutomationMain.getProperty("language"));
        } catch (AutomationFrameworkException e) {
            throw new PageObjectException("Main factory initialization exception.", e);
        }

        try {
            Browser browser = Browser.getCurrentBrowser();
            LOG.info("Current URL: " + getCurrentUrl());
            if (StringUtils.isNotBlank(page.getPageUrl()) 
                    && !browser.getCurrentUrl().contains(page.getPageUrl())) {
                LOG.error("Navigate to URL: " + page.getPageUrl());
                throw new PageObjectException("Page validation exception. Expected page URL is " + page.getPageUrl() + "  URL in fact " + getCurrentUrl());
            }
            page.setPageFactory(this);
            page.init(browser, locale);
            LOG.info(page.getClass().getName() + " page created.");
        } catch (Exception e) {
            throw new PageObjectException("Unable initialize " + page.getClass().getName() + " page by URL: " + page.getPageUrl() + "\n" + e.getMessage(), e);
        }
        return page;
    }

    /**
     * Returns base url
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Returns current URL
     * @throws AutomationFrameworkException 
     */
    public String getCurrentUrl() throws AutomationFrameworkException {
        return Browser.getCurrentBrowser().getCurrentUrl();
    }

    /**
     * Defines base url & current browser to direct by this URL
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Returns processor creating WebElements according to predefined type
     * 
     * @param type
     *            - type is defined depending on addressing type to html pages elements (xpath, css name, id, name)
     */
    public <T extends AbstractProcessor> T createElementProcessor(PathType type) {
        @SuppressWarnings("unchecked")
        T result = (T) context.getBean(type.toString());
        return result;
    }


    @SuppressWarnings("unchecked")
    public <T extends AbstractPageObject> T createPage(Class<T> page) throws PageObjectException {
        Constructor<T> cons;
        try {
         cons = page.getConstructor();
         T result = cons.newInstance();
         for(Field field: page.getDeclaredFields()){
             field.setAccessible(true);
             if(field.getAnnotation(CCProperty.class)!= null){
                 AnnotationBuilder.processPropertyField(result, field);
             }
         }
         AspectJProxyFactory proxyFactory = new AspectJProxyFactory(result);
         proxyFactory.addAspect(new PageObjectStateControlAcpect(this));
         result = (T) proxyFactory.getProxy();
        return result;            
    } catch (Exception e) {
        throw new PageObjectException("Page object creation problem.", e);
    }
    }


}
