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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.components.AbstractPageObject.PathType;
import org.cybercat.automation.core.Browser;
import org.openqa.selenium.WebElement;

/**
 * jQuery button 
 * @author Ubegun
 */
public class JQButton extends Button {

    private final static Logger LOG = Logger.getLogger(JQButton.class);
    
    /**
     * @param name
     * @param type
     * @param path
     */
    public JQButton(String name, PathType type, String path) {
        super(name, type, path);
    }

    public JQButton(String name, PathType type, String[] path) {
        super(name, type, path);
    }

    /**
     * Execute JQuere
     */
    public void focusClick() throws AutomationFrameworkException {
        if (getElement() != null) {
            try {
                super.highlightElement();
                LOG.info("click on: " + super.getName());
                executeScript("arguments[0].focus();", getElement());
                scrollElementToScreenCenter(getElement());
                getElement().click();
            } catch (Exception e) {
                e.printStackTrace();
                LOG.error("Failed to click  on " + this.getName() + "element: " + StringUtils.join(this.getPath(), " | "));
                fireClick();
            }
        } else {
            LOG.error("Unable to click on " + this.getName() + "element: " + StringUtils.join(this.getPath(), " | "));
            throw new PageObjectException("Unable to click on " + this.getName() + "element: " + StringUtils.join(this.getPath(), " | "));
        }
    }
    
    
    public void fireClick() throws AutomationFrameworkException{
        super.highlightElement();
        executeScript("arguments[0].click();", getElement());
    }

    public void focusFireClick() throws AutomationFrameworkException{
        super.highlightElement();
        executeScript("var dispatchMouseEvent = function(target, var_args) {" +
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
            Browser.getCurrentBrowser().executeScript(scrollFunction, element);
            LOG.info("Elemet is scrolled to the center of the screen, using jQuery");
        }
        // assuming WebDriverException here in case of there is no on jQuery on page
        catch(Exception e){
            LOG.error("Unable to scroll element to the screen center, jQuery is not defined on page for " + getName() + " element ." + 
                    " \n\t "+ this.getClass().getSimpleName() + " class name, " +
                    " \n\t "+ this.getActualPath() + " xpath, ");
        }
    }    
}
