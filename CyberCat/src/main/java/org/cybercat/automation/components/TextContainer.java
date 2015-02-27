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



import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.addons.common.logging.provider.LogLevel;
import org.cybercat.automation.components.AbstractPageObject.PathType;
import org.cybercat.automation.core.Browser;


public class TextContainer extends PageElement {
    
    private final static Logger log = LogManager.getLogger(TextContainer.class);
    
    public TextContainer(String name, PathType type, String path) {
        super(name, type, path);
    }

    public TextContainer(String name, PathType type, String[] path) {
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

    public String getText() throws PageObjectException {
        try {
            String text = getElement().getText();
            log.info("element text is: "+text);
            log.log(LogLevel.ELEMENT_ACTION, "Get element text: " + getName() + "[path: " + getPath()[0] + "] ELEMENT TEXT is: " + text);
            return text;
        } catch (Exception e) {
            throw new PageObjectException("Unable to get text in element: " + getName() + " by path:" + getPath()[0], e);
        }
    }

    public boolean verifyText(String text) throws PageObjectException {
        return StringUtils.equalsIgnoreCase(getElement().getText(), text) || StringUtils.equalsIgnoreCase(getElement().getAttribute("value"), text);
    }

}
