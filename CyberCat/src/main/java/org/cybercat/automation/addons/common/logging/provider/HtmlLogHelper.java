package org.cybercat.automation.addons.common.logging.provider;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cybercat.automation.core.Platform;
import org.cybercat.automation.test.IVersionControl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

public class HtmlLogHelper {

    private static final Logger log = LogManager.getLogger(HtmlLogHelper.class);

    public static String makeBold(String message){
        return String.format("<b>%s</b>", message);
    }

    public static String makeUnderline(String message){
        return String.format("<u>%s</u>", message);
    }

    public static String addColorToText(String message, String colorName){
        return String.format("<font color=\"%s\">%s</font>", colorName, message);
    }

    public static void writePlatformVsVersionLog(Platform[] platforms, int version){
        StringBuilder platformsLine = new StringBuilder();
        for(Platform platformItem : platforms) {
            platformsLine.append(platformItem == null ? "&nbsp;&nbsp;All platforms supported" :  platformItem + ";&nbsp;");
        }
        log.log(LogLevel.CONFIGURATION_START, makeBold("&nbsp;&nbsp;Version: ") + version);
        log.log(LogLevel.CONFIGURATION_START, makeBold("&nbsp;&nbsp;Supported platforms: ") + platformsLine.toString());

    }

    public static void writeFeatureNameLog(Map<Object, Object> featureFields, Object currentFeature){
        StringBuilder logLine = new StringBuilder();
        if(!featureFields.isEmpty()) {
            for (Map.Entry<Object, Object> feature: featureFields.entrySet()) {
                if(Arrays.asList(currentFeature.getClass().getInterfaces()).contains(feature.getValue())){
                    logLine.append(" <p style=\"margin-left:8px\">")
                            .append(makeBold("Parent feature:  "))
                            .append(feature.getKey());
                }
            }
        }
        String additionLog = (logLine.toString().equals("")) ? "" : logLine.toString();
        log.log(LogLevel.CONFIGURATION_START, makeBold("&nbsp;&nbsp;Name: ") + currentFeature.getClass().getSimpleName() + additionLog);
    }

    public static Map getFeatureFields(Object feature, Map featureFields){
        Field[] fields = feature.getClass().getDeclaredFields();
        for (Field field : fields){
            if(Arrays.asList(field.getType().getInterfaces()).contains(IVersionControl.class)){
                featureFields.put(field.getDeclaringClass(), field.getType());
            }
        }
        return featureFields;
    }

    public static String getTestMethodName(String message){
        String[] splited = message.split(":");
        String testMethod = splited[2];
        return testMethod.split("<br>")[0].trim();
    }
}
