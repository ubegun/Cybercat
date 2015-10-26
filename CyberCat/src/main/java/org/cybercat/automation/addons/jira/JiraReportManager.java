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
package org.cybercat.automation.addons.jira;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.Configuration;
import org.cybercat.automation.addons.jira.soap.Jira;
import org.cybercat.automation.core.AddonContainer;
import org.cybercat.automation.events.EventListener;
import org.cybercat.automation.events.EventStartTest;
import org.cybercat.automation.persistence.TestArtifactManager;
import org.cybercat.automation.persistence.model.TestCase;

public class JiraReportManager implements AddonContainer {

  public static final String JIRA_BUGS = "Jira bugs";

  private static final Logger LOG = Logger.getLogger(JiraReportManager.class);

  @Override
  public Collection<EventListener<?>> createListeners(Configuration config) {
    ArrayList<EventListener<?>> listeners = new ArrayList<EventListener<?>>();
    if(!config.hasFeature(JIRA_BUGS))
      return listeners;

    listeners.add(new EventListener<EventStartTest>(EventStartTest.class, 1000) {
      @Override
      public void doActon(EventStartTest event) throws Exception {
        TestCase testCase = new TestCase(event.getTestClass().getName());
        testCase.setBugs(buildJiraInfo(event));
        TestArtifactManager.updateTestRunInfo(testCase);
      }
    });
    return listeners;
  }

  private Set<TestCase.JiraInfo> buildJiraInfo(EventStartTest startTestEvent) {
    Set<TestCase.JiraInfo> bugs = new HashSet<>();
    if (startTestEvent.getBugIDs() == null) {
      return new HashSet<>();
    }
    for (String bugID : startTestEvent.getBugIDs()) {
      String bugSummary;
      try {
        bugSummary = Jira.getBugSummary(bugID);
      } catch (AutomationFrameworkException e) {
        bugSummary = "-";
        LOG.warn("Cannot get bug summary with ID: " + bugID, e);
      }
      bugs.add(new TestCase.JiraInfo(bugID, bugSummary));
    }
    return bugs;
  }

  @Override
  public String[] getSupportedFeatures() {
    return new String[] { JIRA_BUGS };
  }
}
