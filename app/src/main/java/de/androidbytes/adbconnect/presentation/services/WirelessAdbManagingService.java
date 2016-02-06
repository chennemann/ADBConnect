package de.androidbytes.adbconnect.presentation.services;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import de.androidbytes.adbconnect.R;
import de.androidbytes.adbconnect.domain.interactor.UseCase;
import de.androidbytes.adbconnect.domain.interactor.result.AdbConnectRequirementsStateEnum;
import de.androidbytes.adbconnect.domain.interactor.result.AdbStateEnum;
import de.androidbytes.adbconnect.domain.interactor.result.ConnectionInformation;
import de.androidbytes.adbconnect.presentation.AdbConnectApplication;
import de.androidbytes.adbconnect.presentation.di.HasComponent;
import de.androidbytes.adbconnect.presentation.di.components.AdbManagingServiceComponent;
import de.androidbytes.adbconnect.presentation.di.components.DaggerAdbManagingServiceComponent;
import de.androidbytes.adbconnect.presentation.eventbus.EventBus;
import de.androidbytes.adbconnect.presentation.eventbus.events.AdbConnectRequirementsCheckedEvent;
import de.androidbytes.adbconnect.presentation.eventbus.events.CurrentIpAddressEvaluatedEvent;
import de.androidbytes.adbconnect.presentation.eventbus.events.ServiceConnectionStateChangedEvent;
import de.androidbytes.adbconnect.presentation.eventbus.events.WirelessAdbStateEvaluatedEvent;
import de.androidbytes.adbconnect.presentation.observer.AdbConnectRequirementsObserver;
import de.androidbytes.adbconnect.presentation.observer.AdbStateObserver;
import de.androidbytes.adbconnect.presentation.observer.EvaluateCurrentIpAddressObserver;
import de.androidbytes.adbconnect.presentation.observer.ObserverHandler;
import de.androidbytes.adbconnect.presentation.observer.UseCaseObserver;
import de.androidbytes.adbconnect.presentation.observer.result.AdbConnectRequirementsObserverResult;
import de.androidbytes.adbconnect.presentation.observer.result.EvaluateCurrentIpAddressObserverResult;
import de.androidbytes.adbconnect.presentation.observer.result.ObserverResult;
import de.androidbytes.adbconnect.presentation.observer.result.WirelessAdbStateObserverResult;
import de.androidbytes.adbconnect.presentation.utils.InteractionUtility;
import de.androidbytes.adbconnect.presentation.utils.PreferenceUtility;
import de.androidbytes.adbconnect.presentation.utils.ServiceUtility;
import de.androidbytes.adbconnect.presentation.view.activity.MainScreenActivity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import trikita.log.Log;


/**
 * Created by Christoph on 26.09.2015.
 */
@Accessors(prefix = "m")
public class WirelessAdbManagingService extends BaseService implements HasComponent<AdbManagingServiceComponent>, ObserverHandler {


    @Getter(AccessLevel.PRIVATE)
    private AdbStateObserver mTurnWirelessAdbOnObserver;

    @Getter(AccessLevel.PRIVATE)
    private AdbStateObserver mTurnWirelessAdbOffObserver;

    @Getter(AccessLevel.PRIVATE)
    private AdbStateObserver mCheckWirelessAdbStateObserver;

    @Getter(AccessLevel.PRIVATE)
    private AdbConnectRequirementsObserver mCheckAdbConnectRequirementsObserver;

    @Getter(AccessLevel.PRIVATE)
    private EvaluateCurrentIpAddressObserver mEvaluateCurrentIpAddressObserver;

    @Getter(AccessLevel.PRIVATE)
    private List<UseCaseObserver> mUseCaseObservers = new ArrayList<>();


    private final IBinder mBinder = new ServiceBinder();
    private AdbManagingServiceComponent mAdbManagingServiceComponent;

    @Inject
    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PROTECTED)
    protected EventBus mEventBus;

    @Inject
    protected Context mApplicationContext;

    @Inject
    @Named("turnWirelessAdbOn")
    UseCase turnWirelessAdbOnUseCase;

    @Inject
    @Named("turnWirelessAdbOff")
    UseCase turnWirelessAdbOffUseCase;

    @Inject
    @Named("checkAdbConnectRequirements")
    UseCase checkAdbConnectRequirementsUseCase;

    @Inject
    @Named("checkWirelessAdbState")
    UseCase checkWirelessAdbStateUseCase;

    @Inject
    @Named("evaluateCurrentIpAddress")
    UseCase evaluateCurrentIpAddressUseCase;

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private boolean mRequirementStateOk;

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PRIVATE)
    private boolean mAdbOverWifiActive;

    private static final int NOTIFICATION_ID = 0x115379CA;

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private boolean mTurnedOffManually = true;


    /*
     * (non-Javadoc)
	 *
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String intentAction = "no";

        if (intent != null) {
            intentAction = intent.getAction();

            if (intentAction != null) {

                if (intentAction.equals(getString(R.string.service_action_network_not_connected)) || intentAction.equals(getString(R.string.service_action_network_connected))) {
                    checkAdbConnectRequirements();
                    setTurnedOffManually(false);
                } else if (intentAction.equals(getString(R.string.service_action_start_adb_over_wifi))) {
                    toggleWirelessAdbState(true);
                } else if (intentAction.equals(getString(R.string.service_action_stop_adb_over_wifi))) {
                    toggleWirelessAdbState(false);
                }

            } else {
                intentAction = "no";
            }
        }

        Log.i("WirelessAdbManagingService with " + intentAction + " Action Called");

        return Service.START_STICKY;

    }

    @Override
    public void onCreate() {

        super.onCreate();

        mAdbManagingServiceComponent = DaggerAdbManagingServiceComponent.builder()
                    .applicationComponent(((AdbConnectApplication) getApplication()).getApplicationComponent())
                    .build();

        getComponent().inject(this);

        initialize();
    }

    private void initialize() {

        setTurnWirelessAdbOnObserver(new AdbStateObserver(this, turnWirelessAdbOnUseCase, true, false));
        setTurnWirelessAdbOffObserver(new AdbStateObserver(this, turnWirelessAdbOffUseCase, true, false));
        setCheckWirelessAdbStateObserver(new AdbStateObserver(this, checkWirelessAdbStateUseCase, true, false));
        setCheckAdbConnectRequirementsObserver(new AdbConnectRequirementsObserver(this, checkAdbConnectRequirementsUseCase, true, false));
        setEvaluateCurrentIpAddressObserver(new EvaluateCurrentIpAddressObserver(this, evaluateCurrentIpAddressUseCase));

        getEventBus().register(this);
    }

//    region Observer Setter

    private void setTurnWirelessAdbOnObserver(AdbStateObserver turnWirelessAdbOnObserver) {

        mTurnWirelessAdbOnObserver = turnWirelessAdbOnObserver;
        addObserver(mTurnWirelessAdbOnObserver);
    }

    private void setTurnWirelessAdbOffObserver(AdbStateObserver turnWirelessAdbOffObserver) {

        mTurnWirelessAdbOffObserver = turnWirelessAdbOffObserver;
        addObserver(mTurnWirelessAdbOffObserver);
    }

    private void setCheckWirelessAdbStateObserver(AdbStateObserver checkWirelessAdbStateObserver) {

        mCheckWirelessAdbStateObserver = checkWirelessAdbStateObserver;
        addObserver(mCheckWirelessAdbStateObserver);
    }

    private void setCheckAdbConnectRequirementsObserver(AdbConnectRequirementsObserver adbConnectRequirementsObserver) {

        mCheckAdbConnectRequirementsObserver = adbConnectRequirementsObserver;
        addObserver(mCheckAdbConnectRequirementsObserver);
    }

    private void setEvaluateCurrentIpAddressObserver(EvaluateCurrentIpAddressObserver evaluateCurrentIpAddressObserver) {

        mEvaluateCurrentIpAddressObserver = evaluateCurrentIpAddressObserver;
        addObserver(mEvaluateCurrentIpAddressObserver);
    }

    private void addObserver(UseCaseObserver useCaseObserver) {

        getUseCaseObservers().add(useCaseObserver);
    }

//    endregion


    @Override
    public void onDestroy() {

        super.onDestroy();

        for (UseCaseObserver useCaseObserver : getUseCaseObservers()) {
            useCaseObserver.unsubscribeObserver();
        }

        getEventBus().unregister(this);
    }

    /**
     * {@link AdbManagingServiceComponent} for Dagger Dependency Injection
     *
     * @return {@link AdbManagingServiceComponent}
     */
    @Override
    public AdbManagingServiceComponent getComponent() {

        return mAdbManagingServiceComponent;
    }

    private void showNotification(ConnectionInformation connectionInformation) {

        if (PreferenceUtility.shouldShowNotification(getApplicationContext())) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            builder.setSmallIcon(R.drawable.ic_adb_white_24dp);
            builder.setColor(ContextCompat.getColor(this.getApplicationContext(), R.color.colorAccent));

            Intent intent = new Intent(this.getApplicationContext(), MainScreenActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(pendingIntent);

            builder.setContentTitle(getResources().getString(R.string.app_name));

            if (isAdbOverWifiActive()) {
                builder.setContentText(getResources().getString(R.string.adb_over_wifi_enabled_execution_commands_information_simple, connectionInformation.getIpAddress(), connectionInformation.getAdbPort()));
            } else {
                if (PreferenceUtility.canUseRootPermissions(getApplicationContext())) {
                    builder.setContentText(getResources().getString(R.string.adb_over_wifi_disabled_execution_commands_information_notification));
                } else {
                    String port = String.valueOf(PreferenceUtility.listenOnDynamicPort(getApplicationContext()));
                    builder.setContentText(getResources().getString(R.string.adb_over_wifi_disabled_no_root_execution_commands_information_simple, port));
                }
            }

            if (PreferenceUtility.canUseRootPermissions(getApplicationContext())) {

                int actionIconResId;
                String serviceActionLabel;

                if (isAdbOverWifiActive()) {
                    actionIconResId = R.drawable.ic_phonelink_off_white_24dp;
                    serviceActionLabel = getApplicationContext().getResources().getString(R.string.service_action_stop_adb_over_wifi_label);
                } else {
                    actionIconResId = R.drawable.ic_phonelink_white_24dp;
                    serviceActionLabel = getApplicationContext().getResources().getString(R.string.service_action_start_adb_over_wifi_label);
                }

                Intent serviceIntent = ServiceUtility.getToggleIntent(getApplicationContext(), !isAdbOverWifiActive());
                PendingIntent serviceActionIntent = PendingIntent.getService(getApplicationContext(), 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.addAction(actionIconResId, serviceActionLabel, serviceActionIntent);
            }


            if (PreferenceUtility.showPersistentNotification(getApplicationContext()) && !(!isAdbOverWifiActive() && PreferenceUtility.persistNotificationOnlyOnActiveAdbOverWifi(getApplicationContext()))) {
                builder.setOngoing(true);

                int notificationPriority;
                switch (PreferenceUtility.getNotificationPriority(getApplicationContext())) {
                    case 0:
                        notificationPriority = NotificationCompat.PRIORITY_MIN;
                        break;
                    case 1:
                        notificationPriority = NotificationCompat.PRIORITY_LOW;
                        break;
                    case 2:
                        notificationPriority = NotificationCompat.PRIORITY_DEFAULT;
                        break;
                    case 3:
                        notificationPriority = NotificationCompat.PRIORITY_HIGH;
                        break;
                    case 4:
                        notificationPriority = NotificationCompat.PRIORITY_MAX;
                        break;
                    default:
                        notificationPriority = NotificationCompat.PRIORITY_DEFAULT;
                        break;
                }

                Log.i("Notification Priority: " + notificationPriority);
                builder.setPriority(notificationPriority);
            } else {
                builder.setOngoing(false);
            }

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    /**
     * Method to pass the result of a {@link de.androidbytes.adbconnect.presentation.observer.UseCaseObserver} to the Implementation of the {@link ObserverHandler}
     *
     * @param observerResult Result of the executed {@link UseCase}
     */
    @Override
    public void passUseCaseResult(ObserverResult observerResult) {

        Log.i("ObserverResult of Type: " + observerResult.getClass().getSimpleName());

        if (observerResult instanceof AdbConnectRequirementsObserverResult) {

            AdbConnectRequirementsStateEnum adbConnectRequirementsState = ((AdbConnectRequirementsObserverResult) observerResult).getAdbConnectRequirements();

            setRequirementStateOk(adbConnectRequirementsState == AdbConnectRequirementsStateEnum.ADB_CONNECT_REQUIREMENTS_AVAILABLE);

            getEventBus().post(new AdbConnectRequirementsCheckedEvent(adbConnectRequirementsState));

            if(!isRequirementStateOk()) {
                clearNotification();
            }

            //Check Adb over Wifi State regardless of the needed requirements to turn it off if something changed.
            checkCurrentWirelessAdbState();

        } else if (observerResult instanceof WirelessAdbStateObserverResult) {

            AdbStateEnum wirelessAdbState = ((WirelessAdbStateObserverResult) observerResult).getWirelessAdbState();

            if(wirelessAdbState == AdbStateEnum.ADB_STATE_NO_ROOT_PERMISSIONS_GRANTED) {
                InteractionUtility.showToast(getApplicationContext(), getString(R.string.no_root_permissions_granted), Toast.LENGTH_LONG);
                PreferenceUtility.canUseRootPermissions(getApplicationContext(), false);
                checkCurrentWirelessAdbState();
            } else {

                if (isRequirementStateOk()) {

                    setAdbOverWifiActive(wirelessAdbState == AdbStateEnum.ADB_STATE_WIRELESS_ADB_ACTIVE || wirelessAdbState == AdbStateEnum.ADB_STATE_WIRELESS_ADB_TURNED_ON);

                    if (wirelessAdbState == AdbStateEnum.ADB_STATE_WIRELESS_ADB_TURNED_OFF) {
                        setTurnedOffManually(true);
                    } else if(wirelessAdbState == AdbStateEnum.ADB_STATE_WIRELESS_ADB_TURNED_ON) {
                        PreferenceUtility.addToUsageCount(getApplicationContext());
                    }

                    boolean isTrustedNetwork = isTrustedNetwork();

                    if (!isTurnedOffManually() && isTrustedNetwork && !isAdbOverWifiActive() && PreferenceUtility.shouldAutostartOnTrustedNetworks(getApplicationContext())) {

                        Log.i("Autostart ADB over WIFI");
                        toggleWirelessAdbState(true);

                    } else {

                        getEventBus().post(new WirelessAdbStateEvaluatedEvent(wirelessAdbState));
                        evaluateCurrentIpAddress();
                    }

                } else {
                    if (wirelessAdbState == AdbStateEnum.ADB_STATE_WIRELESS_ADB_ACTIVE) {
                        Log.i("Wireless Adb is active althought the requirements are not met anymore");
                        getTurnWirelessAdbOffObserver().execute();
                    }
                }
            }

        } else if (observerResult instanceof EvaluateCurrentIpAddressObserverResult) {

            ConnectionInformation connectionInformation = ((EvaluateCurrentIpAddressObserverResult) observerResult).getConnectionInformation();

            if (isAdbOverWifiActive() || (isTrustedNetwork() && !PreferenceUtility.showNotificationOnlyOnActiveAdbOverWifi(getApplicationContext()))) {
                Log.i("Display Notification");
                showNotification(connectionInformation);
            } else {
                Log.i("Clear Notification");
                clearNotification();
            }

            getEventBus().post(new CurrentIpAddressEvaluatedEvent(connectionInformation));
        }

    }

    private boolean isTrustedNetwork() {

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        String currentConntectedSSID = wifiManager.getConnectionInfo().getSSID().replaceAll("\"", "");

        Set<String> trustedNetworks = PreferenceUtility.getTrustedNetworks(getApplicationContext());
        boolean isTrustedNetwork;

        if(trustedNetworks != null && trustedNetworks.size() > 0) {
            isTrustedNetwork = trustedNetworks.contains(currentConntectedSSID);
        } else {
            isTrustedNetwork = false;
        }

        return isTrustedNetwork;
    }

    public void checkCurrentWirelessAdbState() {

        if (!getCheckWirelessAdbStateObserver().isCurrentlyRunning()) {
            Log.i("Check current Wireless Adb State");
            getCheckWirelessAdbStateObserver().execute();
        }

    }

    public void toggleWirelessAdbState(boolean setActive) {

        if (setActive) {
            getTurnWirelessAdbOnObserver().execute();
        } else {
            getTurnWirelessAdbOffObserver().execute();
        }

    }

    public void evaluateCurrentIpAddress() {

        getEvaluateCurrentIpAddressObserver().execute();

    }

    public void checkAdbConnectRequirements() {

        getCheckAdbConnectRequirementsObserver().execute();

    }


    /**
     * Service Binder class for {@link WirelessAdbManagingService}. This class is used to bind
     * to the {@link WirelessAdbManagingService}
     */
    public class ServiceBinder extends Binder {

        /**
         * Returns the currently instantiated WirelessAdbManagingService
         *
         * @return {@link WirelessAdbManagingService}
         */
        public WirelessAdbManagingService getService() {

            return WirelessAdbManagingService.this;
        }
    }

    /**
     * Method will be executed as soon as the Service is connected to the {@link de.androidbytes.adbconnect.presentation.presenter.MainScreenPresenter}
     *
     * @param serviceConnectionStateChangedEvent {@link ServiceConnectionStateChangedEvent} thats holds information about the Connection status
     */
    @Subscribe
    public void onServiceConnectionStateChangedEvent(ServiceConnectionStateChangedEvent serviceConnectionStateChangedEvent) {
    }
}
