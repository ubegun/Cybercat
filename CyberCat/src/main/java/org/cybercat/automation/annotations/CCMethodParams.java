package org.cybercat.automation.annotations;

public @interface CCMethodParams {
    public String type() default "";
    public String value() default "";
}