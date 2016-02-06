package de.androidbytes.adbconnect.domain.interactor.result;


/**
 * Created by Christoph on 28.09.2015.
 */
public enum AdbConnectRequirementsStateEnum {

    ADB_CONNECT_REQUIREMENTS_AVAILABLE("ADB_CONNECT_REQUIREMENTS_AVAILABLE"),
    ADB_CONNECT_REQUIREMENT_WIFI_NOT_ENABLED("ADB_CONNECT_REQUIREMENT_WIFI_NOT_ENABLED"),
    ADB_CONNECT_REQUIREMENT_WIFI_STATE_NOT_CONNECTED("ADB_CONNECT_REQUIREMENT_WIFI_STATE_NOT_CONNECTED"),
    ADB_CONNECT_REQUIREMENT_USB_DEBUGGING_DISABLED("ADB_CONNECT_REQUIREMENT_USB_DEBUGGING_DISABLED"),
    ADB_CONNECT_REQUIREMENT_DEVELOPER_OPTIONS_DISABLED("ADB_CONNECT_REQUIREMENT_DEVELOPER_OPTIONS_DISABLED");

    private final String fStateValue;

    AdbConnectRequirementsStateEnum(final String stateValue) {
        fStateValue = stateValue;
    }

    public String toString() {
        return fStateValue;
    }

}
