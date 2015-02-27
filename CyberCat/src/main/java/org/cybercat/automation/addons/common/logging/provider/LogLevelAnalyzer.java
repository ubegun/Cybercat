package org.cybercat.automation.addons.common.logging.provider;


import org.apache.logging.log4j.Level;


public class LogLevelAnalyzer {

    private static final int LOWER_LEVEL_LIMIT = 550;
    private static final int UPPER_LEVEL_LIMIT = 600;

    private Level previousLevel;
    private Level currentLogLevel;

    private int previousAssertPriority = 100;

    public boolean isHtmlLogLevel() {
        return currentLogLevel.intLevel() > LOWER_LEVEL_LIMIT && currentLogLevel.intLevel() < UPPER_LEVEL_LIMIT ;
    }

    public boolean isNeedCloseConfigurationNode() {
        return isPreviousLevelExists() ?
                checkPreviousLevel(LogLevel.CONFIGURATION_START)
                && !checkCurrentLevel(LogLevel.CONFIGURATION_START)
                && currentLogLevel.isLessSpecificThan(previousLevel)
                : false;
    }

    public boolean isNeedClosePreviousNode() {
        return isPreviousLevelExists() ? isTheSameLevels() : false;
    }

    public boolean isNeedClosePreviousContainer() {
        return isPreviousLevelExists() ?
                !checkPreviousLevel(LogLevel.TEST_FAIL)
                && currentLogLevel.isMoreSpecificThan(previousLevel)
                        && !isTheSameLevels()
                : false;
    }

    public boolean isNeedAddContainer() {
        return isPreviousLevelExists() ?
                !isTheSameLevels()
                        && !checkCurrentLevel(LogLevel.CONFIGURATION_START)
                        && !checkCurrentLevel(LogLevel.ADDITIONAL_DETAILS)
                : false;
    }

    public boolean isTestFinished() {
        return checkCurrentLevel(LogLevel.TEST_FINISH);
    }

    public boolean isConfigurationStepFailed(){
        return checkCurrentLevel(LogLevel.TEST_FAIL) && checkPreviousLevel(LogLevel.CONFIGURATION_START);
    }

    public boolean isTestStepFailed(){
        return checkCurrentLevel(LogLevel.TEST_FAIL) && !checkPreviousLevel(LogLevel.TEST_FINISH);
    }

    public boolean isNeedAddTestStepsHeader() {
        return isPreviousLevelExists() ?
                checkPreviousLevel(LogLevel.CONFIGURATION_START)
                        && checkCurrentLevel(LogLevel.TEST_METHOD_START)
                : false;
    }

    public boolean isPreviousStepFinished() {
        return isPreviousLevelExists() ? checkPreviousLevel(LogLevel.TEST_FINISH) : false;
    }

    public String getFloatStyle(int pageStartPriority) {
        return (pageStartPriority <= getPreviousAssertPriority()) ? "left" : "none";
    }

    public Level getCurrentLogLevel() {
        return currentLogLevel;
    }

    public void setCurrentLogLevel(Level currentLogLevel) {
        this.currentLogLevel = currentLogLevel;
    }

    public void setPreviousLevel(Level previousLevel) {
        this.previousLevel = previousLevel;
    }

    public Level getPreviousLevel() {
        return previousLevel;
    }

    public int getPreviousAssertPriority() {
        return previousAssertPriority;
    }

    public void setPreviousAssertPriority(int previousAssertPriority) {
        this.previousAssertPriority = previousAssertPriority;
    }

    private boolean isTheSameLevels(){
        return previousLevel.equals(currentLogLevel);
    }

    private boolean checkPreviousLevel(Level logLevel){
        return previousLevel.equals(logLevel);
    }

    private boolean checkCurrentLevel(Level logLevel){
        return currentLogLevel.equals(logLevel);
    }

    private boolean isPreviousLevelExists(){
        return previousLevel != null;
    }
}

