package org.cybercat.external.addon.slack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.TestContext;
import org.cybercat.automation.addons.ExteranlAddon;
import org.cybercat.automation.events.EventListener;
import org.cybercat.automation.events.EventTestFail;

public class SlackAddon implements ExteranlAddon {

  public final static String POST_MESSAGE = "Post message to specific channel for The Slack.com";
  public final static String POST_FAILURE_MESSAGE = "Post message to the slack channel in case of tests failure";
  private SlackClient slackService;
  private String testDescription;

  
  @Override
  public String[] getSupportedFeatures() {
    return new String[] { POST_MESSAGE, POST_FAILURE_MESSAGE };
  }

  @Override
  public Collection<EventListener<?>> createListeners(TestContext config) {
    List<EventListener<?>> eventListeners = new ArrayList<EventListener<?>>();      
    try {
      slackService = new SlackClient();
      
    } catch (AutomationFrameworkException e) {
      e.printStackTrace();
    }
    this.testDescription = config.getTestDescription();
    if(config.hasFeature(POST_MESSAGE))    
    eventListeners.add(new EventListener<EventPostMessage>(EventPostMessage.class, 10){
      
      @Override
      public void doActon(EventPostMessage event) throws Exception {
        String message = "Test [ " + testDescription + " ] says: \n " + event.getMessage();
        if(StringUtils.isBlank(event.getChennalName())){
          slackService.sendMessage(message);
        }else{
          slackService.sendMessage(event.getChennalName(), message);
        }
      }
      
    });
    if(config.hasFeature(POST_FAILURE_MESSAGE))    
    eventListeners.add(new EventListener<EventTestFail>(EventTestFail.class, 1000){

      @Override
      public void doActon(EventTestFail event) throws Exception {
        String message = event.getTestClass().getSimpleName() + " [ " + testDescription +" ] ->" + event.getMethodName() + " test [failed]"; 
        slackService.sendMessage(message);        
      }
      
    });
    
    return eventListeners;
  }

}
