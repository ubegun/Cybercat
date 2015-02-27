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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.PageElementRuntimeException;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.components.AbstractPageObject.PathType;
import org.cybercat.automation.components.processor.AbstractProcessor;
import org.cybercat.automation.components.processor.AbstractProcessor.AbstractCriteria;
import org.cybercat.automation.core.Browser;
import org.cybercat.automation.utils.CommonUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class GroupElements<T extends PageElement> extends PageElement{

    private static Logger log = LogManager.getLogger(GroupElements.class);

    private List<T> subElements = new ArrayList<T>();
    private Class<T> eType;

    public GroupElements(String name, PathType type, String path, Class<T> eType) {
        super(name, type, path);
        this.eType = eType;
    }

    public GroupElements(String name, PathType type, String[] path, Class<T> eType) {
        super(name, type, path);
        this.eType = eType;
    }

    public GroupElements(String name, String path, AbstractProcessor processor, Class<T> eType) {
        super(name, path, processor);
        this.eType = eType;
    }

    public GroupElements(String name, String[] path, AbstractProcessor processor, Class<T> eType) {
        super(name, path, processor);
        this.eType = eType;
    }

    @Override
    public void detach() {
        subElements = new ArrayList<T>();
        super.detach();
    }

    @Override
    public void initWebElement(Browser browser) throws PageObjectException {
        if (getState().equals(ElementState.CREATED)) {
                processor.initWebElementByCriteria(browser, new AbstractCriteria<List<WebElement>>(getPath()) {

                    @Override
                    public ExpectedCondition<List<WebElement>> getExpectedCondition(String path) {
                        return ExpectedConditions.presenceOfAllElementsLocatedBy(processor.getByElement(path));
                    }
                    
                    @Override
                    public boolean onSuccess(List<WebElement> elements, String path) {
                        if (elements != null && elements.size() > 0) {
                            setActualPath(path);
                            int i = 0;
                            for(WebElement element : elements){
                                subElements.add(createNewElement(path, i++, element));
                            }
                            return true;
                        }
                        return false;
                    }

                });
            if (subElements.size() == 0) {
                log.error("element \"" + getName() + "\" is not found ");
                throw new PageObjectException("element \"" + getName() + "\" is not found.");
            }
            setState(ElementState.INITIALIZED);            
        }
        
   }
   
    
    @Override
    public void initWebElement(Browser driver, String text) throws PageObjectException {
        throw new PageElementRuntimeException("Unsupportable method");
    }    

    
    private T createNewElement(String path, int index, WebElement wElement){
        try {            
            Constructor<T> cons =  eType.getConstructor(String.class, PathType.class, String.class);
            T result = cons.newInstance(this.getName() + "#" + index, this.getProcessorType(), path);
            result.setElement(wElement);
            result.setState(ElementState.INITIALIZED);
            return result; 
        } catch (Exception e) {
            log.error(e);
        }
        return null; 
    }

    /**
     * Returns sub-elements
     */
    public List<T> getElements(){
        return subElements;
    }
    
    public T getElementRandom(){
        return subElements.get(CommonUtils.generateNumber(subElements.size() - 1));
    }
    
    /**
     * @param text - regular expression 
     * @return sub-element matched by this text fragment 
     */
    public T getElementByText(String text){
        for(T element: subElements){
            try {
                if(element.getText().matches(text))
                    return element;
            } catch (PageObjectException e) {
                log.error(e);
            }
        }
        return null;
    }
    
    public boolean isPresent(String elementText) throws PageObjectException{
        T result = getElementByText(elementText);
        if(result == null){
            log.debug("ELEMENT "+ getName() +" WITH TEXT "+elementText+ "IS NOT DEFINED");
            throw new PageObjectException("ELEMENT "+ getName() +" WITH TEXT "+elementText+ "IS NOT DEFINED");
    }
              return result.isDisplayed();
        
    }
    
    public String[] getVisibleElementsText() throws PageObjectException{
        List<String> visibleElements = new ArrayList<>();
        for(T e :subElements){
            if(e.isDisplayed())
                visibleElements.add(e.getText());
        }
        
        return visibleElements.toArray(new String[visibleElements.size()]);
    }

    public void dragByOffset(PageElement to, int xOffset, int yOffset) throws AutomationFrameworkException {
        Browser.getCurrentBrowser().getActions()
                .moveToElement(getElement())
                .clickAndHold(getElement())
                .moveToElement(to.getElement(), xOffset, yOffset).perform();
    }

    public void releaseDragged(PageElement draggedElement) throws AutomationFrameworkException {
        Browser.getCurrentBrowser().getActions()
                .release().perform();
    }
    
}
