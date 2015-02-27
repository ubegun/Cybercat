package org.cybercat.automation.testng;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cybercat.automation.addons.common.logging.provider.HtmlLogBuilder;
import org.cybercat.automation.addons.common.logging.provider.HtmlLogHelper;
import org.cybercat.automation.addons.common.logging.provider.LogLevel;
import org.cybercat.automation.addons.common.logging.provider.LogLevelAnalyzer;
import org.testng.Assert;

import java.util.Collection;


public class TestNGAssert {

    private static final Logger log = LogManager.getLogger(TestNGAssert.class);

    private static int priorityForMessage;


    public static void assertTrue(boolean condition, String conditionToCheck) {
        mainLogMessage(HtmlLogHelper.makeBold("ASSERT TRUE: ") + conditionToCheck);
        Assert.assertTrue(condition, conditionToCheck);
        passedLogMessage();
    }

    public static void assertFalse(boolean condition, String conditionToCheck){
        mainLogMessage(HtmlLogHelper.makeBold("ASSERT FALSE: ") + conditionToCheck);
        Assert.assertFalse(condition, conditionToCheck);
        passedLogMessage();
    }

    public static void assertActualEqualsExpected(Object actual, Object expected, String conditionToCheck){
        mainLogMessage(HtmlLogHelper.makeBold("ASSERT EQUALS: ") +" ACTUAL - " + actual + "; EXPECTED - " + expected);
        Assert.assertEquals(actual, expected, conditionToCheck);
        passedLogMessage();
    }

    public static void assertCollectionEquals(Collection<?> actual, Collection<?> expected, String conditionToCheck){
        mainLogMessage(HtmlLogHelper.makeBold("ASSERT COLLECTION EQUALS: ") +" ACTUAL - " + actual + "; EXPECTED - " + expected);
        Assert.assertEquals(actual, expected, conditionToCheck);
        passedLogMessage();
    }

    public static void assertActualNotEqualsExpected(Object actual, Object expected, String conditionToCheck){
        mainLogMessage(HtmlLogHelper.makeBold("ASSERT NOT EQUALS: ") +" ACTUAL - " + actual + "; EXPECTED - " + expected);
        Assert.assertNotEquals(actual, expected, conditionToCheck);
        passedLogMessage();
    }

    public static void assertObjectNotNull(Object object, String conditionToCheck){
        mainLogMessage(HtmlLogHelper.makeBold("ASSERT OBJECT NOT NULL: ") +" Received object - " + object.getClass().getSimpleName());
        Assert.assertNotNull(object, conditionToCheck);
        passedLogMessage();
    }

    public static void assertObjectNull(Object object, String conditionToCheck){
        mainLogMessage(HtmlLogHelper.makeBold("ASSERT OBJECT NULL: ") +" Received object - " + (object == null? object : object.getClass().getSimpleName()));
        Assert.assertNull(object, conditionToCheck);
        passedLogMessage();
    }

    private static void mainLogMessage(String message){
        priorityForMessage = 100;
        log.log(LogLevel.PAGE_START, message, HtmlLogBuilder.PageStartStatus.ASSERT_START, priorityForMessage);
    }

    private static void passedLogMessage(){
        priorityForMessage = 101;
        log.log(LogLevel.PAGE_START, " => " + HtmlLogHelper.addColorToText(HtmlLogHelper.makeBold("PASSED"), "#138700"), HtmlLogBuilder.PageStartStatus.ASSERT_START, priorityForMessage);
    }


}
