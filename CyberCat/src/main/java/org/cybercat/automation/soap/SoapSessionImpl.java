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
package org.cybercat.automation.soap;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cybercat.automation.persistence.model.User;

class SoapSessionImpl implements SoapSession {

    static{
        CookieManager manager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
        cookieStore = manager.getCookieStore();
    }
    
    private final static CookieStore cookieStore;
    
    User currentUser; 
    private Map<URI, List<HttpCookie>> uriIndexSnapshot = null;
    
    public SoapSessionImpl(User currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public void makeCookieSnapshot(){
        uriIndexSnapshot = new HashMap<URI, List<HttpCookie>>();
        List<URI> uris = cookieStore.getURIs();
        List<HttpCookie> cookies;
        for(URI uri: uris){
            cookies = new ArrayList<HttpCookie>(cookieStore.get(uri));
            uriIndexSnapshot.put(uri, cookies);
        }
    }

    @Override
    public void putCookieSnapshot(){
        cookieStore.removeAll();
        for(Map.Entry<URI, List<HttpCookie>> entry: uriIndexSnapshot.entrySet()){
            for(HttpCookie cockie : entry.getValue()){
                cookieStore.add(entry.getKey(), cockie);
            }
        }
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }
    
}
