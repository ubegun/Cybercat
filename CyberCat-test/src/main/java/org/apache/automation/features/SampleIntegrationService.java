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

package org.apache.automation.features;

import org.cybercat.automation.annotations.CCProperty;
import org.cybercat.automation.persistence.model.Identity;

/**
 * @author Ubegun
 *
 */
public class SampleIntegrationService implements  ISampleIntegrationService {

    
    @CCProperty("sample.soap.endpoint")
    private String endPoint; 

    @Override
    public Identity getIdentity() {
        return null;
    }

    @Override
    public int getVersion() {
        // TODO Auto-generated method stub
        return 0;
    }

    
    @Override
    public Object doSomething(){
        System.out.println(endPoint + " doSomthing()");        
        return new Object();
    }
    
    @Override
    public void doSomethingElse(){
        System.out.println(endPoint + "doSomthingElse()");
    }

    @Override
    public void setup() {
        // TODO Auto-generated method stub
        
    }
    
}
