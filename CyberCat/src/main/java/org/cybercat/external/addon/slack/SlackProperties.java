package org.cybercat.external.addon.slack;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

public class SlackProperties {

  private final static String P_TOKEN = "token";
  private final static String P_CHANNEL = "channelName";
  private final static String P_USERNAME = "username";

  private Properties prop = new Properties();

  private String token;
  private String channel;
  private String username;

  public SlackProperties(String propFileName) throws IOException {
    super();
    prop.load(ClassLoader.getSystemResourceAsStream(propFileName));
    init();
  }

  private void init() {
    token = getProperty(P_TOKEN);
    channel = getProperty(P_CHANNEL);
    username = getProperty(P_USERNAME);
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  private String getProperty(String name) {
    return StringUtils.trim(prop.getProperty(name));
  }
}
