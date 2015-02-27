package org.cybercat.automation.addons.common.logging.appender;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.cybercat.automation.persistence.model.TestCase;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class HtmlLogAppender extends AbstractLogAppender {


    protected HtmlLogAppender(String name, String logType, Filter filter, Layout<? extends Serializable> layout) {
        super(name, logType, filter, layout);
    }


    @Override
    public void flush(TestCase test, String directory) {
        try {
            if (threadBuffer == null || threadBuffer.get() == null || threadBuffer.get().length() < 10)
                return;

            path = createPathToLog(directory);
            test.setFullLogHtml(path.toString());

            byte[] header = getLayout().getHeader() == null ? new byte[]{} : getLayout().getHeader();
            byte[] footer = getLayout().getFooter() == null ? new byte[]{} : getLayout().getFooter();

            BufferedWriter writer = Files.newBufferedWriter(path, Charset.defaultCharset());

            writer.write(new String(header, "UTF-8"), 0, header.length);
            writer.write(threadBuffer.get().toString());
            writer.write(new String(footer));

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
