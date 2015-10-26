package org.cybercat.external.addon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.Configuration;
import org.cybercat.automation.addons.ExteranlAddon;
import org.cybercat.automation.events.EventListener;

public class SlackAddon implements ExteranlAddon {

  public final static String POST_MESSAGE = "Post message to specific channel for The Slack.com";
  private SlackClient slackService;

  
  @Override
  public String[] getSupportedFeatures() {
    return new String[] { POST_MESSAGE };
  }

  @Override
  public Collection<EventListener<?>> createListeners(Configuration config) {
    List<EventListener<?>> eventListeners = new ArrayList<EventListener<?>>();
    if(config.hasFeature(POST_MESSAGE)){
      
      initSlackService(eventListeners);
    }
    return eventListeners;
  }

  private void initSlackService(List<EventListener<?>> eventListeners) {
    try {
      slackService = new SlackClient();
      
    } catch (AutomationFrameworkException e) {
      e.printStackTrace();
    }
    eventListeners.add(new EventListener<EventPostMessage>(EventPostMessage.class, 10){
      
      @Override
      public void doActon(EventPostMessage event) throws Exception {
        if(StringUtils.isBlank(event.getChennalName())){
          slackService.sendMessage(event.getMessage());
        }else{
          slackService.sendMessage(event.getChennalName(), event.getMessage());
        }
      }
      
    });
  }

}
