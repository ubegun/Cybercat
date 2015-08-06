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
package org.cybercat.automation.components;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.PageElementRuntimeException;
import org.cybercat.automation.PageFactory;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.ResourceManager;
import org.cybercat.automation.annotations.AnnotationBuilder;
import org.cybercat.automation.components.StatefulElement.PresentStatus;
import org.cybercat.automation.core.Browser;
import org.openqa.selenium.Alert;

import com.sun.media.Log;

/**
 * Abstract page element providing methods for managing html elements at user page.
 * <br>
 *  Adds element to the page. It is recommended to use it for setting elements in initPageElement() method.
 *  To work with set element use Get method that corresponds your element type.<br> When getting element through 
 *  Get method, it it initialized and validated on the web page. While validating it is checked whether 
 *  it is visible on the web page. If an objects is not visible or<br> initialization 
 *  process failed, PageObjectException is thrown.
 * 
 */
public abstract class AbstractPageObject {

    public static enum PathType {
        byXPath, byCssSelector, byId, byName, byClassName, byLinkText
        // ByPartialLinkText, ByTagName
    }

    public enum PageState {
        CREATED, INITIALIZED
    }

    private PageState state = PageState.CREATED;

    private static Logger LOG = Logger.getLogger(AbstractPageObject.class);

    private Browser browser;
    private String pageUrl;
    //initialized pageFragments
    private List<AbstractPageObject> pageFragments = new ArrayList<AbstractPageObject>();
    Locale currentLocation;
    protected ResourceBundle pageResourceBundle;
    Map<String, PageElement> elements = new HashMap<String, PageElement>();
    @SuppressWarnings("unused")
    private String title;

    private PageFactory pageFactory;

    @Transient
    public String getPageUrl() {
        return pageUrl;
    }

    @Transient
    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }    

    @Transient
    public void init(Browser browser, Locale locale) throws AutomationFrameworkException {
        if(this.state == PageState.INITIALIZED){
            return;
        }    
        this.state = PageState.INITIALIZED;
        AnnotationBuilder.processCCPageFragment(this);
        LOG.info(this.getClass().getSimpleName() + "\n page initialization...");
        this.browser = browser;
        this.currentLocation = locale;
        ResourceBundle bundle = ResourceManager.getLocators(locale);
        this.setPageResourceBundle(bundle);
        initPageElement();
        isLoaded();
        title = browser.getTitle();
    }

    @Transient
    public PageState getState() {
        return state;
    }

    @Transient
    public void detach(){
        this.cleanupElements();
        state = PageState.CREATED;
        browser = null;
    }
    
    
    /**
     * initialize page elements for final implementation
     */
    @Transient
    protected abstract void initPageElement();

    public String getPageName() throws AutomationFrameworkException {
        return getBrowser().getTitle();
    }

    /**
     * @return
     * @throws AutomationFrameworkException 
     */
    private Browser getBrowser() throws AutomationFrameworkException {
        if(browser == null)
            browser = Browser.getCurrentBrowser();        
        return browser;
    }

    /**
     * Returns the page element allowing to identify if the page has loaded yet
     * 
     * @throws PageObjectException
     * @throws AutomationFrameworkException 
     */
    protected abstract PageElement getUniqueElement() throws AutomationFrameworkException;

    public Locale getCurrentLocale() {
        return currentLocation;
    }

    /**
     * Adds element to the page. It is recommended to use it for setting elements in initPageElement() method.
     * 
     * @param element
     */
    @Transient
    protected <T extends PageElement> void addElement(T element) {
        element.setProcessor(pageFactory.createElementProcessor(element.getProcessorType()));
        this.elements.put(element.getName(), element);
    }

    /**
     * Returns status of element by name. Time to wait for status is specified in property file
     * 
     * @param elementName - the name of the element that was set in init mode
     * @throws AutomationFrameworkException 
     */
    protected PresentStatus getElementStatus(String elementName, Object... arg) throws AutomationFrameworkException {
        if (!this.elements.containsKey(elementName))
            throw new PageElementRuntimeException(elementName + " element is not defined.");
        PageElement element = this.elements.get(elementName);
        element.setPath(replaceKey(element.getPath()));
        element.updatePath(arg);
        StatefulElement<PageElement> result = new StatefulElement<PageElement>(element);
        result.setPath(replaceKey(result.getPath()));
        result.initWebElement(getBrowser());
        return result.getPresentStatus();
    }


    protected boolean isElementPresent(String elementName, int timeoutSec) throws AutomationFrameworkException {
        return validateElementWithTimeOut(elementName, PresentStatus.VISIBLE, timeoutSec);
    }

    /**
     * Validate element's status by specified status. Waiting timeout is set to 0
     * 
     * @param elementName - the name of the element that was set in init mode
     * @param expectedStatus - expected status from {@link PresentStatus} enumeration
     * @return
     * @throws AutomationFrameworkException 
     */
    protected boolean validateElement(String elementName, PresentStatus extectedStatus, Object... arg) throws AutomationFrameworkException {
        if (!this.elements.containsKey(elementName))
            throw new PageElementRuntimeException(elementName + " element is not defined.");
        PageElement element = this.elements.get(elementName);
        element.setPath(replaceKey(element.getPath()));
        element.updatePath(arg);
        StatefulElement<PageElement> result = new StatefulElement<PageElement>(element, extectedStatus, 0);
        result.initWebElement(getBrowser());
        return result.isValid();
    }

    /**
     *     Waits for element's specified status.
     *      
     * @param elementName - the name of the element that was set in init mode
     * @param expectedStatus - expected status from {@link PresentStatus} enumeration
     * @param timeout - Defines the implicit and explicit timeouts. Waiting time for the set element mode in seconds. 
     * @param arg - arguments that modify xPath. 
     * @see  String#format(String, Object...) 
     * @return boolean 
     * @throws AutomationFrameworkException 
     */
    protected boolean validateElementWithTimeOut(String elementName, PresentStatus expectedStatus, int timeout, Object... arg) throws AutomationFrameworkException {
        if (!this.elements.containsKey(elementName))
            throw new PageElementRuntimeException(elementName + " element is not defined.");
        PageElement element = this.elements.get(elementName);
        element.setPath(replaceKey(element.getPath()));
        element.updatePath(arg);
        StatefulElement<PageElement> result = new StatefulElement<PageElement>(element, expectedStatus, timeout);
        result.initWebElement(getBrowser());
        return result.isValid();
    }

    @SuppressWarnings("unchecked")
    protected <T extends PageElement> T getElementByName(String name, Object... arg) throws AutomationFrameworkException {
        if (!this.elements.containsKey(name)) {
            LOG.error(name + " elemen element is not defined.");
            return null;
        }
        LOG.debug("Thread info: Page Object->" + this.getClass().getSimpleName() + "\tElement->" + name
                + "\tBrowser ID->" + getBrowser().getSessionId());
        T element;
        try {
            element = (T) elements.get(name);
        } catch (ClassCastException e) {
            throw new AutomationFrameworkException(
                    "Wrong type of page element. Please verify code on getting type of element.\n "
                            + this.getClass().getSimpleName() + "\n\t `-> " + name);
        }
        element.detach();
        element.updatePath(arg);
        element.setPath(replaceKey(element.getPath()));
        try{
            element.initWebElement(getBrowser());
        }catch(AutomationFrameworkException e){ 
            LOG.warn("The element not found: \n " + this.getClass().getSimpleName() + "\n\t `-> " + element.getName());
            throw new AutomationFrameworkException(e);
        }    
        return element;
    }

    /**
     * Replace localized key Key format: {key.name}
     * 
     */
    private String[] replaceKey(String[] paths) {
        for (int i = 0; i < paths.length; i++) {
            Pattern p = Pattern.compile("(\\{[^\\}]*\\})");
            Matcher m = p.matcher(paths[i]);
            String xPath = new String(paths[i]);
            while (m.find()) {
                try {
                    String key = paths[i].substring(m.start() + 1, m.end() - 1);
                    xPath = xPath.replace("{" + key + "}", pageResourceBundle.getString(key));
                } catch (Exception e) {
                    LOG.error("Fail to replace localized key. Path:" + paths[i] + "\tPage: "
                            + this.getClass().getSimpleName());
                }
            }
            paths[i] = xPath;
        }
        return paths;
    }

    protected Button getDynamicButton(String name, String text) throws AutomationFrameworkException {
        if (this.elements.containsKey(name)) {
            Button result = (Button) this.elements.get(name);
            result.setPath(replaceKey(result.getPath()));

            // should reinit element
            result.setState(PageElement.ElementState.CREATED);

            result.initWebElement(getBrowser(), text);
            return result;
        }
        LOG.error(name + " elemen element is not defined.");
        return null;
    }

        
    /**
     * Returns a valid element of {@link JQButton} type
     *  
     * @param name - name of element
     * @param arg - arguments that modify xPath. 
     * @throws AutomationFrameworkException 
     * @see  String#format(String, Object...) 
     */
    protected JQButton getJQButton(String name, Object... arg) throws AutomationFrameworkException {
        return (JQButton) getElementByName(name, arg);
    }
    
    /**
     * Returns a valid element of {@link Button} type
     *  
     * @param name - name of element
     * @param arg - arguments that modify xPath. 
     * @throws AutomationFrameworkException 
     * @see  String#format(String, Object...) 
     */
    protected Button getButton(String name, Object... arg) throws AutomationFrameworkException {
        return (Button) getElementByName(name, arg);
    }

    /**
     * Returns a valid element of {@link SvgChart} type
     *  
     * @param name - name of element
     * @param arg - arguments that modify xPath. 
     * @throws AutomationFrameworkException 
     * @see  String#format(String, Object...) 
     */
    protected SvgChart getSvgChart(String name, Object... arg) throws AutomationFrameworkException {
        return (SvgChart) getElementByName(name, arg);
    }

    /**
     * Returns a valid element of {@link SelectorBox} type
     *  
     * @param name - name of element
     * @param arg - arguments that modify xPath. 
     * @throws AutomationFrameworkException 
     * @see  String#format(String, Object...) 
     */
    protected SelectorBox getSelectorBox(String name, Object... arg) throws AutomationFrameworkException {
        return (SelectorBox) getElementByName(name, arg);
    }

    /**
     * Returns a valid element of {@link TextContainer} type
     *  
     * @param name - name of element
     * @param arg - arguments that modify xPath. 
     * @throws AutomationFrameworkException 
     * @see  String#format(String, Object...) 
     */
    protected TextContainer getTextContainer(String name, Object... arg) throws AutomationFrameworkException {
        return (TextContainer) getElementByName(name, arg);
    }

    /**
     * Returns a valid element of {@link TextField} type
     *  
     * @param name - name of element
     * @param arg - arguments that modify xPath. 
     * @throws AutomationFrameworkException 
     * @see  String#format(String, Object...) 
     */
    protected TextField getTextField(String name, Object... arg) throws AutomationFrameworkException {
        return (TextField) getElementByName(name, arg);
    }

    /**
     * Returns a valid element of {@link RadioGroup} type
     *  
     * @param name - name of element
     * @param arg - arguments that modify xPath. 
     * @throws AutomationFrameworkException 
     * @see  String#format(String, Object...) 
     */
    protected RadioGroup getRadioGroup(String name, Object... arg) throws AutomationFrameworkException {
        return (RadioGroup) getElementByName(name, arg);
    }

    /**
     * Returns a valid element of {@link GroupElements} type
     *  
     * @param name - name of element
     * @param arg - arguments that modify xPath. 
     * @throws AutomationFrameworkException 
     * @see  String#format(String, Object...) 
     */
    protected GroupElements<?> getGroupElements(String name, Object... arg) throws AutomationFrameworkException {
        return (GroupElements<?>) getElementByName(name, arg);
    }

    /**
     * Returns a valid element of {@link NavigationLink} type
     *  
     * @param name - name of element
     * @param arg - arguments that modify xPath. 
     * @throws AutomationFrameworkException 
     * @see  String#format(String, Object...) 
     */
    protected NavigationLink getNavigationLink(String name, Object... arg) throws AutomationFrameworkException {
        return (NavigationLink) getElementByName(name, arg);
    }

    /**
     * Returns a valid element of {@link CheckBox} type
     *  
     * @param name - name of element
     * @param arg - arguments that modify xPath. 
     * @throws AutomationFrameworkException 
     * @see  String#format(String, Object...) 
     */
    protected CheckBox getCheckBox(String name, Object... arg) throws AutomationFrameworkException {
        return (CheckBox) getElementByName(name, arg);
    }

    protected void isLoaded() throws AutomationFrameworkException {
        // Define a list of WebElements that match the unique element locator
        // for the page
        PageElement uniqueElement = getUniqueElement();
        // WebElement uniqueElement = getUniqueElement().getElement();

        // Assert that the unique element is present in the DOM
        if (uniqueElement == null) {
            throw new PageObjectException("Unique Element not defined for " + this.getClass().getSimpleName()
                    + " page. Pleace to implement getUniqueElement() method.");
        }
    }

    @Transient
    public void addPageFragment(AbstractPageObject pageFragment) {
        this.pageFragments.add(pageFragment);
    }
    
    @Transient
    public void setPageFactory(PageFactory pageFactory) {
        this.pageFactory = pageFactory;
    }

    @Transient
    protected void setPageResourceBundle(ResourceBundle pageResourceBundle) {
        this.pageResourceBundle = pageResourceBundle;
    }

    protected String getResourceByName(String name) throws PageObjectException {
        String resourceName = name.replace(' ', '.');
        if (pageResourceBundle.containsKey(resourceName)) {
            return pageResourceBundle.getString(resourceName);
        }
        throw new PageObjectException("The resourse was not defined for this resource name");
    }

    @Transient
    protected void cleanupElements() {
        for (Map.Entry<String, PageElement> entry : elements.entrySet()) {
            entry.getValue().detach();
        }
        for (AbstractPageObject pageFragment : pageFragments) {
            pageFragment.cleanupElements();
        }
        pageFragments = new ArrayList<AbstractPageObject>();
    }

    protected void switchToFrame(String name) throws AutomationFrameworkException {
        getBrowser().switchToFrame(name);
    }

    protected String getWindowHandle() throws AutomationFrameworkException {
        return getBrowser().getWindowHandle();
    }

    protected Alert switchToAlert() throws AutomationFrameworkException {
        return getBrowser().switchToAlert();
    }

    protected Set<String> getWindowNames() throws AutomationFrameworkException {
        return getBrowser().getWindowHandles();
    }

    protected void switchToDefaultContent() throws AutomationFrameworkException {
        getBrowser().switchToDefaultContent();
    }

    protected Object execJS(String script, Object... args) throws AutomationFrameworkException {
        try{ 
            return getBrowser().executeScript(script, args);
        }catch(Exception e){  
            Log.error("Execution Java script for " + this.getClass().getSimpleName() + " page object. Script: " +  script);
            throw new AutomationFrameworkException("Execution Java script for " + this.getClass().getSimpleName() + " page object. Script: " +  script, e);
        }
    }

    protected void pause(long millis) {
        try {
            Thread.sleep(millis);
            LOG.info("Waiting action complete for " + millis + " ms");
        } catch (InterruptedException e) {
            LOG.error("Errors occured when executing pause() method");
        }
    }

    protected boolean areAllElementsPresent(String... names) throws AutomationFrameworkException {
        for (String name : names) {
            if (getElementStatus(name) != PresentStatus.VISIBLE) {
                return false;
            }
        }
        return true;
    }

    protected List<PageElement> getMissingElements() throws AutomationFrameworkException {
        Collection<PageElement> elements = this.elements.values();
        List<PageElement> missElements = new ArrayList<>();
        for (PageElement element : elements) {
            if (!isElementPresent(element.getName(), 1)) {
                missElements.add(element);
            }
        }

        if (missElements.size() > 0) {
            LOG.warn("Missing elements: " + missElements);
        }
        return missElements;
    }

    protected String getCurrentUrl() throws AutomationFrameworkException {
        String url = getBrowser().getCurrentUrl();
        LOG.info("Current URL is " + url);
        return url;
    }

    protected void refreshPage() throws AutomationFrameworkException {
        getBrowser().refresh();
        cleanupElements();
        LOG.info("Page \"" + getPageName() + "\" with url: " + getCurrentUrl() + " refreshed");
    }

    protected void navigateBack() throws AutomationFrameworkException {
        LOG.info("Navigating back from \"" + getPageName() + "\" ");
        getBrowser().navigateBack();
    }

    /**
     * Provides wait until all of ajax requests on page will be finished
     * 
     * @param timeToWaitInSeconds
     *            - maximum time to wait in seconds
     * @throws PageObjectException
     */
    protected void waitForAjaxRequestsEnding(long timeToWaitInSeconds) throws PageObjectException {
        try {
            // can throw WebDriver exceptions in case of window.jQuery is undefined on page
            LOG.info("wait For Ajax Requests finished");
            getBrowser().waitForAjaxRequestsEnding(timeToWaitInSeconds);
        } catch (Exception e) {
            LOG.info("Cannot wait for ajax requests  finish, perhaps jQuery is not injected in this page");
        }

    }

    protected boolean isBrowserRemote() throws AutomationFrameworkException {
        return getBrowser().isRemote();
    }

    protected String getLocalizedValue(String key) {
        return pageResourceBundle.getString(key);
    }

    protected String[] getLocalizedValues(String[] keys) {
        String[] localizedValues = new String[keys.length];
        for (int i = 0; i < keys.length; i++)
            localizedValues[i] = pageResourceBundle.getString(keys[i]);
        return localizedValues;
    }

    protected String getCurrentLanguage() {
        return pageResourceBundle.getLocale().getLanguage();
    }

    @Transient
    protected void threadWait(long second) throws PageObjectException {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            throw new PageObjectException(e);
        }
    }

    protected String getPageSource() throws AutomationFrameworkException {
        return getBrowser().getPageSource();
    }

}
