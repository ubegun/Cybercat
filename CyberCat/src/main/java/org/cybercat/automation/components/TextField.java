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
import org.cybercat.automation.utils.CommonUtils;
import org.openqa.selenium.Keys;


public class TextField extends Button {

    private final static Logger log = LogManager.getLogger(TextField.class);

    public TextField(String name, PathType type, String path) {
        super(name, type, path);
    }

    public TextField(String name, PathType type, String[] path) {
        super(name, type, path);
    }

    /**
     * Types text into text field (Value is cleared before new text is added)
     * 
     * @param text
     * @throws PageObjectException
     */
    public void typeText(String text) throws PageObjectException {
        if (super.getElement() != null) {
            // highlightElement();
            super.getElement().clear();
            super.getElement().sendKeys(text);
            log.info(getName() + " text field was filled with the following text: " + text);
            log.log(LogLevel.ELEMENT_ACTION, getName() + " text field was filled with the following text: " + text);
        } else {
            throw new PageObjectException(getName() + " text field doesn't exist");
        }
    }

    public void clearSendKeys(String keyName) throws PageObjectException {
        getElement().clear();
        sendKeys(keyName);
    }
    
    public void clearTextField() throws PageObjectException {
        log.info("clear "+getName()+" text field");
        log.log(LogLevel.ELEMENT_ACTION, "clear "+getName()+" text field");
        getElement().clear();        
    }

    public void sendKey(String keyName) throws PageObjectException {
        if (super.getElement() != null) {
            // highlightElement();
            super.getElement().sendKeys(Keys.valueOf(keyName));
            log.info(getName() + " text field was filled with the following text: " + keyName);
            log.log(LogLevel.ELEMENT_ACTION, getName() + " text field was filled with the following text: " + keyName);
        } else {
            throw new PageObjectException(getName() + " text field doesn't exist");
        }
    }

    public void setValue(AbstractPageObject thisPage, String text) throws PageObjectException {
        if (super.getElement() != null) {
            super.getElement().clear();
            try {
                setAttribute(thisPage, "value", text);
            } catch (AutomationFrameworkException e) {
                throw new PageObjectException(e);
            }
            log.info(getName() + " text field was filled with the following text: " + text);
            log.log(LogLevel.ELEMENT_ACTION, getName() + " text field was filled with the following text: " + text);
        } else {
            throw new PageObjectException(getName() + " text field doesn't exist");
        }
    }

    /**
     * Types text into frames that represent WYSIWIG editors text area
     * 
     * @param text
     * @throws AutomationFrameworkException 
     */
    public void typeTextIntoFrame(String text) throws AutomationFrameworkException {
        if (super.getElement() != null) {
         String query = String.format("arguments[0].innerHTML = '%s'", text);
         Browser.getCurrentBrowser().executeScript(query, super.getElement());
         log.info(getName() + " text field was filled with the following text: " + text);
         log.log(LogLevel.ELEMENT_ACTION, getName() + " text field was filled with the following text: " + text);
        } else {
         throw new PageObjectException(getName() + " text field doesn't exist");
        }
       }  
    
    public void addTextIntoFrame(String text) throws PageObjectException {
        if (super.getElement() != null) {            
            super.getElement().sendKeys(text);
            log.info(getName() + " text field was filled with the following text: " + text);
            log.log(LogLevel.ELEMENT_ACTION, getName() + " text field was filled with the following text: " + text);
        } else {
            throw new PageObjectException(getName() + " text field doesn't exist");
        }
    }   

    public void selectRandomItem(int range) throws AutomationFrameworkException {
        int index = CommonUtils.generateNumber(range);
        if (index == 1)
            index++;
        super.highlightElement();
        this.click();
        for (int i = 0; i < index; i++) {
            this.sendKey("ARROW_DOWN");
        }
    }

    public void selectItem(int itemNumber) throws AutomationFrameworkException {
        this.click();
        for (int i = 0; i < itemNumber; i++) {
            this.sendKey("ARROW_DOWN");
        }
    }

    public void pressEnter() throws PageObjectException {
        this.sendKey("ENTER");
    }

    @Override
    public void initWebElement(Browser browser) throws PageObjectException {
        super.initWebElement(browser);
    }

    @Override
    public void initWebElement(Browser browser, String text) throws PageObjectException {
        super.initWebElement(browser, text);
    }

}
