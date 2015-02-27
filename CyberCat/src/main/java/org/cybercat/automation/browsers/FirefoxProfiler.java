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

import java.nio.file.Paths;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cybercat.automation.utils.WorkFolder;
import org.openqa.selenium.firefox.FirefoxProfile;


public class FirefoxProfiler extends FirefoxProfile {

    public FirefoxProfiler(Map<String, String> prorerties) {
        super();
        initProrerties(prorerties);
    }

    public FirefoxProfiler() {
        super();
    }

    private void initProrerties(Map<String, String> prorerties) {
        for (Map.Entry<String, String> entry : prorerties.entrySet()) {
            if (setInt(entry.getKey(), entry.getValue()))
                continue;
            if (setBoolean(entry.getKey(), entry.getValue()))
                continue;
            super.setPreference(entry.getKey(), entry.getValue());
        }
    }

    private boolean setInt(String key, String value) {
        try {
            int intV = Integer.parseInt(value);
            super.setPreference(key, intV);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private boolean setBoolean(String key, String value) {
        try {
            boolean boolV = Boolean.parseBoolean(value);
            super.setPreference(key, boolV);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public FirefoxProfile addNetExportPreferences(FirefoxProfile firefoxProfile){
        firefoxProfile.setPreference("app.update.enabled", false);
        String domain = "extensions.firebug.";

        // Set default Firebug preferences
        firefoxProfile.setPreference(domain + "currentVersion", "1.10.6");
        firefoxProfile.setPreference(domain + "allPagesActivation", "on");
        firefoxProfile.setPreference(domain + "defaultPanelName", "net");
        firefoxProfile.setPreference(domain + "net.enableSites", true);

        // Set default NetExport preferences
        firefoxProfile.setPreference(domain + "netexport.alwaysEnableAutoExport", true);
        firefoxProfile.setPreference(domain + "netexport.showPreview", false);
        firefoxProfile.setPreference(domain + "netexport.pageLoadedTimeout", "60000");
        firefoxProfile.setPreference(domain + "netexport.defaultLogDir", Paths.get(WorkFolder.Log.getPath().toString(), "har").toString());
        return firefoxProfile;
    }

}
