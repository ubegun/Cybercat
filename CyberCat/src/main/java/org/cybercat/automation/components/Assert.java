package org.cybercat.automation.components;

import org.apache.commons.lang3.StringUtils;
import org.cybercat.automation.AutomationFrameworkException;

public class Assert {

    public static void assertContain(String message, String subString, String text) throws AutomationFrameworkException {
        if (subString == null)
            throw new AutomationFrameworkException(message + "the subString expression is null.");
        if (text == null)
            throw new AutomationFrameworkException(message + "the text expression is null.");
        if (!StringUtils.contains(text, subString))
            throw new AutomationFrameworkException(message + " The left expression is " + subString + ". The right expression is" + text);

    }

    public static void assertEqual(String message, Object obj1, Object obj2) throws AutomationFrameworkException {
        if (obj1 == null)
            throw new AutomationFrameworkException(message + " Expression from the left is null.");
        if (obj2 == null)
            throw new AutomationFrameworkException(message + " Expression from the right is null.");
        if (!obj1.equals(obj2))
            throw new AutomationFrameworkException(message + " The left expression is " + obj1.toString() + ". The right expression is" + obj2.toString());
    }
    
    public static void assertNotEmpty(String message, String obj1)  throws AutomationFrameworkException {
        assertNotNull(message, (Object) obj1);
        if (StringUtils.isEmpty(obj1))
            throw new AutomationFrameworkException(message + " Expression from the left is null.");        
    }

    public static void assertNotNull(String message, Object obj1)  throws AutomationFrameworkException {
        if (obj1 == null)
            throw new AutomationFrameworkException(message + " Expression from the left is null.");        
    }
}
