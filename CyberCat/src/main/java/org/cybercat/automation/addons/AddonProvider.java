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

package org.cybercat.automation.addons;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.core.AutomationMain;
import org.reflections.Reflections;

/**
 * @author Ubegun
 * 
 */
public class AddonProvider {

    private Set<ExteranlAddon> addons;
    
    private final static Logger logger = Logger.getLogger(AddonProvider.class); 

    /**
     * @throws AutomationFrameworkException
     * 
     */
    public AddonProvider() throws AutomationFrameworkException {
        addons = new HashSet<ExteranlAddon>();
        String rootPackage = AutomationMain.getConfigProperties().getExternalAddonPackage();
        if (StringUtils.isBlank(rootPackage))
            return;
        Reflections reflections = new Reflections(rootPackage);
        Set<Class<? extends ExteranlAddon>> addonClasses = reflections.getSubTypesOf(ExteranlAddon.class);
        logger.info("########### External addons are loaning ##########");
        for (Class<? extends ExteranlAddon> addonClass : addonClasses) {
            try{
                ExteranlAddon addon = addonClass.getConstructor().newInstance();
                addons.add(addon);
                logger.info("##" + addonClass.getSimpleName() + " addon has been loaded.");
                logger.info("# Supported features: ");
                for(String name : addon.getSupportedFeatures()){
                  logger.info("# - " + name);
                }
                logger.info("#");
            }catch(Exception e){
                logger.error("External addon initialization exception.", e);
            }
            logger.info("########### Addons loaded ##########");
        }
    }

    public Set<ExteranlAddon> getAddons() {
        return addons;
    }
    
}
