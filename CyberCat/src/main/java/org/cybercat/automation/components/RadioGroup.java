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
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.components.AbstractPageObject.PathType;
import org.cybercat.automation.components.processor.AbstractProcessor;
import org.cybercat.automation.core.Browser;


public class RadioGroup extends PageElement {

    private final static Logger log = Logger.getLogger(RadioGroup.class);

    // private HashMap<String, Button> entries = new HashMap();
    // private RemoteWebDriver driver;
    //
    // public RadioGroup(String name, HashMap<String, Button> entries ) {
    // super(name , entries[0].getProcessorType(), "");
    // this.entries = entries;
    // }

    protected Button[] entries;

    public RadioGroup(String name, Button[] entries) {
        super(name, entries[0].getProcessorType(), "");
        this.entries = entries;
    }
    
    public RadioGroup(String name, PathType pathType, String path) {
        super(name, pathType, path);
        
        //this.entries = entries;
    }
    
    public void click(int index) throws AutomationFrameworkException {
        entries[index].setProcessor(processor);
        entries[index].initWebElement(Browser.getCurrentBrowser());
        entries[index].click();
        log.info("Selected: " + entries[index].getName());
    }

    // when declaring radio button its name must contain label specified on UI
    // in other case this method will not work
    // FIXME works incorrect in case "Male", "Female" strings
    public void clickByName(String name) throws AutomationFrameworkException {
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].getName().toLowerCase().contains(name.toLowerCase())) {
                click(i);
                return;
            }
        }
        throw new PageObjectException("can not click on Radio by name " + name);
    }

    public void jsClickByName(String name) throws AutomationFrameworkException {
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].getName().toLowerCase().contains(name.toLowerCase())) {
                entries[i].focusFireClick();
                return;
            }
        }
        throw new PageObjectException("can not click on Radio by name " + name);
    }

    @Override
    public void initWebElement(Browser browser) throws PageObjectException {
        for (int i = 0; i < entries.length; i++) {
            entries[i].initWebElement(browser);
        }
    }

    @Override
    public void setProcessor(AbstractProcessor processor) {
        super.processor = processor;
        for (int i = 0; i < entries.length; i++) {
            entries[i].setProcessor(processor);
        }
    }

    @Override
    public void detach() {
        super.detach();
        for (Button but : entries)
            but.detach();
    }
    
    /**
     * Selects first unchecked (i.e. its value is not 'checked' ) radio button in radio group 
     * @throws AutomationFrameworkException 
     */
    public void clickFirstUnchecked() throws AutomationFrameworkException {
        for (int i = 0; i < entries.length; i++)
            if (!"true".equals(entries[i].getAtributeByName("checked"))) {
                entries[i].click();
                return;
            }
    }
    
    /**
     * Returns index of selected entry in radio group
     * @return index of selected entry or -1 if there isn`t selected entry
     */
    public int getSelectedEntryIndex() {
        for (int i = 0; i < entries.length; i++)
            if ("true".equals(entries[i].getAtributeByName("checked"))) {
                return i;
            }
        return -1;
    }
    
    /**
     * Returns  selected entry in radio group
     * @return selected entry
     */
    public Button getSelectedEntry() {
        int index = getSelectedEntryIndex();
        if(index >=0)
            return entries[index];
        else 
            return null;
    }
    
    /**
     * Returns  entry in radio group by name
     * @param name - name of the entry
     * @return entry with name specified in parameter
     */
    public Button getEntryByName(String name) {
        for (int i = 0; i < entries.length; i++)
            if (entries[i].getName().equals(name))
                return entries[i];
        return null;
    }

    /**
     * Checks if specified in parameter is selected
     * @param name - name of the entry
     * @return true - if entry is selected otherwise returns false
     */
    public boolean isEntrySelected(String name) {
        
        if (getSelectedEntry() == null)
            return false;
        
        if(getSelectedEntry().getName().equals(name))
            return true;
        else 
            return false;

    }

    public Button getEntry(int index){
        return entries[index];
    }

    public Button[] getEntries() {
        return entries;
    }
}
