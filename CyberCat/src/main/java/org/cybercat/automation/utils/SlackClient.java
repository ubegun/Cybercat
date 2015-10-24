package org.cybercat.automation.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.cybercat.automation.AutomationFrameworkException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Slack client. 
 * 
 * @author ubegun
 *
 */
public class SlackClient {

  private static final Logger log = Logger.getLogger(SlackClient.class);

  private final static String SEND_MESSAGE = "chat.postMessage";
  private final static String CHANNELS_LIST = "channels.list";
  private final static String AUTHORIZE = "authorize";
  private final static String OAUTH = "oauth.access";

  // Property names
  private final static String P_TOKEN = "token";

  private String baseURL;
  private String oauth2URL = "https://slack.com/oauth/";
  private String apiUrl = "https://slack.com/api/";
  private String token;

  private Properties prop = new Properties();

  public SlackClient() throws AutomationFrameworkException {
    this(null, "slack.properties");
    token = getProperty(P_TOKEN);
    if (token == null)
      throw new AutomationFrameworkException("Slak tokent is null.");
  }

  public Map<String, String> getChannels() throws AutomationFrameworkException {
    HashMap<String, String> channels = new HashMap<String, String>();
    MultivaluedMap<String, String> params = new MultivaluedMapImpl();
    params.add("token", token);
    ClientResponse resp = getResponse(params, CHANNELS_LIST);
    System.out.println(resp.toString());
    try {
      JSONObject jObj = new JSONObject(resp.getEntity(String.class));
      JSONArray cArr = jObj.getJSONArray("channels");
      for (int i = 0; i < cArr.length(); i++) {
        JSONObject channel = cArr.getJSONObject(i);
        channels.put(channel.getString("name"), channel.getString("id"));
      }
    } catch (Exception e) {
      throw new AutomationFrameworkException(e);
    }
    return channels;
  }

  public SlackClient(String token, String propFileName) throws AutomationFrameworkException {
    try {
      prop.load(ClassLoader.getSystemResourceAsStream(propFileName));
      baseURL = apiUrl;
      this.token = token;
    } catch (IOException e) {
      throw new AutomationFrameworkException(e);
    }
  }

  public void sendMessage(String channelId, String message) throws AutomationFrameworkException {
    MultivaluedMap<String, String> params = new MultivaluedMapImpl();
    params.add("token", token);
    params.add("channel", channelId);
    params.add("text", message);
    params.add("username", getProperty("username"));
    ClientResponse resp = getResponse(params, SEND_MESSAGE);
    log.info(resp.toString());
    log.info("------------------------------------------------------------");
    log.info(resp.getEntity(String.class));
  }

  private ClientResponse getResponse(MultivaluedMap<String, String> params, String... paths)  throws AutomationFrameworkException {
    ClientConfig conf = new DefaultClientConfig();

    Client client = Client.create(conf);
    WebResource webResource = client.resource(baseURL);
    for (String path : paths) {
      webResource = webResource.path(path);
    }
    Builder builder = webResource.queryParams(params).accept(MediaType.APPLICATION_JSON_TYPE).type(MediaType.APPLICATION_JSON_TYPE);
    ClientResponse response = builder.get(ClientResponse.class);
    if (response.getStatus() != 200) {
      throw new AutomationFrameworkException("Failed : HTTP error code : " + response.getStatus());
    }
    log.info("+++ Slak server response 200 +++");
    log.info(response.toString());
    return response;
  }

  private String getProperty(String name) {
    return StringUtils.trim(prop.getProperty(name));
  }

}
