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
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.addons.common.logging.provider.LogLevel;
import org.cybercat.automation.components.AbstractPageObject.PathType;
import org.cybercat.automation.core.Browser;
import org.openqa.selenium.Point;


public class Button extends TextContainer {

    private final static Logger LOG = LogManager.getLogger(Button.class);
    
    public Button(String name, PathType type, String path) {
        super(name, type, path);
    }

    public Button(String name, PathType type, String[] path) {
        super(name, type, path);
    }
    
    @Override
    public void initWebElement(Browser browser) throws PageObjectException {
        super.initWebElement(browser);
    }

    @Override
    public void initWebElement(Browser browser, String text) throws PageObjectException {
        super.initWebElement(browser, text);
    }

    public void click() throws AutomationFrameworkException {
        if (getElement() != null) {
            try {
                super.highlightElement();
                LOG.info("click on: " + super.getName());
                LOG.log(LogLevel.ELEMENT_ACTION, "CLICK on element:  " + super.getName() +"; ELEMENT NAME: "+ super.getText() + "; ELEMENT PATH: "+ getPath()[0]);
                getElement().click();
            } catch (Exception e) {
                e.printStackTrace();
                LOG.error("Failed to click  on " + this.getName() + "element: " + this.getPath());
                throw new PageObjectException("Failed to click  on " + this.getName() + "element: " + this.getPath() ,e);
            }
        } else {
            LOG.error("Unable to click on " + this.getName() + "element: " + this.getPath());
            throw new PageObjectException("Unable to click on " + this.getName() + "element: " + this.getPath()[0]);
        }
    }

    
    public void submit() throws PageObjectException {
        if (getElement() != null) {
            try {
                super.highlightElement();
                getElement().submit();
                LOG.info("clicked on: " + super.getName());
                LOG.log(LogLevel.ELEMENT_ACTION, "CLICK on element:  " + super.getName() +"; ELEMENT NAME: "+ super.getText() + "; ELEMENT PATH: "+ getPath()[0]);
            } catch (Exception e) {
                throw new PageObjectException("Unable to click on " + this.getName() + " element: " + this.getPath()[0], e);
            }
        } else {
            LOG.error("Unable to click on " + this.getName() + "element: " + this.getPath());
            throw new PageObjectException("Unable to click on " + this.getName() + "element: " + this.getPath()[0]);
        }
    }
    

    
    public void clickOffset(Point cord) throws AutomationFrameworkException {
        LOG.info("Offset click to element: " + getElement().getTagName() + ", by coordinates: " + cord);
        LOG.log(LogLevel.ELEMENT_ACTION, "Offset click to element: " + super.getName() + ", by coordinates: " + cord +"; ELEMENT NAME: "+ super.getText() + "; ELEMENT PATH: "+ getPath()[0]);
        try{  
            Browser.getCurrentBrowser().getActions().moveToElement(getElement(), cord.getX(), cord.getY()).click().perform();
        }catch(Exception e){  
            LOG.error("Exception on clickOffset execution for " + getName() + " element ." + 
                    " \n\t "+ this.getClass().getSimpleName() + " class name, " +
                    " \n\t "+ this.getActualPath() + " xpath, ");
            throw new AutomationFrameworkException("Exception on clickOffset execution for " + getName() + " element ." + 
                    " \n\t "+ this.getClass().getSimpleName() + " class name, " +
                    " \n\t "+ this.getActualPath() + " xpath, ", e);
        }
    }

    public void dragOffset(int x,int y) throws AutomationFrameworkException {
        LOG.info("Offset drag of element: " + getElement().getTagName() + ", by coordinates: " + " x ="+x+" y="+y);
        LOG.log(LogLevel.ELEMENT_ACTION, "Frag offset of element: " + super.getName() + ", by coordinates: " + " x ="+x+" y="+y
                +"; ELEMENT NAME: "+ super.getText() + "; ELEMENT PATH: "+ getPath()[0]);
        try{ 
            Browser.getCurrentBrowser().getActions().moveToElement(getElement()).clickAndHold().moveByOffset(x,y).release().perform();
        }catch(Exception e){  
            LOG.error("Exception on dragOffset action for " + getName() + " element ." + 
                    " \n\t "+ this.getClass().getSimpleName() + " class name, " +
                    " \n\t "+ this.getActualPath() + " xpath, ");
            throw new AutomationFrameworkException("Exception on dragOffset action for " + getName() + " element ." + 
                    " \n\t "+ this.getClass().getSimpleName() + " class name, " +
                    " \n\t "+ this.getActualPath() + " xpath, ", e);
        }     
    }
    
    public String getValue() {
        return getAtributeByName("value");
    }

    public void hower() throws AutomationFrameworkException{
        try{
            LOG.log(LogLevel.ELEMENT_ACTION, "Hover on element " + super.getName() + "; ELEMENT NAME: " + super.getText() + "; ELEMENT PATH: "+ getPath()[0]);
            Browser.getCurrentBrowser().getActions().moveToElement(getElement()).perform();
        }catch(Exception e){  
            LOG.error("Exception on hower action for " + getName() + " element ." + 
                    " \n\t "+ this.getClass().getSimpleName() + " class name, " +
                    " \n\t "+ this.getActualPath() + " xpath, ");
            throw new AutomationFrameworkException("Exception on hower action for " + getName() + " element ." + 
                    " \n\t "+ this.getClass().getSimpleName() + " class name, " +
                    " \n\t "+ this.getActualPath() + " xpath, ", e);
        }     
            
    }

    public void doubleClick() throws AutomationFrameworkException{
        try{ 
        Browser.getCurrentBrowser().getActions().doubleClick(getElement()).perform();
        }catch(Exception e){  
            LOG.error("Exception on doubleClick action for " + getName() + " element ." + 
                    " \n\t "+ this.getClass().getSimpleName() + " class name, " +
                    " \n\t "+ this.getActualPath() + " xpath, ");
            throw new AutomationFrameworkException("Exception on doubleClick action for " + getName() + " element ." + 
                    " \n\t "+ this.getClass().getSimpleName() + " class name, " +
                    " \n\t "+ this.getActualPath() + " xpath, ", e);
        }     

    }
}
