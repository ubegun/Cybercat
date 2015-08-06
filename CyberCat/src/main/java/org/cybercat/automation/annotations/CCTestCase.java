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
 * @param id - this is a legacy ID for Quality Center system. (the system is not used) 
 * @param description - this is a description of the test case. Its value is consumed in test reports, screenshots, and videos in a form of subtitles in tests logs 
 * @param bugs - List of related IDs of bugs that can be used in bug tracking add-ons, see JiraReportManager
 * @param features - list of features that are activated for the Test Case 
 * <p><br>
 * Supported features:
 * <p><br>
 * Cybercat framework provides few standard features
 * <ul>
 * <li>Browser - it is a logical representation of browser windows, single tone for the Test Case thread. It provides 
 * methods of control for browser windows through the WebDriver
 * <li>JiraReportManager - this is a donation code by Adidas team of automation. 
 * Provides support with the Jira issue tracker by jira REST apis. Endpoint and authentication properties for Jira are contained in the test properties file. 
 * Service factory for SOAP connection is JirasoapserviceV2SoapBindingStub
 * <li>MediaController - this is a part of the Video add-on. Video add-on is used for creating video streams as well as their serialization to artifact storage 
 * <li>PerformanceReportManager - part of Yslow add-on.
 * <li>ScreenshotManager - part of screenshot add-on.
 * <li>TestLoggerAddon - part of thread logger add-on.
 * </ul>
 * <p><br>
 * Custom functions may be added by the user for this user to be able to make steps described below:
 *  <ul>
 *  <li>implement ExteranlAddon
 *  <li>define "external.addon.package" property in "test.properties"
 *  <li>define custom events if necessary (see extending org.cybercat.automation.events.Event)
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
