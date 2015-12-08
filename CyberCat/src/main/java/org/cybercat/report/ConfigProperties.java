package org.cybercat.report;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

public class ConfigProperties {

  
  protected Properties config = new Properties();

  public ConfigProperties() {
    this(System.getProperty("config.properties"));
  }

  public ConfigProperties(String propFName) {
    this(ClassLoader.getSystemResourceAsStream(propFName));
  }

  public ConfigProperties(InputStream io) {
    try {
      config.load(io);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public ConfigProperties(ConfigProperties otherConfig){
    this.config = otherConfig.config;
  }

  public String getProperty(String name) {
    return StringUtils.trim(config.getProperty(name));
  }
  
  private String getProperty(String name, String defaultValue ) {
    return StringUtils.trim(config.getProperty(name, defaultValue));
  }
  
  public ConfigProperties(Properties confProperties) {
    this.config = confProperties;
  }

  public boolean isRemoteServer() {
    return Boolean.parseBoolean(getProperty("remote.server", "true"));
  }

  public String getSeleniumHost() {
    return getProperty("selenium.host", "127.0.0.1");
  }

  public String getSeleniumPort() {
    return getProperty("selenium.port", "4444");
  }

  // Browser settings
  // acceptable parameters for browser.name [firefox, chrome, internetExplorer, safari, firefoxWithExtensions]
  public String getBrowserName() {
    return getProperty("browser.name", "firefox");
  }

  // this option works with no-remote web driver configuration only
  public String getBrowserProfileName() {
    return getProperty("browser.profile.name");
  }

  // Log info
  public String getEnvironmentName() {
    return getProperty("environment.name", "Cybercat sample test");
  }

  // Site language
  public String getContentLanguage() {
    return getProperty("language", "en");
  }

  // URL's
  public String getBaseUrl() {
    return getProperty("home.page.baseUrl");
  }

  // Jira Adddon
  public String getJiraSoapWsdlUrl() {
    return getProperty("jira.soap.wsdl.url");
  }

  public String getJiraSoapEndpoint() {
    return getProperty("jira.soap.endpoint");
  }

  public String getJiraLogin() {
    return getProperty("jira.login");
  }

  public String getJiraPassword() {
    return getProperty("jira.password");
  }

  // Common values
  // Time-outs
  public String getTimeout() {
    return getProperty("timeout", "15");
  }

  public String getImplicitTimeout() {
    return getProperty("implicit.timeout", "60");
  }

  public String getExplicitTimeout() {
    return getProperty("explicit.timeout", "60");
  }

  // This option enable page elements validation on loading time
  public boolean getElementsVisibilityValidation() {
    return Boolean.parseBoolean(getProperty("elements.visibility.validation", "false"));
  }

  // This value defines how many previous builds to save in history.
  // If the value is equals -1 the storage is never will truncated.
  public int getStorageHistorySize() {
    return Integer.parseInt(getProperty("artifacts.storage.history.size", "-1"));
  }

  public boolean isTruncateModel() {
    return Boolean.parseBoolean(getProperty("artifacts.storage.history.model.truncate", "true"));
  }

  // Version control
  public int getAppVersion() {
    return Integer.parseInt(getProperty("app.version", "1"));
  }

  public String getFeatureRootPackage() {
    return getProperty("version.control.root.package", "org.apache.automation.features");
  }

  // Common
  public String getSampleEndPoint() {
    return getProperty("sample.soap.endpoint", "http://soap.endpoint.org/sample.wsdl");
  }

  public String getExternalAddonPackage() {
    return getProperty("external.addon.package");
  }

}
