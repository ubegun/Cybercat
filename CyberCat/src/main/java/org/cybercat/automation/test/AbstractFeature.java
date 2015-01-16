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


import org.cybercat.automation.core.Platform;

/**
 * @author Ubegun
 *
 */
public abstract class AbstractFeature implements IVersionControl, IFeature{

    protected Platform[] platforms;

    /**
     * This is the only constructor available. It will be called to create your feature.
     */
    protected AbstractFeature() {
        super();
        platforms = getPlatforms();
    }

    @Override
    public Platform[] getPlatforms() {
        return new Platform[]{Platform.WEB, Platform.TABLET, Platform.MOBILE};
    }

    /**
     * Identifies version by default. can be redefined to change version feature.
     */
    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public final boolean isSupportsPlatform(Platform platform) {
        for (Platform p : platforms) {
            if (platform.equals(p)) {
                return true;
            }
        }
        return false;
    }

}
