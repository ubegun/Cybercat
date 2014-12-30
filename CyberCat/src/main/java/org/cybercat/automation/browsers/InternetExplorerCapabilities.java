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
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.nio.file.Path;
import java.nio.file.Paths;

public class InternetExplorerCapabilities {

    private DesiredCapabilities ieProfile;

    public InternetExplorerCapabilities() {
        ieProfile = DesiredCapabilities.internetExplorer();
        ieProfile.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        System.setProperty("webdriver.ie.driver", getDriverPath());
    }

    public DesiredCapabilities getIeProfile() {
        return ieProfile;
    }

    public void setIeProfile(DesiredCapabilities ieProfile) {
        this.ieProfile = ieProfile;
    }

    private String getDriverPath(){
      Path driver = Paths.get(".","ie_driver") ;
      if(!driver.toFile().isDirectory()){
        driver = Paths.get("C:" ,"TEMP", "ie_driver");
      }
      driver = Paths.get(driver.toString(), CommonUtils.isX64() ? "x64" : "x32", "IEDriverServer.exe");
      return driver.toString();
    }
    
}
