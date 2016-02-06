/**
 * Copyright (C) 2016 Christoph Hennemann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.androidbytes.adbconnect.presentation.view;


import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import de.androidbytes.adbconnect.R;
import de.androidbytes.adbconnect.domain.interactor.result.ConnectionInformation;
import de.androidbytes.adbconnect.presentation.utils.PreferenceUtility;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * An {@link de.androidbytes.adbconnect.presentation.view.ApplicationState} is a collection of dependent conditions that are combined to represent a possible state
 * the Application can have.
 * Every ApplicationStateId contains data that are mapped to the UI.
 * So the View can be updated by passing a single instance of an {@link de.androidbytes.adbconnect.presentation.view.ApplicationState}.
 */
@Accessors(prefix = "m")
@Getter
@Setter(AccessLevel.PRIVATE)
public class ApplicationState {

    private String mApplicationStateInformation = "";
    private String mExecutionSourceInformation = "";
    private String mExecutionCommandsInformation = "";
    private boolean mAdbOverWifiSwitchEnabled = false;
    private boolean mAdbOverWifiSwitchChecked = false;

    /**
     * An empty {@link de.androidbytes.adbconnect.presentation.view.ApplicationState}
     */
    public static final int EMPTY = 0x0000;

    /**
     * An {@link de.androidbytes.adbconnect.presentation.view.ApplicationState} where every Requirement was met and the 'Adb over WIFI' - Service is enabled.
     */
    public static final int ADB_OVER_WIFI_ENABLED = 0x0001;

    /**
     * An {@link de.androidbytes.adbconnect.presentation.view.ApplicationState} where every Requirement was met but the 'Adb over WIFI' - Service is disabled.
     */
    public static final int ADB_OVER_WIFI_DISABLED = 0x0002;

    /**
     * An {@link de.androidbytes.adbconnect.presentation.view.ApplicationState} where every Requirement was met and the 'Adb over WIFI' - Service is enabled.
     * Use when Application can't obtain root permissions.
     */
    public static final int ADB_OVER_WIFI_ENABLED_NO_ROOT = 0x0003;

    /**
     * An {@link de.androidbytes.adbconnect.presentation.view.ApplicationState} where every Requirement was met but the 'Adb over WIFI' - Service is disabled.
     * Use when Application can't obtain root permissions.
     */
    public static final int ADB_OVER_WIFI_DISABLED_NO_ROOT = 0x0004;

    /**
     * An {@link de.androidbytes.adbconnect.presentation.view.ApplicationState} to notify that WIFI is currently turned off and should be enabled before enabling
     * the 'Adb over WIFI' - Service.
     */
    public static final int WIFI_TURNED_OFF = 0x0005;

    /**
     * An {@link de.androidbytes.adbconnect.presentation.view.ApplicationState} to notify that WIFI is currently not connected.
     */
    public static final int WIFI_NOT_CONNECTED = 0x0006;

    /**
     * An {@link de.androidbytes.adbconnect.presentation.view.ApplicationState} to notify that USB Debugging is currently disabled and the Development Options are
     * hidden.
     */
    public static final int DEVELOPER_OPTIONS_DISABLED = 0x0007;

    /**
     * An {@link de.androidbytes.adbconnect.presentation.view.ApplicationState} to notify that USB Debugging is currently disabled.
     */
    public static final int USB_DEBUGGING_DISABLED = 0x0008;


    /**
     * Possible ApplicationStates mapped to an Integer.
     * Exposes the available ApplicationStates to the public that don't need any {@link ConnectionInformation} provided.
     */
    @IntDef({
                EMPTY,
                ADB_OVER_WIFI_ENABLED,
                ADB_OVER_WIFI_ENABLED_NO_ROOT,
                ADB_OVER_WIFI_DISABLED,
                ADB_OVER_WIFI_DISABLED_NO_ROOT,
                WIFI_TURNED_OFF,
                WIFI_NOT_CONNECTED,
                DEVELOPER_OPTIONS_DISABLED,
                USB_DEBUGGING_DISABLED
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ApplicationStateId {}


    /**
     * Create a new {@link de.androidbytes.adbconnect.presentation.view.ApplicationState} and load the applicable Configuration.
     *
     * @param applicationStateId ID of the Configuration of the desired {@link de.androidbytes.adbconnect.presentation.view.ApplicationState}
     * @param connectionInformation Current {@link ConnectionInformation} of the Device
     */
    public ApplicationState(Context context, @ApplicationStateId int applicationStateId, ConnectionInformation connectionInformation, boolean useSimpleLayout) {

        loadApplicationStateConfiguration(context, applicationStateId, connectionInformation, useSimpleLayout);
    }

    private void loadApplicationStateConfiguration(Context context, int applicationStateId, ConnectionInformation connectionInformation, boolean useSimpleLayout) {

        String ipAddress = connectionInformation.getIpAddress();
        String adbPort = connectionInformation.getAdbPort();

        String applicationStateInformation;
        String executionSourceInformation;
        String executionCommandsInformation;
        boolean adbSwitchEnabled;
        boolean adbSwitchChecked;

        switch (applicationStateId) {

            case EMPTY:
                applicationStateInformation = getStringFromResource(context, R.string.empty_application_state_information);
                executionSourceInformation = getStringFromResource(context, R.string.empty_application_state_information);
                executionCommandsInformation = useSimpleLayout ? getStringFromResource(context, R.string.empty_execution_commands_information_simple) : getStringFromResource(context, R.string.empty_application_state_information);
                adbSwitchEnabled = false;
                adbSwitchChecked = false;
                break;
            case ADB_OVER_WIFI_ENABLED:
                applicationStateInformation = getStringFromResource(context, R.string.adb_over_wifi_enabled_application_state_information);
                executionSourceInformation = getStringFromResource(context, R.string.adb_over_wifi_enabled_execution_source_information);
                executionCommandsInformation = useSimpleLayout ? getStringFromResource(context, R.string.adb_over_wifi_enabled_execution_commands_information_simple, ipAddress, adbPort) : getStringFromResource(context, R.string.adb_over_wifi_enabled_execution_commands_information, ipAddress, adbPort);
                adbSwitchEnabled = true;
                adbSwitchChecked = true;
                break;
            case ADB_OVER_WIFI_DISABLED:
                applicationStateInformation = getStringFromResource(context, R.string.adb_over_wifi_disabled_application_state_information);
                executionSourceInformation = getStringFromResource(context, R.string.adb_over_wifi_disabled_execution_source_information);
                executionCommandsInformation = useSimpleLayout ? getStringFromResource(context, R.string.adb_over_wifi_disabled_execution_commands_information_simple) : getStringFromResource(context, R.string.adb_over_wifi_disabled_execution_commands_information);
                adbSwitchEnabled = true;
                adbSwitchChecked = false;
                break;
            case ADB_OVER_WIFI_ENABLED_NO_ROOT:
                applicationStateInformation = getStringFromResource(context, R.string.adb_over_wifi_enabled_no_root_application_state_information);
                executionSourceInformation = getStringFromResource(context, R.string.adb_over_wifi_enabled_no_root_execution_source_information);
                executionCommandsInformation = useSimpleLayout ? getStringFromResource(context, R.string.adb_over_wifi_enabled_no_root_execution_commands_information_simple, ipAddress, adbPort) : getStringFromResource(context, R.string.adb_over_wifi_enabled_no_root_execution_commands_information, ipAddress, adbPort);
                adbSwitchEnabled = false;
                adbSwitchChecked = true;
                break;
            case ADB_OVER_WIFI_DISABLED_NO_ROOT:
                applicationStateInformation = getStringFromResource(context, R.string.adb_over_wifi_disabled_no_root_application_state_information);
                executionSourceInformation = getStringFromResource(context, R.string.adb_over_wifi_disabled_no_root_execution_source_information);

                adbPort  = String.valueOf(PreferenceUtility.listenOnDynamicPort(context));

                executionCommandsInformation = useSimpleLayout ? getStringFromResource(context, R.string.adb_over_wifi_disabled_no_root_execution_commands_information_simple, adbPort, ipAddress, adbPort) : getStringFromResource(context, R.string.adb_over_wifi_disabled_no_root_execution_commands_information, adbPort, ipAddress, adbPort);
                adbSwitchEnabled = false;
                adbSwitchChecked = false;
                break;
            case WIFI_TURNED_OFF:
                applicationStateInformation = getStringFromResource(context, R.string.wifi_turned_off_application_state_information);
                executionSourceInformation = getStringFromResource(context, R.string.wifi_turned_off_execution_source_information);
                executionCommandsInformation = useSimpleLayout ? getStringFromResource(context, R.string.wifi_turned_off_execution_commands_information_simple) : getStringFromResource(context, R.string.wifi_turned_off_execution_commands_information);
                adbSwitchEnabled = false;
                adbSwitchChecked = false;
                break;
            case WIFI_NOT_CONNECTED:
                applicationStateInformation = getStringFromResource(context, R.string.wifi_not_connected_application_state_information);
                executionSourceInformation = getStringFromResource(context, R.string.wifi_not_connected_execution_source_information);
                executionCommandsInformation = useSimpleLayout ? getStringFromResource(context, R.string.wifi_not_connected_execution_commands_information_simple) : getStringFromResource(context, R.string.wifi_not_connected_execution_commands_information);
                adbSwitchEnabled = false;
                adbSwitchChecked = false;
                break;
            case DEVELOPER_OPTIONS_DISABLED:
                applicationStateInformation = getStringFromResource(context, R.string.developer_options_disabled_application_state_information);
                executionSourceInformation = getStringFromResource(context, R.string.developer_options_disabled_execution_source_information);
                executionCommandsInformation = useSimpleLayout ? getStringFromResource(context, R.string.developer_options_disabled_execution_commands_information_simple) : getStringFromResource(context, R.string.developer_options_disabled_execution_commands_information);
                adbSwitchEnabled = false;
                adbSwitchChecked = false;
                break;
            case USB_DEBUGGING_DISABLED:
                applicationStateInformation = getStringFromResource(context, R.string.usb_debugging_disabled_application_state_information);
                executionSourceInformation = getStringFromResource(context, R.string.usb_debugging_disabled_execution_source_information);
                executionCommandsInformation = useSimpleLayout ? getStringFromResource(context, R.string.usb_debugging_disabled_execution_commands_information_simple) : getStringFromResource(context, R.string.usb_debugging_disabled_execution_commands_information);
                adbSwitchEnabled = false;
                adbSwitchChecked = false;
                break;
            default:
                applicationStateInformation = getStringFromResource(context, R.string.empty_application_state_information);
                executionSourceInformation = getStringFromResource(context, R.string.empty_application_state_information);
                executionCommandsInformation = useSimpleLayout ? getStringFromResource(context, R.string.empty_application_state_information) : getStringFromResource(context, R.string.empty_application_state_information);
                adbSwitchEnabled = false;
                adbSwitchChecked = false;
                break;

        }


        setApplicationStateInformation(applicationStateInformation);
        setExecutionSourceInformation(executionSourceInformation);
        setExecutionCommandsInformation(executionCommandsInformation);
        setAdbOverWifiSwitchEnabled(adbSwitchEnabled);
        setAdbOverWifiSwitchChecked(adbSwitchChecked);

    }

    private String getStringFromResource(Context context, @StringRes int stringResId) {
        return context.getString(stringResId);
    }

    private String getStringFromResource(Context context, @StringRes int stringResId, String param1) {
        return context.getString(stringResId, param1);
    }

    private String getStringFromResource(Context context, @StringRes int stringResId, String param1, String param2) {
        return context.getString(stringResId, param1, param2);
    }

    private String getStringFromResource(Context context, @StringRes int stringResId, String param1, String param2, String param3) {
        return context.getString(stringResId, param1, param2, param3);
    }

}
