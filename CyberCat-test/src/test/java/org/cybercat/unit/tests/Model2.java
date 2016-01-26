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

package org.cybercat.unit.tests;

import org.cybercat.automation.persistence.model.Entity;
import org.cybercat.automation.utils.CommonUtils;

/**
 * @author Ubegun
 *
 */
public class Model2 implements Entity{

    private long id = CommonUtils.genGuid();
    
    public void setId(long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.cybercat.automation.persistence.model.Entity#getId()
     */
    @Override
    public long getId() {
        return id ;
    }

}
