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

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.annotations.AnnotationBuilder;
import org.cybercat.automation.core.TestStepAspect;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

/**
 * @author Ubegun
 * 
 */
public abstract class AbstractFeature implements IVersionControl, IFeature{

    private static Logger log = Logger.getLogger(AbstractFeature.class);

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
  
}
