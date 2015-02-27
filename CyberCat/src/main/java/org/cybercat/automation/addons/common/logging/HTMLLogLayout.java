package org.cybercat.automation.addons.common.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.message.Message;
import org.cybercat.automation.addons.common.logging.provider.HtmlLogBuilder;
import org.cybercat.automation.addons.common.logging.provider.LogLevel;
import org.cybercat.automation.addons.common.logging.provider.LogLevelAnalyzer;
import org.cybercat.automation.utils.CommonUtils;

import java.nio.charset.Charset;


@Plugin(name = "HTMLLogLayout",
        category = "Core",
        elementType = "layout",
        printObject = true)
public class HTMLLogLayout extends AbstractStringLayout {

    private static final String DEFAULT_TITLE = "Test Run HTML log";
    private static final String DEFAULT_CONTENT_TYPE = "text/html";

    private final String title;
    private final String contentType;

    private static HTMLLogLayout thisLogger;

    private HtmlLogBuilder messageBuilder;
    private LogLevelAnalyzer logLevelAnalyzer;

    protected HTMLLogLayout(Charset charset, String title, String contentType, String fontSize) {
        super(charset);
        this.title = title;
        this.contentType = contentType;

        thisLogger = this;

        logLevelAnalyzer = new LogLevelAnalyzer();
        messageBuilder = new HtmlLogBuilder(new StringBuilder(), logLevelAnalyzer);

    }

    public static HTMLLogLayout getLogger() {
        return thisLogger;
    }

    public String getHtmlMessage() {
        return messageBuilder.getHtmlMessage();
    }

    @PluginFactory
    public static HTMLLogLayout createLayout(
            @PluginAttribute(value = "title", defaultString = DEFAULT_TITLE) final String title,
            @PluginAttribute(value = "contentType", defaultString = DEFAULT_CONTENT_TYPE) String contentType,
            @PluginAttribute(value = "charset", defaultString = "UTF-8") final Charset charset,
            @PluginAttribute("fontSize") String fontSize
    ) {
        return new HTMLLogLayout(charset, title, contentType, fontSize);
    }

    @Override
    public String toSerializable(LogEvent event) {

        Level eventLogLevel = event.getLevel();
        logLevelAnalyzer.setCurrentLogLevel(eventLogLevel);

        if (!logLevelAnalyzer.isHtmlLogLevel()) {
            return "";
        }

        
        messageBuilder.resetMessageBuilder();

        messageBuilder.addConfigurationFailedNode(event.getMessage());
        messageBuilder.addTestCaseFailedNode(event.getMessage());
        messageBuilder.addFinishedTestNode();

        if (!logLevelAnalyzer.isPreviousStepFinished()
                && !logLevelAnalyzer.getCurrentLogLevel().equals(LogLevel.TEST_FINISH)
                && !logLevelAnalyzer.getCurrentLogLevel().equals(LogLevel.TEST_FAIL)) {

            messageBuilder.closeConfigurationNode();
            messageBuilder.closePreviousNode();
            messageBuilder.closePreviousContainer();

            messageBuilder.addTestStepsHeader();
            messageBuilder.addContainerNode();

            buildTeslLogBody(eventLogLevel.name(), event.getMessage());

        }

        return messageBuilder.getHtmlMessage();
    }


    @Override
    public byte[] getHeader() {
        final StringBuilder sbuf = new StringBuilder();

        sbuf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" ");
        sbuf.append("\"http://www.w3.org/TR/html4/loose.dtd\">");
        sbuf.append(Constants.LINE_SEPARATOR);
        sbuf.append("<html>").append(Constants.LINE_SEPARATOR);
        sbuf.append("<head>").append(Constants.LINE_SEPARATOR);
        sbuf.append("<meta charset=\"").append(getCharset()).append("\"/>").append(Constants.LINE_SEPARATOR);
        sbuf.append("<title>").append(title).append("</title>").append(Constants.LINE_SEPARATOR);

        sbuf.append("<style type=\"text/css\">").append(Constants.LINE_SEPARATOR);
        sbuf.append(CommonUtils.readFromResourceFile("css", "htmlLog.css"));
        sbuf.append("</style>").append(Constants.LINE_SEPARATOR);

        sbuf.append("<script>").append(Constants.LINE_SEPARATOR);
        sbuf.append(CommonUtils.readFromResourceFile("js", "htmlLog.js"));
        sbuf.append("</script>").append(Constants.LINE_SEPARATOR);

        sbuf.append("</head>").append(Constants.LINE_SEPARATOR);
        sbuf.append("<body style=\"font-family: Arial\" bgcolor=\"#F8F8F8\" topmargin=\"6\" leftmargin=\"6\">").append(Constants.LINE_SEPARATOR);
        sbuf.append("<hr size=\"1\" noshade>").append(Constants.LINE_SEPARATOR);
        sbuf.append("Log session start time ").append(new java.util.Date()).append("<br>").append(Constants.LINE_SEPARATOR);
        sbuf.append("<br>").append(Constants.LINE_SEPARATOR);

        return sbuf.toString().getBytes(getCharset());
    }

    @Override
    public byte[] getFooter() {
        final StringBuilder sbuf = new StringBuilder();
        sbuf.append("<br>").append(Constants.LINE_SEPARATOR);
        sbuf.append("</body></html>");
        return sbuf.toString().getBytes(getCharset());
    }

    private void buildTeslLogBody(String currentLogLevel, Message message) {
        switch (currentLogLevel) {
            case "TEST_START":
                messageBuilder.buildTestNode(message.getFormattedMessage(), message.getParameters()[0].toString());
                logLevelAnalyzer.setPreviousLevel(LogLevel.TEST_START);
                break;
            case "CONFIGURATION_START":
                messageBuilder.buildConfigurationNode(message.getFormattedMessage());
                logLevelAnalyzer.setPreviousLevel(LogLevel.CONFIGURATION_START);
                break;
            case "TEST_METHOD_START":
                messageBuilder.buildTestMethodNode(message.getFormattedMessage());
                logLevelAnalyzer.setPreviousLevel(LogLevel.TEST_METHOD_START);
                break;
            case "STEP_START":
                messageBuilder.buildStepStartNode(message.getFormattedMessage());
                logLevelAnalyzer.setPreviousLevel(LogLevel.STEP_START);
                break;
            case "PAGE_START":
                messageBuilder.buildPageActionNode(message);
                logLevelAnalyzer.setPreviousLevel(LogLevel.PAGE_START);
                break;
            case "ELEMENT_ACTION":
                messageBuilder.buildElementActionNode(message.getFormattedMessage());
                logLevelAnalyzer.setPreviousLevel(LogLevel.ELEMENT_ACTION);
                break;
            case "ADDITIONAL_DETAILS":
                messageBuilder.buildAdditionalInfoNode(message.getFormattedMessage());
                break;
        }
    }


}
