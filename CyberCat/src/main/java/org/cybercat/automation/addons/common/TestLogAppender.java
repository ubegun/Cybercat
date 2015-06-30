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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * This is not thread safe class
 */
public class TestLogAppender extends AppenderSkeleton {

    private ThreadLocal<StringBuffer> threadBuffer;
    private static TestLogAppender thisLogger;

    public TestLogAppender() {
        super();
        thisLogger = this;
        threadBuffer = new ThreadLocal<StringBuffer>();
    }

    /**
     * Returns single tone logger
     */
    public static TestLogAppender getLogger() {
        return thisLogger;
    }

    @Override
    protected void append(LoggingEvent event) {
        push(getLayout().format(event));
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    public void push(String logLine) {
        if (threadBuffer != null && threadBuffer.get() != null)
            threadBuffer.get().append(logLine);
    }

    public void startRecording() {
        threadBuffer.set(new StringBuffer());
    }

    public void flush(Path path) {
        try {
            if (threadBuffer == null || threadBuffer.get() == null || threadBuffer.get().length() < 10)
                return;
            path.getParent().toFile().mkdirs();
            BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"));
            writer.write(threadBuffer.get().toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
