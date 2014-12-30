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

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceManager {

    public static ResourceBundle getLocators(Locale locale) {
        return ResourceBundle.getBundle("Locators", locale);
    }

    public static ResourceBundle getTestMetaData() {
        return ResourceBundle.getBundle("MetaData");
    }

    public static String getJsAsString(String scriptName) throws PageObjectException {
        try {
            InputStream stream = ClassLoader.getSystemResourceAsStream("javascript/" + scriptName);
            StringBuffer result = new StringBuffer();
            int i;
            while ((i = stream.read()) != -1) {
                result.append((char) i);
            }
            return result.toString();
        } catch (IOException e) {
            throw new PageObjectException(e);
        }

    }

}
