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
package org.cybercat.automation;

import org.cybercat.automation.components.AbstractPageObject;
import org.cybercat.automation.components.AbstractPageObject.PathType;
import org.cybercat.automation.components.processor.AbstractProcessor;

/**
 * Creates and initializes page classes that extend AbstractPageObject 
 */
public interface PageFactory {
        
    
    <T extends AbstractPageObject> T createPage(Class<T> page) throws PageObjectException;
    
    /**
     * Returns base url
     */
    String getBaseUrl();
    
    /**
     * Returns current URL
     * @throws AutomationFrameworkException 
     */
    String getCurrentUrl() throws AutomationFrameworkException;

    /**
     * Defines base url
     */
    void setBaseUrl(String baseUrl);
    

    /**
     * Returns processor creating WebElements according to predefined type
     * @param type - type is defined depending on addressing type to html pages elements
     * (xpath, css name, id, name) 
     */
    <T extends AbstractProcessor> T createElementProcessor(PathType type);

}
