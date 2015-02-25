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

package org.cybercat.automation.core.integration;

import org.cybercat.automation.core.Platform;
import org.cybercat.automation.persistence.model.Identity;
import org.cybercat.automation.test.IVersionControl;

/**
 * @author Ubegun
 * 
 */
public interface IIntegrationService extends IVersionControl {

    // TODO: More information is needed about different algorithms of authorization
    abstract Identity getIdentity();

    /**
     * This method should contain initialization block which is executed after creating an object and after
     * initialization of all annotated fields of this object
     */
    void setup();

    @Override
    default boolean isSupportsPlatform(Platform platform) {
        return true;
    }

    @Override
    default Platform[] getPlatforms() {
        return new Platform[]{Platform.WEB, Platform.TABLET, Platform.MOBILE};
    }
}