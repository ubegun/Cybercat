package org.cybercat.slack;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Map;

import org.cybercat.automation.AutomationFrameworkException;
import org.junit.Test;

public class SlackServiceTest {

  
  
  //@Test
  public void testSendMessage() throws AutomationFrameworkException {
    SlackService ss = new SlackService();
    String token = ss.authorize();
    String message = "Test " + (new Date()).toString();
    Map<String, String> chennels = ss.getChannels();
    ss.sendMessage(chennels.get("general"), message);
    assertTrue(true);
  }

}
