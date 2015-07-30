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
package org.cybercat.automation.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import org.cybercat.automation.addons.common.ScreenshotManager;
import org.cybercat.automation.addons.common.TestLoggerAddon;

/**
 * 
 * @param id - this is legacy Id for Quality Center system. (the system is not used) 
 * @param description - this is description of the test case. Consuming in test reports, on screenshots and video, like a subtitles, in tests logs 
 * @param bugs - List of related IDs of bugs, can be used in bug tracking addons see JiraReportManager
 * @param features - list of features what activated for the Test Case 
 * <p><br>
 * Supported features:
 * <p><br>
 * Cybercat framework provides few standard features
 * <ul>
 * <li>Browser - this is logical representation of browser windows, single tone for the Test Case thread. The browser provides methods for control browser windows through the WebDriver
 * <li>JiraReportManager - this is donation code by Adidas team of automation. Provides support with the Jira issue tracker by jira REST apis.Endpoint and authentication properties for Jira is in test properties file. Service factory for SOAP connection is JirasoapserviceV2SoapBindingStub
 * <li>MediaController - this is part of Video addon. Video addon response by creating video streams and serialization to artifact storage 
 * <li>PerformanceReportManager - part of Yslow addon.
 * <li>ScreenshotManager - part of screenshot addon.
 * <li>TestLoggerAddon - part of thread logger addon.
 * </ul>
 * <p><br>
 * Custom functions may be added by the user for this user can make steps described below:
 *  <ul>
 *  <li>implements ExteranlAddon
 *  <li>define "external.addon.package" property in "test.properties"
 *  <li>define custom events if it necessary ( see extending org.cybercat.automation.events.Event)
 *  </ul> 
 *   
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( {ElementType.METHOD, ElementType.TYPE} )
public @interface CCTestCase {
    int[] id() default {};
    String description();
    String[] bugs() default {};
    String[] features() default {ScreenshotManager.EXCEPTION_SCREENSHOT, TestLoggerAddon.FULL_LOG}; 
}
