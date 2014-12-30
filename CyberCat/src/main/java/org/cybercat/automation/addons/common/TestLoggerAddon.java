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
package org.cybercat.automation.addons.common;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import org.cybercat.automation.Configuration;
import org.cybercat.automation.core.AddonContainer;
import org.cybercat.automation.events.EventListener;
import org.cybercat.automation.events.EventStartTest;
import org.cybercat.automation.events.EventStopTest;
import org.cybercat.automation.persistence.TestArtifactManager;
import org.cybercat.automation.persistence.model.TestCase;
import org.cybercat.automation.utils.CommonUtils;
import org.cybercat.automation.utils.WorkFolder;


/**
 * This is not thread safe class
 */
public class TestLoggerAddon implements AddonContainer {

    public final static String FULL_LOG = "Full log";
    private TestLogAppender thisLogger;
    
    public TestLoggerAddon() {
        super();
        thisLogger =  TestLogAppender.getLogger();
    }

    @Override
    public Collection<EventListener<?>> createListeners(Configuration config) {
        ArrayList<EventListener<?>> listeners = new ArrayList<EventListener<?>>();
        if(!config.hasFeature(FULL_LOG)){
            return listeners;
        }
        listeners.add(new EventListener<EventStartTest>(EventStartTest.class, 100) {
            @Override
            public void doActon(EventStartTest event) throws Exception {
                thisLogger.startRecording();
            }
        });
        listeners.add(new EventListener<EventStopTest>(EventStopTest.class, 100) {
            @Override
            public void doActon(EventStopTest event) throws Exception {
                Path fullLog = Paths.get(WorkFolder.Screenshots.getPath().toString(), event.getDirName(),
                        CommonUtils.dateToString(event.getStopTime()) + event.getFileName() + "_full.log");
                thisLogger.flush(fullLog);
                TestCase test = new TestCase(event.getTestClass().getName());
                test.setFullLog(fullLog.toString());
                TestArtifactManager.updateTestInfo(test);
            }
        });
        return listeners;
    }

    @Override
    public String[] getSupportedFeatures() {
        return new String[]{FULL_LOG};
    }

}
