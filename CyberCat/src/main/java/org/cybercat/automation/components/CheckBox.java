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

import org.cybercat.automation.components.AbstractPageObject.PathType;

public class CheckBox extends PageElement {

    public CheckBox(String name, PathType type, String path) {
        super(name, type, path);
    }

    public void check() {
        if (!getElement().isSelected()) {
            getElement().click();
        }
    }
    
    public void uncheck() {
        if (getElement().isSelected()) {
            getElement().click();
        }
    }
    
    public boolean isChecked() {
        return getElement().isSelected();
    }
}
