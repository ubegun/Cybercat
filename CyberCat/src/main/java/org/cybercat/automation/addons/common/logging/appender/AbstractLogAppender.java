package org.cybercat.automation.addons.common.logging.appender;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.cybercat.automation.persistence.model.TestCase;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;


@Plugin(name = "TestLogAppender",
        category = "Core",
        elementType = "appender",
        printObject = true)
public abstract class AbstractLogAppender extends AbstractAppender {

    protected ThreadLocal<StringBuffer> threadBuffer;

    private static List<AbstractLogAppender> logAppenders;

    protected Path path;
    protected String logType;

    protected AbstractLogAppender(String name, String logType, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout);
        getLogAppenders().add(this);
        threadBuffer = new ThreadLocal<>();
        this.logType = logType;
    }

    public static List<AbstractLogAppender> getLogAppenders() {
        if (logAppenders == null) {
            logAppenders = new LinkedList<>();
        }
        return logAppenders;
    }

    @PluginFactory
    public static AbstractLogAppender createLogger(@PluginAttribute("name") String name,
                                                   @PluginAttribute("logType") String logType,
                                                   @PluginElement("Filter") Filter filter,
                                                   @PluginElement("Layout") Layout<? extends Serializable> layout) {

        AbstractLogAppender logAppender = null;
        if (name == null) {
            name = "LogAppender";
        }

        if (logType == null) {
            logType = "log";
        }

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        if (logType.equals("html")) {
            logAppender = new HtmlLogAppender(name, logType, filter, layout);
        } else if (logType.equals("log")) {
            logAppender = new TextLogAppender(name, logType, filter, layout);
        }
        return logAppender;
    }



    @Override
    public void append(LogEvent event) {
        push(getLayout().toSerializable(event).toString());
    }

    public void push(String logLine) {
        if (threadBuffer != null && threadBuffer.get() != null)
            threadBuffer.get().append(logLine);
    }

    public void startRecording() {
        threadBuffer.set(new StringBuffer());
    }

    protected Path createPathToLog(String directory){
        Path path = Paths.get(directory + logType);
        path.getParent().toFile().mkdirs();
        return path;
    }

    public abstract void flush(TestCase test, String directory);

}
