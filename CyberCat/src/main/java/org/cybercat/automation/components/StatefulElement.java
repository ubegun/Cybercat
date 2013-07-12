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

import org.apache.log4j.Logger;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.browsers.Browser;
import org.cybercat.automation.components.processor.AbstractProcessor.AbstractCriteria;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;


/**
 * This is a synthetic element that represents visual state of a certain element on the web page.
 * 
 * @author Ubegun
 *
 * @param <T> - certain element like {@link Button}, {@link TextField} etc. 
 */
public class StatefulElement<T extends PageElement> extends PageElement {

	private static Logger log = Logger.getLogger(StatefulElement.class);
	
    /**
     * This is enumeration of states for page elements.  
     *
     */
    public enum PresentStatus {
        NOT_PRESENT_ON_DOM, PRESENT_NOT_VISIBLE, VISIBLE
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

    AbstractCriteria<Boolean> invisibilityCriteria = new AbstractCriteria<Boolean>(path) {

        @Override
        public ExpectedCondition<Boolean> getExpectedCondition(String path) {
            return ExpectedConditions.invisibilityOfElementLocated(processor.getByElement(path));
        }

        @Override
        public boolean onSuccess(Boolean elements, String path) {
            isValid &= elements.booleanValue(); 
            return false;
        }

        @Override
        public void onException(String path) {
            super.onException(path);
            presentStatus = PresentStatus.VISIBLE;
        }

        @Override
        public void onException(Exception e, String path) {
            super.onException(e, path);
            presentStatus = PresentStatus.VISIBLE;
        }
    };

    private PresentStatus presentStatus;
    private PresentStatus expectedStatus;
    private boolean isValid;

    private long tempImplicitTimeout;
    private long tempExplicitTimeout;
    
    public StatefulElement(T pElement) {
        super(pElement.name, pElement.path, pElement.processor);
        this.expectedStatus = PresentStatus.VISIBLE;
        tempImplicitTimeout = processor.getImplicitTimeout();
        tempExplicitTimeout = processor.getExplicitTimeout();
    }

    public StatefulElement(T pElement, PresentStatus expectedStatus) {
        this(pElement);
        this.expectedStatus = expectedStatus;
    }

    public StatefulElement(T pElement, PresentStatus expectedStatus, long timeToWait) {
        this(pElement);
        this.expectedStatus = expectedStatus;
        processor.setImplicitTimeout(timeToWait);
        processor.setExplicitTimeout(timeToWait);
    }

    @Override
    public void initWebElement(Browser browser) {
    	log.info(processor.getImplicitTimeout()+" sec. wait for element: "+super.name);
        setBrowser(browser);
        browser.callImplicitlyWait(processor.implicitTimeout);
        switch (expectedStatus) {
        case VISIBLE:
            processor.initWebElementByCriteria(browser, visibilityCriteria);
            break;        
        case NOT_PRESENT_ON_DOM:
            isValid = true;
            processor.setImplicitTimeout(0);
            processor.initWebElementByCriteria(browser, invisibilityCriteria);
            if(isValid){
                presentStatus = PresentStatus.NOT_PRESENT_ON_DOM;
            }  
            break;
        case PRESENT_NOT_VISIBLE:
            isValid = true;
            processor.setImplicitTimeout(0);
            processor.initWebElementByCriteria(browser, invisibilityCriteria);
            if(isValid){
                presentStatus = PresentStatus.PRESENT_NOT_VISIBLE;
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
