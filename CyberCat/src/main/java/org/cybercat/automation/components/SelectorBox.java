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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.addons.common.logging.provider.LogLevel;
import org.cybercat.automation.components.AbstractPageObject.PathType;
import org.cybercat.automation.utils.CommonUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;


public class SelectorBox extends PageElement {

    private static Logger LOG = LogManager.getLogger(SelectorBox.class);

    public SelectorBox(String name, PathType type, String path) {
        super(name, type, path);
    }

    public void typeTextInSelectBox(String text) throws PageObjectException {
        if (getElement() != null) {
            getElement().sendKeys(text);
            LOG.info("Component name:" + super.getName() + "  entered value:" + text);
            LOG.log(LogLevel.ELEMENT_ACTION, "Type text in SelectBox. Component name:" + super.getName() + "  entered value:" + text);
        } else {
            throw new PageObjectException("Unable to type text in an element: " + getPath()[0]);
        }
    }

    public void selectFromSelectBox(String value) throws PageObjectException {
        selectFromSelectBox(value, false);
    }
    
    public void selectFromSelectBox(String value, Boolean checkSelected) throws PageObjectException {
        Select selectBox = new Select(getElement());
        try {
            if (checkSelected){
                
                if (StringUtils.equalsIgnoreCase(getFirstSelectedValue(),value)){
                    LOG.info("Value already selected in SelectBox, value= "+value);
                    LOG.log(LogLevel.ELEMENT_ACTION, "Select from select box -> Value already selected in SelectBox, value= "+value);
                    return;
                }
            }
            selectBox.selectByValue(value);
            LOG.info("Component name:" + super.getName() + "  selected value:" + value);
            LOG.log(LogLevel.ELEMENT_ACTION, "Select from select box -> Component name:" + super.getName() + "  selected value:" + value);
        } catch (Exception e) {
            throw new PageObjectException("Component name:" + super.getName() + "  by path:" + value, e);
        }
    }
    
    
    
    public void selectFromSelectorBoxContains(String value) throws PageObjectException {
        selectFromSelectorBoxContains(value, false);
    }
    
    public void selectFromSelectorBoxContains(String value, boolean checkSelected) throws PageObjectException {
        Select selectBox = new Select(getElement());
        try {
                List<String> elementsText = getOptionsText();
                
                for (String elementText:elementsText){
                        
                        if (StringUtils.containsIgnoreCase(elementText, value)){
                                if (checkSelected){
                                    String selected = getFirstSelectedValue();
                                    if (StringUtils.equalsIgnoreCase(selected,value)){
                                        LOG.info("Selected option equals to value to select: "+value);
                                        return;
                                    }
                                }
                                selectBox.selectByIndex(elementsText.lastIndexOf(elementText));
                                
                                LOG.info("Component name:" + super.getName() + "  selected value:" + elementText);
                                LOG.log(LogLevel.ELEMENT_ACTION, "Select from select box -> Component name:" + super.getName() + "  selected value:" + value);

                                return;
                        }
                }
        } catch (Exception e) {
            throw new PageObjectException("Component name:" + super.getName() + "  by path:" + super.getPath()[0], e);
        }
    }

    public String selectRandomValue() throws PageObjectException {
        String value = "";
        Select select = new Select(getElement());
        // select.deselectAll();
        Integer size = select.getOptions().size();
        Random random = new Random();
        Integer index = random.nextInt(size - 1);
        try {
            value = select.getOptions().get(index).getText();
            select.selectByIndex(index);   
            LOG.info("Component name:" + super.getName() + "  selected value:" + value);
            LOG.log(LogLevel.ELEMENT_ACTION, "Select from select box -> Component name:" + super.getName() + "  selected value:" + value);
            return value;
        } catch (Exception e) {
            LOG.error("Component name:" + super.getName() + "  by path:" + value, e);
            throw new PageObjectException("Component name:" + super.getName() + "  by path:" + value, e);
        }
    }
    
    public String getRandomValue(){
        Select select = new Select(getElement());       
        Integer size = select.getOptions().size();
        int index=CommonUtils.generateNumber(size)-1;
        String value =select.getOptions().get(index).getText();
        return value;
    }

    public void selectorBox(String valueToSelect) {
        Select select = new Select(getElement());
        List<WebElement> options = select.getOptions();
        for (WebElement we : options) {
            if (we.getText().equals(valueToSelect)) {
                we.click();
                LOG.info("Component name:" + super.getName() + "  selected value:" + valueToSelect);
                LOG.log(LogLevel.ELEMENT_ACTION, "Select from select box -> Component name:" + super.getName() + "  selected value:" + valueToSelect);
                break;
            }
        }
    }

    public String getFirstSelectedValue() {
        Select select = new Select(getElement());
        return select.getFirstSelectedOption().getText();
    }
    
    public Integer getSize(){
    	Select select = new Select(getElement());
    	return select.getOptions().size();
    }
    
    public List<String> getOptionsText(){
    	Select select = new Select(getElement());
    	
    	List<WebElement> elements = select.getOptions();
    	
    	List<String> out = new ArrayList<>();
    	
    	for (WebElement element:elements){
    		out.add(element.getText());
    	}
    	
    	return out;
    }

    public void selectByOptionValue(String optionValue){
        List<WebElement> options = new Select(getElement()).getOptions();
        for(WebElement option:options){
               if(StringUtils.containsIgnoreCase(option.getAttribute("value"),optionValue)){
                   option.click();
                   LOG.info("Selected value: " + option.getAttribute("value"));
                   LOG.log(LogLevel.ELEMENT_ACTION, "Select from select box -> Component name:" + super.getName() + "  selected value:" + option.getAttribute("value"));
                   break;
               }
        }


    }

}
