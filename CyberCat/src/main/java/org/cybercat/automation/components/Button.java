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

import org.apache.log4j.Logger;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.browsers.Browser;
import org.cybercat.automation.components.AbstractPageObject.PathType;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;


public class Button extends TextContainer {

    private final static Logger LOG = Logger.getLogger(Button.class);
    
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

    public void click() throws PageObjectException {
        if (getElement() != null) {
            try {
                super.highlightElement();
                LOG.info("click on: " + super.getName());
                browser.executeScript("arguments[0].focus();", getElement());
                scrollElementToScreenCenter(getElement());
                getElement().click();
            } catch (Exception e) {
            	e.printStackTrace();
                LOG.error("Failed to click. Second try by JS Click()");
                fireClick();
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
            } catch (Exception e) {
                throw new PageObjectException("Unable to click on " + this.getName() + " element: " + this.getPath()[0], e);
            }
        } else {
            LOG.error("Unable to click on " + this.getName() + "element: " + this.getPath());
            throw new PageObjectException("Unable to click on " + this.getName() + "element: " + this.getPath()[0]);
        }
    }
    
    public void fireClick(){
        super.highlightElement();
        browser.executeScript("arguments[0].click();", getElement());

    }

    public void focusFireClick(){
        super.highlightElement();
        browser.executeScript("var dispatchMouseEvent = function(target, var_args) {" +
                "  var e = document.createEvent('MouseEvents');" +
                "  e.initEvent.apply(e, Array.prototype.slice.call(arguments, 1));" +
                "  target.dispatchEvent(e);" +
                "};" +
                "arguments[0].focus();"+
                "if( document.createEvent ){" +
                "dispatchMouseEvent(arguments[0], 'mouseover', true, true);" +
                "dispatchMouseEvent(arguments[0], 'mousedown', true, true);" +
                "dispatchMouseEvent(arguments[0], 'mouseup', true, true);" +
                "}" +
                "else if( document.createEventObject ){" +
                "arguments[0].fireEvent('onmouseover');" +
                "arguments[0].fireEvent('onmousedown');" +
                "arguments[0].fireEvent('onmouseup');" +
                "}", getElement());
    }


    private void scrollElementToScreenCenter(WebElement element){
        try{
            String scrollFunction = "var viewportWidth = jQuery(window).width(),"+
                    "viewportHeight = jQuery(window).height(),"+
                    "$foo = jQuery(arguments[0]),"+
                    "elWidth = $foo.width(),"+
                    "elHeight = $foo.height(),"+
                    "elOffset = $foo.offset();"+
                    "jQuery(window)"+
                    ".scrollTop(elOffset.top + (elHeight/2) - (viewportHeight/2))"+
                    ".scrollLeft(elOffset.left + (elWidth/2) - (viewportWidth/2));";

        
            browser.executeScript(scrollFunction, element);
            LOG.info("Elemet is scrolled to the center of the screen, using jQuery");
        }
        // assuming WebDriverException here in case of there is no on jQuery on page
        catch(Exception e){
            LOG.info("Unable to scroll element to the screen center, jQuery is not defined on page");
        }
    }
    
    public void clickOffset(Point cord) {
        LOG.info("Offset click to element: " + getElement().getTagName() + ", by coordinates: " + cord);
        browser.getActions().moveToElement(getElement(), cord.getX(), cord.getY()).click().perform();
    }

    public void dragOffset(int x,int y) {
        LOG.info("Offset drag of element: " + getElement().getTagName() + ", by coordinates: " + " x ="+x+" y="+y);
        browser.getActions().moveToElement(getElement()).clickAndHold().moveByOffset(x,y).release().perform();
    }
    
    public String getValue() {
        return getAtributeByName("value");
    }

    public void hower(){
        browser.getActions().moveToElement(getElement()).perform();
    }

    public void doubleClick(){
        browser.getActions().doubleClick(getElement()).perform();
    }
}
