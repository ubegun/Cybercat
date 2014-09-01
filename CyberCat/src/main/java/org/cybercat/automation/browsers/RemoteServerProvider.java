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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.core.Browser;
import org.cybercat.automation.core.Browser.Browsers;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;


public class RemoteServerProvider {

    private static final String SELENIUM_HOST = "selenium.host";
    private static final String SELENIUM_PORT = "selenium.port";

    private Properties configProperties;

    public RemoteServerProvider(Properties configProperties) {
        super();
        this.configProperties = configProperties;
    }

    private DesiredCapabilities createDesiredCapabilities(Browsers browser) throws PageObjectException {
        try {
            DesiredCapabilities capabilities = null;
            switch (browser) {
            case firefox:
                capabilities = DesiredCapabilities.firefox();
                String profileName = configProperties.getProperty("browser.profile.name", null);
                if(StringUtils.isNotBlank(profileName)){
                    ProfilesIni allProfiles = new ProfilesIni();
                    FirefoxProfiler firefoxProfiler = new FirefoxProfiler();
                    FirefoxProfile profile = firefoxProfiler.addNetExportPreferences(allProfiles.getProfile(profileName));
                    capabilities.setCapability(FirefoxDriver.PROFILE, profile);
                }
                break;
            case chrome:
                capabilities = DesiredCapabilities.chrome();
                break;
            case internetExplorer:
                capabilities = DesiredCapabilities.internetExplorer();
                capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                break;
            case safari:
                capabilities = DesiredCapabilities.safari();
                break;
            default:
                new PageObjectException("Browser type unsupported.");
                break;
            }
            return capabilities;
        } catch (Exception e) {
            throw new PageObjectException(
                    "Remote browser initialization exception. Please check configuration parameters in property file.", e);
        }

    }

    public Browser createRemoteWebDriver(Browsers browserType) throws PageObjectException {
        DesiredCapabilities capabilities = createDesiredCapabilities(browserType);
        try {
            RemoteWebDriver driver = new RemoteWebDriver(new URL("http://" + configProperties.getProperty(SELENIUM_HOST) + ":"
                    + configProperties.getProperty(SELENIUM_PORT) + "/wd/hub"), capabilities);
            return new Browser(driver, browserType, true);
        } catch (MalformedURLException e) {
            throw new PageObjectException(e);
        }

    }

}
