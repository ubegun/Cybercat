package org.cybercat.automation.addons.common.logging.appender;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.cybercat.automation.persistence.model.TestCase;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;


public class TextLogAppender extends AbstractLogAppender {


    protected TextLogAppender(String name, String logType, Filter filter, Layout<? extends Serializable> layout) {
        super(name, logType, filter, layout);
    }


    @Override
    public void flush(TestCase test, String directory) {
        try {
            if (threadBuffer == null || threadBuffer.get() == null || threadBuffer.get().length() < 10)
                return;

            path = createPathToLog(directory);
            test.setFullLog(path.toString());

            BufferedWriter writer = Files.newBufferedWriter(path, Charset.defaultCharset());
            writer.write(threadBuffer.get().toString());

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
