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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.components.AbstractPageObject.PathType;
import org.cybercat.automation.components.processor.AbstractProcessor;
import org.cybercat.automation.components.processor.AbstractProcessor.AbstractCriteria;
import org.cybercat.automation.core.Browser;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class PageElement {

    public enum ElementState {
        CREATED, INITIALIZED
    }

    protected String name;
    protected String[] path, oldPath;
    private String actualPath;
    private WebElement element;
    protected AbstractProcessor processor;
    private PathType processorType;
    protected WebDriverWait wait;
    private ElementState state = ElementState.CREATED;

    private static Logger log = Logger.getLogger(PageElement.class);

    protected PageElement(String name, PathType type, String path) {
        this(name, type, new String[] { path });
    }

    public PageElement(String name, String path, AbstractProcessor processor) {
        this(name, new String[] { path }, processor);
    }

    protected PageElement(String name, PathType type, String[] path) {
        this.name = name;
        this.path = path;
        this.oldPath = path;
        this.processorType = type;
        StringBuffer paths = new StringBuffer();
        for (int i = 0; i < path.length; i++) {
            paths.append("'");
            paths.append(path[i]);
            paths.append("',");
        }
        log.debug(" added new element: " + name + "  path:" + paths.toString());
    }

    public PageElement(String name, String[] path, AbstractProcessor processor) {
        this.name = name;
        this.path = path;
        this.oldPath = path;
        this.processor = processor;
    }

    
    protected void setElement(WebElement element) {
        this.element = element;
    }
    
    protected WebElement getElement() {
        return element;
    }

    public void detach() {
        this.element = null;
        this.path = oldPath;
        this.state = ElementState.CREATED;
    }

    public String getName() {
        return name;
    }

    public void setPath(String[] paths) {
        this.path = paths;
    }
    
    public String[] getPath() {
        return path;
    }
    
    public Dimension getElementSize(){
        return getElement().getSize();
    }
    
    final public void updatePath(Object... arg) {
        detach();
        path = new String[oldPath.length];
        for (int i = 0; i < path.length; i++)
            path[i] = String.format(oldPath[i], arg);
    }

    public String getActualPath() {
        return actualPath;
    }

    public void setActualPath(String actualPath) {
        this.actualPath = actualPath;
    }

    /**
     * @param driver
     * @throws PageObjectException
     */
    public void initWebElement(final Browser browser) throws PageObjectException {
        if (state.equals(ElementState.CREATED)) {
            try {
                browser.callImplicitlyWait(processor.implicitTimeout);
                processor.initWebElementByCriteria(browser, new AbstractCriteria<List<WebElement>>(path) {

                    @Override
                    public ExpectedCondition<List<WebElement>> getExpectedCondition(String path) {
                        return ExpectedConditions.presenceOfAllElementsLocatedBy(processor.getByElement(path));
                    }
                    
                    @Override
                    public boolean onSuccess(List<WebElement> elements, String path) {
                        if (elements != null && elements.size() > 0) {
                            actualPath = path;
                            element = elements.get(0);
                            browser.highlightElement(element);
                            setState(ElementState.INITIALIZED);
                            return true;
                        }
                        return false;
                    }
                });
            } catch (Exception e) {
                log.error("element \"" + name + "\" is not found ");
                throw new PageObjectException("Element \"" + name + "\" is not found. By path: "+getActualPath(), e);
            }
            if(element == null)
                throw new PageObjectException("Element \"" + name + "\" is not found on page. By path: "+getActualPath());
            if (!waitPresent()) 
                throw new PageObjectException("\"" + getName() + "\" element is not visible by path " + getActualPath());
            }
        
    }

    /**
     * Intention of this method is to initialize PageElement object based on
     * partial locator (that returns list of similar WebElements) and text
     * contained in the WebElement
     * 
     * @param browser
     * @param text
     * @throws PageObjectException
     */
    public void initWebElement(final Browser browser, final String text) throws PageObjectException {
        if (state.equals(ElementState.CREATED)) {
            try {                
                processor.initWebElementByCriteria(browser, new AbstractCriteria<List<WebElement>>(path) {

                    @Override
                    public ExpectedCondition<List<WebElement>> getExpectedCondition(String path) {
                        return ExpectedConditions.presenceOfAllElementsLocatedBy(processor.getByElement(path));
                    }
                    
                    @Override
                    public boolean onSuccess(List<WebElement> elements, String path) {
                        for (WebElement thisElement : elements) {
                            String innerText = (String) browser.executeScript("return arguments[0].textContent", thisElement);
                            System.out.println(thisElement.toString() + " +++ " + innerText + " +++ " + text);
                            if (StringUtils.containsIgnoreCase(innerText, text)) {
                                actualPath = path;
                                element = thisElement;
                                browser.highlightElement(element);
                                setState(ElementState.INITIALIZED);
                                return true;
                            }
                        }
                        return false;
                    }
                });
            } catch (Exception e) {
                log.error("element \"" + name + "\" is not found ");
                throw new PageObjectException("element \"" + name + "\" is not found by text: \"" + text + "\"", e);
            }
            if(element == null)
                throw new PageObjectException("element \"" + name + "\" is not found. By Path:"+getActualPath());
            if (!waitPresent()) 
                throw new PageObjectException("\"" + getName() + "\" element is not visible by path " + getActualPath()+"  and text: "+text);
            }
        
    }

    public PathType getProcessorType() {
        return processorType;
    }

    public void setProcessor(AbstractProcessor processor) {
        this.processor = processor;
    }


    protected boolean waitPresent() {
        if (state.equals(ElementState.CREATED))
            return false;
        try {
            processor.getWait().until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            log.warn("\"" + getName() + "\" element is not visible.");
            return false;
        }
        return true;
    }

    public boolean isEnabled() {
        return getElement().isEnabled();
    }

    public boolean isDisplayed() {
        return element.isDisplayed();
    }

    public String getAtributeByName(String name) {
        String value = element.getAttribute(name);
        log.info("Atribute " + name + " contains \"" + value + "\" value");
        return value;
    }

    public String getCssValue(String propertyName) {
        return element.getCssValue(propertyName);
    }
    
    public String getText() throws PageObjectException{
    	return getElement().getText();
    }

    /**
     * Set an attribute in the HTML of a page.
     * 
     * @param thisPage
     *            The page that contains this element
     * @param attributeName
     *            The attribute to modify
     * @param value
     *            The value to set
     */
    protected void setAttribute(AbstractPageObject thisPage, String attributeName, String value) {
        thisPage.execJS("arguments[0].setAttribute(arguments[1], arguments[2])", element, attributeName, value);
    }

    public ElementState getState() {
        return state;
    }

    protected void setState(ElementState state) {
        this.state = state;
    }
    
    public void sendKeys(CharSequence... keys){
        log.info("Send keys to TextField: "+getName());
        getElement().sendKeys(keys);
    }
    
    public void dragAndDrop(PageElement toElement, int xOffset, int yOffset) throws AutomationFrameworkException {
        Browser.getCurrentBrowser()
                .getActions()
                .moveToElement(getElement())
                .clickAndHold(getElement())
                .moveToElement(toElement.getElement(), xOffset, yOffset).release().perform();
    }    
    
    public void highlightElement() throws AutomationFrameworkException{
       Browser.getCurrentBrowser().highlightElement(getElement());
    }
    
}
