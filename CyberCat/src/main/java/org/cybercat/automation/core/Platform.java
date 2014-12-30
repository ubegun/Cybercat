package org.cybercat.automation.core;

/**
 * @author SavrukSergiy
 *
 */
public enum Platform {

    WEB("web"),
    MOBILE("mobile"),
    TABLET("tablet");

    private String value;

    Platform(String value) {
        this.value = value;
    }

    public static Platform fromValue(String value) {
        for (Platform type : Platform.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return value;
    }
}