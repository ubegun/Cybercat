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
package org.cybercat.automation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ubegun
 * 
 * The annotation for a feature test case. 
 * The feature - the does it mean, class what contains test steps based 
 * on using page object/ page fragments.      
 * 
 * for example:  
 * <pre>
 * {@code
 *   
 *  class FooFeature extends AbstractFeature implements IFooFeature{ 
 *  
 *  ...
 *  <br> 
 *  @CCPageObject
 *  <br>
 *  private WelcomePage welcomePage;
 *  
 *</pre>
 *
 *  welcomPage will be initialized on first call from the method in this class
 *    
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( {ElementType.FIELD, ElementType.TYPE} )
public @interface CCFeature {

}
