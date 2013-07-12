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
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.browsers.Browser;
import org.cybercat.automation.components.AbstractPageObject.PathType;
import org.cybercat.automation.components.processor.AbstractProcessor.AbstractCriteria;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class VisibleTextField extends TextField {

    private static Logger log = Logger.getLogger(VisibleTextField.class);
    
    public VisibleTextField(String name, PathType type, String path) {
        super(name, type, path);
    }

    public VisibleTextField(String name, PathType type, String[] path) {
        super(name, type, path);
    }

    @Override
    public void initWebElement(final Browser browser) throws PageObjectException {
        if (getState().equals(ElementState.CREATED)) {
            setBrowser(browser);
            try {
                processor.initWebElementByCriteria(browser, new AbstractCriteria<List<WebElement>>(path) {
                    
                    @Override
                    public ExpectedCondition<List<WebElement>> getExpectedCondition(String path) {
                        return ExpectedConditions.presenceOfAllElementsLocatedBy(processor.getByElement(path));
                    }

                    @Override
                    public boolean onSuccess(List<WebElement> elements, String path) {
                        if (elements != null && elements.size() > 0) {
                            setActualPath(path);
                            setState(ElementState.INITIALIZED);
                            for (WebElement innerElement : elements) {
                                setElement(innerElement);
                                if (getElement().isDisplayed()) {
                                    browser.highlightElement(getElement());
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                });
            } catch (Exception e) {
                log.error("element \"" + name + "\" is not found ");
                throw new PageObjectException("element \"" + name + "\" is not found.", e);
            }
            if(getElement() == null)
                throw new PageObjectException("element \"" + name + "\" is not found.");
            if (!waitPresent()) 
                throw new PageObjectException("\"" + getName() + "\" element is not visible by path " + getActualPath());
        }
        //TODO: ???!!!
        super.initWebElement(browser);
    }
    
    @Override
    public void initWebElement(final Browser browser, final String text)
            throws PageObjectException {
        if (getState().equals(ElementState.CREATED)) {
            setBrowser(browser);
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
                                setActualPath(path);
                                setElement(thisElement);
                                setState(ElementState.INITIALIZED);
                                if (getElement().isDisplayed()) {
                                    browser.highlightElement(getElement());
                                    return true;    
                                }
                            }
                        }
                        return false;
                    }
                });
            } catch (Exception e) {
                log.error("element \"" + name + "\" is not found ");
                throw new PageObjectException("element \"" + name + "\" is not found by text: \"" + text + "\"", e);
            }
            if(getElement() == null)
                throw new PageObjectException("element \"" + name + "\" is not found.");
            if (!waitPresent()) 
                throw new PageObjectException("\"" + getName() + "\" element is not visible by path " + getActualPath());
        }
        //TODO: ???!!!
        super.initWebElement(browser, text);
    }
}
