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
package org.cybercat.automation.browsers;


import org.cybercat.automation.utils.CommonUtils;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * This is profiler for the chrom browser configuration
 * 
 *  scope - prototype 
 */
public class ChromeProfile {

    private DesiredCapabilities capabilities;

    public ChromeProfile() {
        capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability("chrome.switches", Arrays.asList("--start-maximized"));
        Path driverPath = Paths.get("C:" ,"TEMP", 
                                    "chrome_drivers", 
                                    getDriverPath(), 
                                    CommonUtils.isWindows() ? "chromedriver.exe" : "chromedriver");
        System.setProperty("webdriver.chrome.driver", driverPath.toFile().getAbsolutePath());
    }

    public DesiredCapabilities getCapabilities() {
        return capabilities;
    }

    private static String getDriverPath() {
        String result = "linux32";
        if (CommonUtils.isWindows()) {
            result = "win";
        } else if (CommonUtils.isMac()) {
            result = "mac";
        } else if (CommonUtils.isUnix() && CommonUtils.isX64()) {
            result = "linux64";
        }
        return result;
    }
}
