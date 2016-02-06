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
package de.androidbytes.adbconnect.presentation.presenter;


import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.squareup.otto.Subscribe;
import de.androidbytes.adbconnect.domain.interactor.result.AdbStateEnum;
import de.androidbytes.adbconnect.domain.interactor.result.ConnectionInformation;
import de.androidbytes.adbconnect.presentation.di.OperatorScope;
import de.androidbytes.adbconnect.presentation.eventbus.EventBus;
import de.androidbytes.adbconnect.presentation.eventbus.events.AdbConnectRequirementsCheckedEvent;
import de.androidbytes.adbconnect.presentation.eventbus.events.CurrentIpAddressEvaluatedEvent;
import de.androidbytes.adbconnect.presentation.eventbus.events.ServiceConnectionStateChangedEvent;
import de.androidbytes.adbconnect.presentation.eventbus.events.WirelessAdbStateEvaluatedEvent;
import de.androidbytes.adbconnect.presentation.services.WirelessAdbManagingService;
import de.androidbytes.adbconnect.presentation.utils.PreferenceUtility;
import de.androidbytes.adbconnect.presentation.utils.ServiceUtility;
import de.androidbytes.adbconnect.presentation.view.ApplicationState;
import de.androidbytes.adbconnect.presentation.view.ViewInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import trikita.log.Log;

import javax.inject.Inject;

import static de.androidbytes.adbconnect.domain.interactor.result.AdbConnectRequirementsStateEnum.*;
import static de.androidbytes.adbconnect.domain.interactor.result.AdbStateEnum.ADB_STATE_WIRELESS_ADB_ACTIVE;
import static de.androidbytes.adbconnect.presentation.view.ApplicationState.*;


/**
 * {@link Presenter} for the MainLayoutPage {@link de.androidbytes.adbconnect.presentation.view.activity.MainScreenActivity}
 *
 * @author Christoph Hennemann
 */
@OperatorScope
@Accessors(prefix = "m")
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class MainScreenPresenter implements Presenter {

    private Context mApplicationContext;
    private EventBus mEventBus;

    @Setter(AccessLevel.PUBLIC)
    private ViewInterface mView;

    /**
     * The {@link WirelessAdbManagingService} is the service which controls the ADB State Changes of the Application.
     */
    private WirelessAdbManagingService mWirelessAdbManagingService;
    private ServiceConnection mWirelessAdbManagingServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {

            Log.i("WirelessAdbManagingService connected to MainScreenPresenter");
            setWirelessAdbManagingService(((WirelessAdbManagingService.ServiceBinder) binder).getService());

            getEventBus().post(new ServiceConnectionStateChangedEvent(ServiceConnectionStateChangedEvent.SERVICE_CONNECTION_ESTABLISHED));
            checkRequirements();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            Log.i("WirelessAdbManagingService disconntected");
            invalidateWirelessAdbManagingServiceBinding();
            getEventBus().post(new ServiceConnectionStateChangedEvent(ServiceConnectionStateChangedEvent.SERVICE_CONNECTION_INVALIDATED));
        }
    };

    @Inject
    public MainScreenPresenter(Context context, EventBus eventBus) {

        setApplicationContext(context);
        setEventBus(eventBus);
    }


//region Service Interaction Methods

    /**
     * Use the {@link ServiceConnection} to bind the {@link MainScreenPresenter} to a specified {@link ServiceConnection}
     */
    private void establishWirelessAdbManagingServiceBinding(ServiceConnection serviceConnection) {

        ServiceUtility.bindService(mApplicationContext, serviceConnection);
    }

    /**
     * Use the {@link ServiceConnection} to unbind the {@link MainScreenPresenter} from the {@link WirelessAdbManagingService}
     */
    private void invalidateWirelessAdbManagingServiceBinding() {

        if (ServiceUtility.unbindService(getApplicationContext(), getWirelessAdbManagingServiceConnection())) {
            setWirelessAdbManagingService(null);
        }
    }


    private boolean isWirelessAdbManagingServiceBound() {

        return null != getWirelessAdbManagingService();
    }

    private void checkRequirements() {

        if (isWirelessAdbManagingServiceBound()) {
            getWirelessAdbManagingService().checkAdbConnectRequirements();
        }
    }

    private void toggleWirelessAdbState(boolean setActive) {

        if (isWirelessAdbManagingServiceBound()) {
            getWirelessAdbManagingService().toggleWirelessAdbState(setActive);
        }
    }

    //endregion


//region Lifecycls Methods

    @Override
    public void start() {

    }

    @Override
    public void resume() {

        if (!isWirelessAdbManagingServiceBound()) {
            Log.i("Attempt to bind WirelessAdbManagingService");
            establishWirelessAdbManagingServiceBinding(getWirelessAdbManagingServiceConnection());
        } else {
            Log.i("WirelessAdbManagingService already bound");
            checkRequirements();
        }

        getEventBus().register(this);
    }

    @Override
    public void pause() {

        getEventBus().unregister(this);
    }

    @Override
    public void destroy() {

        if (isWirelessAdbManagingServiceBound()) {
            invalidateWirelessAdbManagingServiceBinding();
        }
    }

    //endregion


//region View Interaction Handler

    public void wirelessAdbSwitchClicked(boolean isChecked) {

        toggleWirelessAdbState(isChecked);
    }

//endregion


    private ApplicationState createApplicationState(int applicationStateId) {

        return createApplicationState(applicationStateId, new ConnectionInformation());
    }

    private ApplicationState createApplicationState(int applicationStateId, ConnectionInformation connectionInformation) {

        boolean useSimpleLayout = PreferenceUtility.useSimplifiedLayout(getApplicationContext());

        return new ApplicationState(getApplicationContext(), applicationStateId, connectionInformation, useSimpleLayout);
    }


    // Main Screen View Method

    /**
     * Updates the displayed Data by updating the View's ApplicationStateId
     *
     * @param applicationState Current {@link ApplicationState}
     */
    private void updateApplicationState(ApplicationState applicationState) {

        getView().updateViewData(applicationState);
    }


//region Service Callbacks

    /**
     * This Method will be called after the Requirements are Evaluated.
     *
     * @param adbConnectRequirementsCheckedEvent Event that holds the Requirement State represented through {@link de.androidbytes.adbconnect.domain.interactor.result.AdbConnectRequirementsStateEnum}
     */
    @Subscribe
    public void onAdbConnectRequirementsCheckedEvent(AdbConnectRequirementsCheckedEvent adbConnectRequirementsCheckedEvent) {

        if (adbConnectRequirementsCheckedEvent.getAdbConnectRequirementsState() == ADB_CONNECT_REQUIREMENTS_AVAILABLE) {

            Log.i("All Requirements are met. Proceeding...");

        } else if (adbConnectRequirementsCheckedEvent.getAdbConnectRequirementsState() == ADB_CONNECT_REQUIREMENT_WIFI_NOT_ENABLED) {

            Log.i("WIFI is currently turned Off. Displaying related Instructions");
            updateApplicationState(createApplicationState(WIFI_TURNED_OFF));

        } else if (adbConnectRequirementsCheckedEvent.getAdbConnectRequirementsState() == ADB_CONNECT_REQUIREMENT_WIFI_STATE_NOT_CONNECTED) {

            Log.i("WIFI is currently not connected. Displaying related Instructions");
            updateApplicationState(createApplicationState(WIFI_NOT_CONNECTED));

        } else if (adbConnectRequirementsCheckedEvent.getAdbConnectRequirementsState() == ADB_CONNECT_REQUIREMENT_DEVELOPER_OPTIONS_DISABLED) {

            Log.i("USB Debugging is disabled and the Developer Options are hidden. Displaying related Instructions");
            updateApplicationState(createApplicationState(DEVELOPER_OPTIONS_DISABLED));

        } else if (adbConnectRequirementsCheckedEvent.getAdbConnectRequirementsState() == ADB_CONNECT_REQUIREMENT_USB_DEBUGGING_DISABLED) {

            Log.i("USB Debugging is disabled. Displaying related Instructions");
            updateApplicationState(createApplicationState(USB_DEBUGGING_DISABLED));
        }

    }

    /**
     * This Method will be called after the Current Wireless Adb State was Evaluated.
     *
     * @param wirelessAdbStateEvaluatedEvent Event that holds the current Wireless Adb State represented through {@link AdbStateEnum}
     */
    @Subscribe
    public void onWirelessAdbStateEvaluatedEvent(WirelessAdbStateEvaluatedEvent wirelessAdbStateEvaluatedEvent) {

        if (wirelessAdbStateEvaluatedEvent.getWirelessAdbState() == ADB_STATE_WIRELESS_ADB_ACTIVE) {
            Log.i("Currently Adb over Wifi is active");
        } else {
            Log.i("Currently Adb over Wifi is not active");
        }
    }

    /**
     * This Method will be called after the Current Ip Address is Evaluated.
     *
     * @param currentIpAddressEvaluatedEvent Event that holds the current Ip Address and the current Adb Port
     */
    @Subscribe
    public void onCurrentIpAddressEvaluatedEvent(CurrentIpAddressEvaluatedEvent currentIpAddressEvaluatedEvent) {

        boolean usingRoot = PreferenceUtility.canUseRootPermissions(getApplicationContext());

        if (isWirelessAdbManagingServiceBound() && getWirelessAdbManagingService().isAdbOverWifiActive()) {

            if (usingRoot) {

                Log.i("The 'Adb over WIFI' - Service is currently active. Root Permissions are available");
                updateApplicationState(createApplicationState(ADB_OVER_WIFI_ENABLED, currentIpAddressEvaluatedEvent.getConnectionInformation()));

            } else {

                Log.i("The 'Adb over WIFI' - Service is currently active. Root Permissions are not available");
                updateApplicationState(createApplicationState(ADB_OVER_WIFI_ENABLED_NO_ROOT, currentIpAddressEvaluatedEvent.getConnectionInformation()));

            }

        } else {

            if (usingRoot) {

                Log.i("The 'Adb over WIFI' - Service is currently inactive. Root Permissions are available");
                updateApplicationState(createApplicationState(ADB_OVER_WIFI_DISABLED));

            } else {

                Log.i("The 'Adb over WIFI' - Service is currently inactive. Root Permissions are not available");
                updateApplicationState(createApplicationState(ADB_OVER_WIFI_DISABLED_NO_ROOT, currentIpAddressEvaluatedEvent.getConnectionInformation()));

            }
        }
    }


//endregion

}
