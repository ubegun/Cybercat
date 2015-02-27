package org.cybercat.automation.addons.common.logging.provider;

import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.message.Message;

public class HtmlLogBuilder {

    private StringBuilder messageBuilder;
    private LogLevelAnalyzer logLevelAnalyzer;

    public enum PageStartStatus {
        PAGE_OPEN, ASSERT_START;
    }


    public HtmlLogBuilder(StringBuilder messageBuilder, LogLevelAnalyzer logLevelAnalyzer) {
        this.messageBuilder = messageBuilder;
        this.logLevelAnalyzer = logLevelAnalyzer;
    }

    public void resetMessageBuilder() {
        if (messageBuilder.length() > 0) {
            messageBuilder.delete(0, messageBuilder.length());
        }
    }

    public String getHtmlMessage() {
        return messageBuilder.toString();
    }

    public void buildTestNode(String testCaseName, String testClassName) {
        messageBuilder.append("<div style=\"margin-left:25px;\" onclick=\"tree_toggle(arguments[0])\">")
                .append("<div class='test-header'><b>Test run log for test: </b>")
                .append(testCaseName)
                .append("<br><br><b>Test class name:&nbsp;&nbsp;</b>")
                .append(testClassName)
                .append("</div>")
                .append("<p><span class='conf'> + Test case precondition: </span><div id='toReplace' class='Closed'>");
    }

    public void buildConfigurationNode(String message) {
        messageBuilder.append("<div style=\"margin-left:18px\"><span>")
                .append(message)
                .append("</span></div><p></p>");
    }

    public void buildTestMethodNode(String message) {
        messageBuilder.append("<br><li class=\"Node IsRoot ExpandClosed\">")
                .append("<div class=\"Expand\"></div>")
                .append("<div class=\"Content\"><div class='test-step'>")
                .append(message)
                .append("</div></div>");
    }

    public void buildStepStartNode(String message) {
        messageBuilder.append("<p><li class=\"Node ExpandClosed\">")
                .append("<div class=\"Expand\"></div>")
                .append("<div class=\"Content\">")
                .append(message)
                .append("</div>");
    }

    public void buildPageActionNode(Message message) {
        switch (String.valueOf(message.getParameters()[0])) {
            case "PAGE_OPEN":
                buildPageOpenNode(message.getFormattedMessage());
                break;
            case "ASSERT_START":
                buildAssertStartNode(message.getFormattedMessage(), logLevelAnalyzer.getFloatStyle((Integer) message.getParameters()[1]));
                logLevelAnalyzer.setPreviousAssertPriority((Integer) message.getParameters()[1]);
                break;
        }
    }


    private void buildPageOpenNode(String message) {
        messageBuilder.append("<p><li class=\"Node ExpandClosed\">")
                .append("<div class=\"Expand\"></div>")
                .append("<div class=\"Content\">")
                .append(message)
                .append("</div>");
    }

    private void buildAssertStartNode(String message, String floatStyle) {
        String nodeWithFloat = String.format("<div style='float:%s'>", floatStyle);
        messageBuilder.append("<p><li style='margin-left:36px;'>")
                .append("<div></div>")
                .append(nodeWithFloat)
                .append(message)
                .append("</div>");
    }


    public void buildElementActionNode(String message) {
        messageBuilder.append("<p><li class=\"Node ExpandLeaf IsLast\">")
                .append("<div class=\"Expand\"></div>")
                .append("<div class=\"Content\">")
                .append(message)
                .append("</div>");
    }

    public void buildAdditionalInfoNode(String message) {
        messageBuilder.append("<p><div class=\"Container\"><span style=\"margin-left:36px\"><font color='#e18728'><b>ADDITIONAL TEST CASE INFORMATION:</font></b> ")
                .append(message)
                .append("</span></div>");
    }

    public void addTestStepsHeader() {
        if(logLevelAnalyzer.isNeedAddTestStepsHeader()) {
            messageBuilder.append("<br><p style='font-size:18px; margin-left:18px;'><b>Test steps: </b></p>");
        }
    }

    public void addContainerNode() {
        if(logLevelAnalyzer.isNeedAddContainer()) {
            messageBuilder.append("<ul class=\"Container\">");
        }
    }


    public void addConfigurationFailedNode(Message message) {
        if(logLevelAnalyzer.isConfigurationStepFailed()) {
            buildStackTraceMessage(message.getFormattedMessage(), message.getParameters());
            closeParentConfigurationNode();
            addSummaryTestFailMessage();
            logLevelAnalyzer.setPreviousLevel(LogLevel.TEST_FINISH);
        }
    }

    public void addTestCaseFailedNode(Message message) {
        if(logLevelAnalyzer.isTestStepFailed()) {
            buildStackTraceMessage(message.getFormattedMessage(), message.getParameters());
            closePreviousNode();
            closeCurrentContainer();
            closeParentContainer(logLevelAnalyzer.getPreviousLevel().intLevel() - LogLevel.TEST_METHOD_START.intLevel());
            addTestMethodFailMessage(HtmlLogHelper.getTestMethodName(message.getFormattedMessage()));
            logLevelAnalyzer.setPreviousLevel(LogLevel.TEST_FAIL);
        }
    }


    public void addFinishedTestNode() {
        if(logLevelAnalyzer.isTestFinished()) {
            messageBuilder.append("</div><br>");
            messageBuilder.append("Log session finish time ").append(new java.util.Date()).append("<br>").append(Constants.LINE_SEPARATOR);
            messageBuilder.append("<hr size=\"1\" noshade>").append(Constants.LINE_SEPARATOR);
        }
    }

    private void buildStackTraceMessage(String className, Object[] exception) {
        messageBuilder.append("<br><div><span class='test-case-fail'> FAIL in ")
                .append(className)
                .append("</span>")
                .append("<p style='color: #C00000;'><b>Exception:</b> ");
        for (Object exceptionItem : exception) {
            messageBuilder.append("<br>")
                    .append(exceptionItem);
        }
        messageBuilder.append("</div>");
    }

    public void closeConfigurationNode() {
        if(logLevelAnalyzer.isNeedCloseConfigurationNode()) {
            messageBuilder.append("</div>");
        }
    }

    public void closePreviousNode() {
        if(logLevelAnalyzer.isNeedClosePreviousNode()) {
            messageBuilder.append("</li>");
        }
    }

    public void closePreviousContainer(){
        if(logLevelAnalyzer.isNeedClosePreviousContainer()) {
            closeCurrentContainer();
            int timesToClose = logLevelAnalyzer.getPreviousLevel().intLevel() - logLevelAnalyzer.getCurrentLogLevel().intLevel();
            closeParentContainer(timesToClose);
        }
    }

    private void closeCurrentContainer() {
        messageBuilder.append("</ul>");
    }

    private void closeParentContainer(int parentNodes) {
        for (int i = 0; i < parentNodes; i++) {
            messageBuilder.append("</li></ul>");
        }
    }

    private void closeParentConfigurationNode() {
        messageBuilder.append("</div>");
    }

    private void addTestMethodFailMessage(String testMethod) {
            messageBuilder.append("<hr size=\"1\" noshade>")
                    .append("<span style=\"color:#C00000; font-weight:bold; font-family: Arial\"><b>Test method \"")
                    .append(testMethod)
                    .append("\" finished with fail!</b></span>");
    }

    private void addSummaryTestFailMessage(){
        messageBuilder.append("<br><hr size=\"1\" noshade>")
                .append("<span class='test-fail-summary'><b>Test finished with fail!</b>");
    }

}
