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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.components.processor.AbstractProcessor.AbstractCriteria;
import org.cybercat.automation.core.Browser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * This is a synthetic element that represents visual state of a certain element on the web page.
 *
 * @param <T> - certain element like {@link Button}, {@link TextField} etc.
 * @author Ubegun
 */
public class StatefulElement<T extends PageElement> extends PageElement {

	private static Logger log = LogManager.getLogger(StatefulElement.class);

    /**
     * This is enumeration of states for page elements.
     */
    public enum PresentStatus {
        NOT_PRESENT_ON_DOM, PRESENT_NOT_VISIBLE, VISIBLE, ATTRIBUTE_PRESENT, ATTRIBUTE_NOT_PRESENT,
    }

    AbstractCriteria<List<WebElement>> visibilityCriteria = new AbstractCriteria<List<WebElement>>(path) {

        @Override
        public ExpectedCondition<List<WebElement>> getExpectedCondition(String path) {
            return ExpectedConditions.presenceOfAllElementsLocatedBy(processor.getByElement(path));
        }

        @Override
        public boolean onSuccess(List<WebElement> elements, String path) {
            if (elements != null && !elements.isEmpty()) {
                setElement(elements.get(0));
                setState(ElementState.INITIALIZED);
                if (waitPresent()) {
                    presentStatus = PresentStatus.VISIBLE;
                } else {
                    presentStatus = PresentStatus.PRESENT_NOT_VISIBLE;
                }
                return true;
            }
            return false;
        }

        @Override
        public void onException(String path) {
            super.onException(path);
            presentStatus = PresentStatus.NOT_PRESENT_ON_DOM;
        }

        @Override
        public void onException(Exception e, String path) {
            super.onException(e, path);
            presentStatus = PresentStatus.NOT_PRESENT_ON_DOM;
        }
    };

    AbstractCriteria<List<WebElement>> notPresentOnDomCriteria = new AbstractCriteria<List<WebElement>>(path) {

        @Override
        public ExpectedCondition<List<WebElement>> getExpectedCondition(String path) {
            return ExpectedConditions.presenceOfAllElementsLocatedBy(processor.getByElement(path));
        }

        @Override
        public boolean onSuccess(List<WebElement> elements, String path) {
            if (elements != null && !elements.isEmpty()) {
                setElement(elements.get(0));
                setState(ElementState.INITIALIZED);
                if (waitPresent()) {
                    presentStatus = PresentStatus.VISIBLE;
                } else {
                    presentStatus = PresentStatus.PRESENT_NOT_VISIBLE;
                }
                return false;
            }
            return true;
        }

        @Override
        public void onException(String path) {
            super.onException(path);
            presentStatus = PresentStatus.PRESENT_NOT_VISIBLE;
        }

        @Override
        public void onException(Exception e, String path) {
            super.onException(e, path);
                presentStatus = PresentStatus.NOT_PRESENT_ON_DOM;
        }
    };

    AbstractCriteria<Boolean> attributeCriteria = new AbstractCriteria<Boolean>(path) {
        @Override
        public ExpectedCondition<Boolean> getExpectedCondition(final String path) {

            return new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(@Nullable WebDriver webDriver) {
                    for (Map.Entry<String, String> entry : attributes.entrySet()) {
                        boolean isAttribute = webDriver.findElement(By.xpath(path))
                                .getAttribute(entry.getKey()).equalsIgnoreCase(entry.getValue().trim());
                        if (isAttribute != isAttributeExpected)
                            throw new NoSuchElementException();
                    }
                    return true;
                }
            };

        }

        @Override
        public boolean onSuccess(Boolean elements, String path) {
            return isValid = elements;
        }

        @Override
        public void onException(String path) {
            super.onException(path);
            setPresentStatusOnException();
        }

        @Override
        public void onException(Exception e, String path) {
            super.onException(e, path);
            setPresentStatusOnException();
        }

        private void setPresentStatusOnException() {
            if(isAttributeExpected)
                presentStatus = PresentStatus.ATTRIBUTE_NOT_PRESENT;
            else
                presentStatus = PresentStatus.ATTRIBUTE_PRESENT;
        }
    };


    private PresentStatus presentStatus;
    private PresentStatus expectedStatus;
    private boolean isValid;

    private long tempImplicitTimeout;
    private long tempExplicitTimeout;

    private Map<String, String> attributes;
    private boolean isAttributeExpected;

    public StatefulElement(T pElement) {
        this(pElement, PresentStatus.VISIBLE, Collections.<String,String>emptyMap(), 0);
    }

    public StatefulElement(T pElement, PresentStatus expectedStatus) {
        this(pElement, expectedStatus, Collections.<String, String>emptyMap(), 0);
    }

    public StatefulElement(T pElement, PresentStatus expectedStatus, long timeToWait) {
        this(pElement, expectedStatus, Collections.<String, String>emptyMap(), timeToWait);
    }

    public StatefulElement(T pElement, PresentStatus expectedStatus, Map<String, String> attributes) {
        this(pElement, expectedStatus, attributes, 0);
    }

    public StatefulElement(T pElement, PresentStatus expectedStatus, Map<String, String> attributes, long timeToWait) {
        super(pElement.name, pElement.path, pElement.processor);
        this.expectedStatus = expectedStatus;
        this.attributes = attributes;

        tempImplicitTimeout = processor.getImplicitTimeout();
        tempExplicitTimeout = processor.getExplicitTimeout();

        processor.setImplicitTimeout(timeToWait);
        processor.setExplicitTimeout(timeToWait);
    }


    @Override
    public void initWebElement(Browser browser) {
        log.info(processor.getImplicitTimeout() + " sec. wait for element: " + super.name);
        browser.callImplicitlyWait(processor.implicitTimeout);
        switch (expectedStatus) {
            case VISIBLE:
                processor.initWebElementByCriteria(browser, visibilityCriteria);
                break;
            case NOT_PRESENT_ON_DOM:
                isValid = true;
                processor.setImplicitTimeout(0);
                processor.initWebElementByCriteria(browser, notPresentOnDomCriteria);
                break;
            case PRESENT_NOT_VISIBLE:
                isValid = true;
                processor.setImplicitTimeout(0);
                processor.initWebElementByCriteria(browser, visibilityCriteria);
                break;
            case ATTRIBUTE_PRESENT:
                isAttributeExpected = true;
                processor.initWebElementByCriteria(browser, attributeCriteria);
                if(isValid) {
                    presentStatus = PresentStatus.ATTRIBUTE_PRESENT;
                }
                break;
            case ATTRIBUTE_NOT_PRESENT:
                isAttributeExpected = false;
                processor.initWebElementByCriteria(browser, attributeCriteria);
                if(isValid){
                    presentStatus = PresentStatus.ATTRIBUTE_NOT_PRESENT;
                }
                break;
            default:
                break;
        }
        processor.setExplicitTimeout(tempExplicitTimeout);
        processor.setImplicitTimeout(tempImplicitTimeout);
        browser.callImplicitlyWait(processor.implicitTimeout);
    }

    public boolean isValid() {
        return presentStatus == expectedStatus;
    }

    public PresentStatus getPresentStatus() {
        return presentStatus;
    }

    @Override
    public void initWebElement(Browser browser, String text) throws PageObjectException {
        super.initWebElement(browser, text);
    }
}
