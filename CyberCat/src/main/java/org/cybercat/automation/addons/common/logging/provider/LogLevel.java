package org.cybercat.automation.addons.common.logging.provider;


import org.apache.logging.log4j.Level;

public class LogLevel {

    public static Level TEST_START = Level.forName("TEST_START", 551);
    public static Level CONFIGURATION_START = Level.forName("CONFIGURATION_START", 552);
    public static Level TEST_METHOD_START = Level.forName("TEST_METHOD_START", 553);
    public static Level STEP_START = Level.forName("STEP_START", 554);
    public static Level PAGE_START = Level.forName("PAGE_START", 555);
    public static Level ELEMENT_ACTION = Level.forName("ELEMENT_ACTION", 556);
    public static Level TEST_FINISH = Level.forName("TEST_FINISH", 557);
    public static Level TEST_FAIL = Level.forName("TEST_FAIL", 558);
    public static Level ADDITIONAL_DETAILS = Level.forName("ADDITIONAL_DETAILS", 559);
}
