package de.androidbytes.adbconnect.presentation.presenter;


import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.android.vending.billing.IInAppBillingService;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import de.androidbytes.adbconnect.R;
import de.androidbytes.adbconnect.domain.interactor.result.AdbStateEnum;
import de.androidbytes.adbconnect.domain.interactor.result.ConnectionInformation;
import de.androidbytes.adbconnect.presentation.di.OperatorScope;
import de.androidbytes.adbconnect.presentation.eventbus.EventBus;
import de.androidbytes.adbconnect.presentation.eventbus.events.AdbConnectRequirementsCheckedEvent;
import de.androidbytes.adbconnect.presentation.eventbus.events.CurrentIpAddressEvaluatedEvent;
import de.androidbytes.adbconnect.presentation.eventbus.events.ServiceConnectionStateChangedEvent;
import de.androidbytes.adbconnect.presentation.eventbus.events.WirelessAdbStateEvaluatedEvent;
import de.androidbytes.adbconnect.presentation.services.WirelessAdbManagingService;
import de.androidbytes.adbconnect.presentation.utils.InAppBillingUtility;
import de.androidbytes.adbconnect.presentation.utils.InteractionUtility;
import de.androidbytes.adbconnect.presentation.utils.PreferenceUtility;
import de.androidbytes.adbconnect.presentation.utils.ServiceUtility;
import de.androidbytes.adbconnect.presentation.utils.Sku;
import de.androidbytes.adbconnect.presentation.utils.billing.IabHelper;
import de.androidbytes.adbconnect.presentation.utils.billing.IabResult;
import de.androidbytes.adbconnect.presentation.utils.billing.Inventory;
import de.androidbytes.adbconnect.presentation.utils.billing.Purchase;
import de.androidbytes.adbconnect.presentation.view.ApplicationState;
import de.androidbytes.adbconnect.presentation.view.ViewInterface;
import de.androidbytes.adbconnect.presentation.view.dialogs.PurchaseItemAdapter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import trikita.log.Log;

import static de.androidbytes.adbconnect.domain.interactor.result.AdbConnectRequirementsStateEnum.ADB_CONNECT_REQUIREMENTS_AVAILABLE;
import static de.androidbytes.adbconnect.domain.interactor.result.AdbConnectRequirementsStateEnum.ADB_CONNECT_REQUIREMENT_DEVELOPER_OPTIONS_DISABLED;
import static de.androidbytes.adbconnect.domain.interactor.result.AdbConnectRequirementsStateEnum.ADB_CONNECT_REQUIREMENT_USB_DEBUGGING_DISABLED;
import static de.androidbytes.adbconnect.domain.interactor.result.AdbConnectRequirementsStateEnum.ADB_CONNECT_REQUIREMENT_WIFI_NOT_ENABLED;
import static de.androidbytes.adbconnect.domain.interactor.result.AdbConnectRequirementsStateEnum.ADB_CONNECT_REQUIREMENT_WIFI_STATE_NOT_CONNECTED;
import static de.androidbytes.adbconnect.domain.interactor.result.AdbStateEnum.ADB_STATE_WIRELESS_ADB_ACTIVE;
import static de.androidbytes.adbconnect.presentation.view.ApplicationState.ADB_OVER_WIFI_DISABLED;
import static de.androidbytes.adbconnect.presentation.view.ApplicationState.ADB_OVER_WIFI_DISABLED_NO_ROOT;
import static de.androidbytes.adbconnect.presentation.view.ApplicationState.ADB_OVER_WIFI_ENABLED;
import static de.androidbytes.adbconnect.presentation.view.ApplicationState.ADB_OVER_WIFI_ENABLED_NO_ROOT;
import static de.androidbytes.adbconnect.presentation.view.ApplicationState.DEVELOPER_OPTIONS_DISABLED;
import static de.androidbytes.adbconnect.presentation.view.ApplicationState.USB_DEBUGGING_DISABLED;
import static de.androidbytes.adbconnect.presentation.view.ApplicationState.WIFI_NOT_CONNECTED;
import static de.androidbytes.adbconnect.presentation.view.ApplicationState.WIFI_TURNED_OFF;


/**
 * {@link Presenter} for the MainLayoutPage {@link de.androidbytes.adbconnect.presentation.view.activity.MainScreenActivity}
 *
 * @author Christoph Hennemann
 */
@OperatorScope
@Accessors(prefix = "m")
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class MainScreenPresenter implements Presenter, IabHelper.OnIabSetupFinishedListener, IabHelper.OnIabPurchaseFinishedListener, IabHelper.QueryInventoryFinishedListener, IabHelper.OnConsumeFinishedListener {

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

    private IInAppBillingService mInAppBillingService;
    private IabHelper mInAppBillingHelper;
    private boolean mInAppBillingReady;
    private boolean mPurchaseButtonClickedWhileNotReady;
    private int mSelectedSkuIndex;
    private String mPurchasedSku = "";
    private ServiceConnection mInAppBillingServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {

            Log.i("InAppBillingService connected to MainScreenPresenter");
            setInAppBillingService(IInAppBillingService.Stub.asInterface(binder));
            setupInAppBilling();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            Log.i("InAppBillingService disconnected");
            invalidateInAppBillingServiceBinding();
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

    /**
     * Use the {@link ServiceConnection} to bind the {@link MainScreenPresenter} to a specified {@link ServiceConnection}
     */
    private void establishInAppBillingServiceBinding() {

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        getApplicationContext().bindService(serviceIntent, getInAppBillingServiceConnection(), Context.BIND_AUTO_CREATE);
    }

    /**
     * Use the {@link ServiceConnection} to unbind the {@link MainScreenPresenter} from the {@link WirelessAdbManagingService}
     */
    private void invalidateInAppBillingServiceBinding() {

        if (ServiceUtility.unbindService(getApplicationContext(), getInAppBillingServiceConnection())) {
            setInAppBillingService(null);
        }
    }

    private boolean isWirelessAdbManagingServiceBound() {

        return null != getWirelessAdbManagingService();
    }

    private boolean isInAppBillingServiceBound() {

        return null != getInAppBillingService();
    }

    private void checkRequirements() {

        if (isWirelessAdbManagingServiceBound()) {
            getWirelessAdbManagingService().checkAdbConnectRequirements();
        }
    }

    private void checkWirelessAdbState() {

        if (isWirelessAdbManagingServiceBound()) {
            getWirelessAdbManagingService().checkCurrentWirelessAdbState();
        }
    }

    private void toggleWirelessAdbState(boolean setActive) {

        if (isWirelessAdbManagingServiceBound()) {
            getWirelessAdbManagingService().toggleWirelessAdbState(setActive);
        }
    }

    private void evaluateIp() {

        if (isWirelessAdbManagingServiceBound()) {
            getWirelessAdbManagingService().evaluateCurrentIpAddress();
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

        if (!isInAppBillingServiceBound()) {
            Log.i("Attempt to bind InAppBillingService");
            establishInAppBillingServiceBinding();
        } else {
            Log.i("InAppBillingService already bound");
            setupInAppBilling();
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

        if (isInAppBillingServiceBound()) {
            invalidateInAppBillingServiceBinding();
        }
    }

    //endregion


//region View Interaction Handler


    public void actionButtonClicked(View view) {

        Log.i("Purchase Button Clicked");

        if (isInAppBillingReady()) {
            displayPurchaseDialog();
        } else {
            setPurchaseButtonClickedWhileNotReady(true);
        }
    }

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


//    region InAppBilling

    public boolean isInAppBillingResult(int requestCode, int resultCode, Intent data) {

        return getInAppBillingHelper().handleActivityResult(requestCode, resultCode, data);
    }


    private void setupInAppBilling() {

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6BumlCHyyY+LbGk/LRoUCAIekxT29F0gJyIiZvt26g1m2Bophqv1WL/Wm6m+0ipyHOlyO6B9PBOdRa0OgKStNWq9GHPTccMT3geJn+t/yEJPEfbsfTRHiJmB09ismptVW4D1sCkmfd4YbMjw9FaM32kBjpKGE8ivZLpa+6F88Iyi4Rtx/ZBxU3ZCNOzufmzIE93QTrFsjbEzhXXbsbAyF8KxRiJ7KnhAvAaqvMZtneqcUSlrEg27SqqnFODw8obBJHssWzyR+veH45ba1+Kc4Gt8vQsQfcy3y5kESgUs0sUoxzAvKxugJq675lWVUVg8A6AkXOHtwbn+jKXSOubitwIDAQAB";

        mInAppBillingHelper = new IabHelper(getApplicationContext(), base64EncodedPublicKey);

        getInAppBillingHelper().startSetup(this);
    }

    private void displayPurchaseDialog() {

        setPurchaseButtonClickedWhileNotReady(false);

        Context activityContext = ((Fragment) getView()).getActivity();

        AlertDialog.Builder dialog = new AlertDialog.Builder(activityContext);
        dialog.setTitle(activityContext.getString(R.string.purchase_dialog_title));

        View customDialogView = LayoutInflater.from(activityContext).inflate(R.layout.alertdialog_purchase_item_dialog, null, false);
        ListView listView = (ListView) customDialogView.findViewById(R.id.listview_purchase_items);

        final PurchaseItemAdapter adapter = new PurchaseItemAdapter(activityContext, R.layout.alertdialog_purchase_item_list_item_layout, InAppBillingUtility.getAvailableSkus(getApplicationContext()));
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setDivider(null);

        dialog.setView(customDialogView);
        dialog.setPositiveButton(activityContext.getString(R.string.purchase_dialog_positive_button), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Sku selectedSku = InAppBillingUtility.getSkuAtIndex(adapter.getSelectedIndex());
                if (selectedSku != null) {
                    setPurchasedSku(selectedSku.getSku());
                    getInAppBillingHelper().launchPurchaseFlow(((Fragment) getView()).getActivity(), selectedSku.getSku(), 1, MainScreenPresenter.this);
                }
            }
        });
        dialog.setNegativeButton(activityContext.getString(R.string.purchase_dialog_negative_button), null);
        dialog.show();
    }

    private void consumeItem() {

        getInAppBillingHelper().queryInventoryAsync(this);
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase info) {

        if (result.isFailure()) {
            Log.d("Purchase failed");
        } else if (result.isSuccess()) {
            Log.i("Purchase successfully completed");
            consumeItem();
            PreferenceUtility.hasUserDonated(getApplicationContext(), true);
            InteractionUtility.showSnackbar(getView().getRoot(), "Thank you for supporting Development", Snackbar.LENGTH_LONG);
        }

    }

    @Override
    public void onIabSetupFinished(IabResult result) {

        if (!result.isSuccess()) {
            Log.d("In-App-Billing Setup Failed: " + result);
            setInAppBillingReady(false);
        } else {
            Log.i("In-App-Billing Setup OK");
            setInAppBillingReady(true);

            if (isPurchaseButtonClickedWhileNotReady()) {
                displayPurchaseDialog();
            }
        }
    }

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

        if (result.isSuccess()) {
            Log.i("Inventory queried successfully");
            getInAppBillingHelper().consumeAsync(inventory.getPurchase(getPurchasedSku()), this);
        } else {
            Log.i("Inventory query failed");
        }
    }

    @Override
    public void onConsumeFinished(Purchase purchase, IabResult result) {

        if (result.isSuccess()) {
            Log.i("Purchase consumed successfully");
        } else {
            Log.i("Puchase could not consumed successfully");
        }
    }

//    endregion
}
