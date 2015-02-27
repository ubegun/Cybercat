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
package org.cybercat.automation.rss;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.PasswordAuthentication;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cybercat.automation.persistence.model.Identity;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class DefaultRssReader implements RssReader {

    private static final Logger LOG = LogManager.getLogger(DefaultRssReader.class);

    public DefaultRssReader(final Identity user) {
        Authenticator.setDefault(new Authenticator() {
            
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user.getEmail(), user.getPassword().toCharArray());
            }
        });
    }
    
    @Override
    public boolean readRss(String strUrl) {
        SyndFeed feed = null;
        try {
            URL url = new URL(strUrl);
            URLConnection connection = url.openConnection();
            connection.addRequestProperty("Cookie", getCookiesString());
            InputStream in = connection.getInputStream();
            SyndFeedInput feedInput = new SyndFeedInput();
            feed = feedInput.build(new XmlReader(in));
        } catch (URISyntaxException | IllegalArgumentException | FeedException | IOException e) {
            LOG.error("Error while reading rss from: " + strUrl, e);
            return false;
        }
        return feed != null;
    }
    
    private String getCookiesString() throws URISyntaxException {
        CookieManager manager = (CookieManager) CookieHandler.getDefault();
        List<HttpCookie> cookies = manager.getCookieStore().getCookies();
        String cookiesString = "";
        for (int i = 0; i < cookies.size(); ++i) {
            HttpCookie cookie = cookies.get(i);
            cookiesString += cookie.getName() + "=" + cookie.getValue() + "; ";
        }
        return cookiesString;
    }
}
