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

package org.cybercat.common;

import java.util.ArrayList;
import java.util.Date;

import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.PersistenceManager;
import org.cybercat.automation.addons.common.TestLoggerAddon;
import org.cybercat.automation.annotations.CCTestCase;
import org.cybercat.automation.core.AutomationMain;
import org.cybercat.automation.persistence.Criterion;
import org.cybercat.automation.testng.TestNGTestCase;
import org.cybercat.common.Model1.TestEnum;
import org.cybercat.external.addon.EventPostMessage;
import org.cybercat.external.addon.SlackAddon;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Ubegun
 * 
 */
//Step 1 -> setup feature
@CCTestCase(description = "model test", features = {SlackAddon.POST_MESSAGE, SlackAddon.POST_FAILURE_MESSAGE, TestLoggerAddon.FULL_LOG })
public class CommonTest extends TestNGTestCase {

  /*
   * (non-Javadoc)
   * 
   * @see org.cybercat.automation.test.AbstractTestCase#setup()
   */
  @Override
  public void setup() throws AutomationFrameworkException {
    // TODO Auto-generated method stub

  }

  @Test
  public void slackTest() throws AutomationFrameworkException {
    //Step 2 -> send message
    AutomationMain.getEventManager().notify(new EventPostMessage("Test " + (new Date()).toString()));
  }
  
  @Test
  public void stackTest_FailMessage() throws AutomationFrameworkException {
    throw new  AutomationFrameworkException("Test");
  }
  
  @Test
  public void saveLoadmodel_Ok() throws AutomationFrameworkException {
    PersistenceManager pm = AutomationMain.getMainFactory().getPersistenceManager();
    final long m1Id = 1;

    Model1 m1 = new Model1();
    m1.setEmumField(TestEnum.e1);
    m1.setId(m1Id);

    Model2 m2 = new Model2();
    ArrayList<Model2> m2s = new ArrayList<Model2>();
    m2s.add(m2);
    m1.setModel2(m2s);

    pm.save(m1);
    Model1 m1Loaded = pm.findFirst(Model1.class, new Criterion<Model1>() {

      @Override
      public boolean processRestriction(Model1 entity) {
        return entity.getId() == m1Id;
      }
    });

    Assert.assertEquals(m1Loaded.getsField(), m1.getsField());
    Assert.assertEquals(m1Loaded.getEmumField(), m1.getEmumField());
  }
}
