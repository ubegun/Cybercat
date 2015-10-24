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
package org.cybercat.automation.test;

import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.components.Assert;

/**
 * @author Ubegun
 * 
 */
public abstract class AbstractFeature implements IVersionControl, IFeature{

    /**
     * This is the only constructor available. It will be called to create your feature.
     */
    protected AbstractFeature() {        
        super();
    }
    
    /**
     * Identifies version by default. can be redefined to change version feature.  
     */
    @Override
    public int getVersion() {
        return 0;
    }
  
    
    public void assertTrue(String message, boolean assertion) throws AutomationFrameworkException{
    	Assert.assertTrue(message, assertion);
    }

    public void assertEqual(String message, Object obj1, Object obj2) throws AutomationFrameworkException{
    	Assert.assertEqual(message, obj1, obj2);
    }

    public void assertContain(String message, String subString, String text) throws AutomationFrameworkException{
    	Assert.assertContain(message, subString, text);
    }

    public void assertNotEmpty(String message, String text) throws AutomationFrameworkException{
    	Assert.assertNotEmpty(message, text);
    }
    
}
