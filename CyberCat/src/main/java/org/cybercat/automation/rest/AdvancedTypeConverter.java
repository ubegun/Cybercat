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
package org.cybercat.automation.rest;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.mapped.DefaultConverter;

public class AdvancedTypeConverter extends DefaultConverter {

    @Override
    public void setEnforce32BitInt(boolean enforce32BitInt) {
        super.setEnforce32BitInt(enforce32BitInt);
    }

    @Override
    public Object convertToJSONPrimitive(String text) {
        if(StringUtils.isBlank(text))
            return null;
        return super.convertToJSONPrimitive(text);
    }

    
    
}
