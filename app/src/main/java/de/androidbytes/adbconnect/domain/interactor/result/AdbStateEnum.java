package de.androidbytes.adbconnect.domain.interactor.result;


/**
 * Created by Christoph on 22.09.2015.
 */
public enum AdbStateEnum {

    ADB_STATE_NO_ROOT_PERMISSIONS_GRANTED("ADB_STATE_NO_ROOT_PERMISSIONS_GRANTED"),
    ADB_STATE_WIRELESS_ADB_ACTIVE("ADB_STATE_WIRELESS_ADB_ACTIVE"),
    ADB_STATE_WIRELESS_ADB_TURNED_ON("ADB_STATE_WIRELESS_ADB_TURNED_ON"),
    ADB_STATE_WIRELESS_ADB_INACTIVE("ADB_STATE_WIRELESS_ADB_INACTIVE"),
    ADB_STATE_WIRELESS_ADB_TURNED_OFF("ADB_STATE_WIRELESS_ADB_TURNED_OFF");

    private final String fStateValue;

    AdbStateEnum(final String stateValue) {
        fStateValue = stateValue;
    }

    public String toString() {
        return fStateValue;
    }

}
