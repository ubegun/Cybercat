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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.log4j.Logger;
import org.cybercat.automation.utils.WorkFolder;
import org.openqa.selenium.firefox.FirefoxProfile;


public class FirefoxProfiler extends FirefoxProfile {

    private static Logger log = Logger.getLogger(FirefoxProfiler.class);

    public FirefoxProfiler(Map<String, String> prorerties) {
        super();
        initProrerties(prorerties);
    }

    public FirefoxProfiler() {
        super();
        //File firebug = new File("src/main/resources/extensions/firebug-1.10.6-fx.xpi");
        //File netExport = new File("src/main/resources/extensions/netExport-0.9b2.xpi");
       /* try {
            //super.addExtension(firebug);
            //super.addExtension(netExport);
        } catch (IOException err) {
            log.error(err);
        }*/
        super.setPreference("app.update.enabled", false);
        super.setPreference("app.update.enabled", false);
        //String domain = "extensions.firebug.";

        // Set default Firebug preferences
        //super.setPreference(domain + "currentVersion", "1.10.6");
        //super.setPreference(domain + "allPagesActivation", "on");
        //super.setPreference(domain + "defaultPanelName", "net");
        //super.setPreference(domain + "net.enableSites", true);

        // Set default NetExport preferences
        /*super.setPreference(domain + "netexport.alwaysEnableAutoExport", true);
        super.setPreference(domain + "netexport.showPreview", false);
        super.setPreference(domain + "netexport.pageLoadedTimeout", "60000");
        super.setPreference(domain + "netexport.defaultLogDir", Paths.get(WorkFolder.Log.getPath().toString(), "har").toString());
        */
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

}
