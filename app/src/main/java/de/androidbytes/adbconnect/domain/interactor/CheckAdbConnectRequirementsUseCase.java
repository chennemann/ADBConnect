package de.androidbytes.adbconnect.domain.interactor;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

import javax.inject.Inject;

import de.androidbytes.adbconnect.domain.interactor.result.AdbConnectRequirementsStateEnum;
import de.androidbytes.adbconnect.domain.executor.PostExecutionThread;
import de.androidbytes.adbconnect.domain.executor.ThreadExecutor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import rx.Observable;
import rx.Subscriber;
import trikita.log.Log;


/**
 * Created by Christoph on 28.09.2015.
 */
@Accessors(prefix = "m")
public class CheckAdbConnectRequirementsUseCase extends UseCase {

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Context mApplicationContext;

    @Inject
    protected CheckAdbConnectRequirementsUseCase(Context context, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {

        super(threadExecutor, postExecutionThread);
        setApplicationContext(context);
    }

    @Override
    protected Observable buildUseCaseObservable() {

        final boolean useGlobalSettingsSource = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
        final ContentResolver contentResolver = getApplicationContext().getContentResolver();

        return Observable.create(new Observable.OnSubscribe<AdbConnectRequirementsStateEnum>() {

            @SuppressLint("NewApi")
            @Override
            public void call(Subscriber<? super AdbConnectRequirementsStateEnum> subscriber) {

                Log.i("Check all ADB Connect Requirements");

                boolean wifiConnected = false;
                boolean adbDebuggingStateActive = false;
                boolean developerOptionsEnabled = false;


                Log.i("Check if WIFI is enabled");

                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                boolean wifiEnabled = wifiManager.isWifiEnabled();

                if(wifiEnabled) {

                    Log.i("Check if Device is connected to a Wireless Network");
                    ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        Network[] networks = connectivityManager.getAllNetworks();
                        if (networks != null) {
                            for (Network network : networks) {

                                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
                                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                                    wifiConnected = networkInfo.isConnected();
                                }
                            }
                        }
                    } else {
                        wifiConnected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
                    }

                    Log.i(wifiConnected ? "The Device is connected to a Wireless Network" : "The Device is not connected to a Wireless Network");


                    if (wifiConnected) {

                        Log.i("Check if USB Debugging is Enabled");

                        final int adbDebuggingState;

                        if (useGlobalSettingsSource) {
                            adbDebuggingState = Settings.Global.getInt(contentResolver, Settings.Global.ADB_ENABLED, 0);
                        } else {
                            adbDebuggingState = Settings.Secure.getInt(contentResolver, Settings.Secure.ADB_ENABLED, 0);
                        }

                        adbDebuggingStateActive = adbDebuggingState == 1;
                        Log.i(adbDebuggingStateActive ? "USB Debugging is currently enabled" : "USE Debugging is currently disabled");


                        if (!adbDebuggingStateActive) {

                            Log.i("Check if Developer Options are enabled");

                            final int developerOptionsEnabledState;

                            if (useGlobalSettingsSource) {
                                developerOptionsEnabledState = Settings.Global.getInt(contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);
                            } else {
                                developerOptionsEnabledState = Settings.Secure.getInt(contentResolver, Settings.Secure.DEVELOPMENT_SETTINGS_ENABLED, 0);
                            }

                            developerOptionsEnabled = developerOptionsEnabledState == 1;
                            Log.i(developerOptionsEnabled ? "Developer Options are Enabled." : "The Developer Options are not Unlocked");
                        }

                    }
                }


                if(!wifiEnabled) {
                    subscriber.onNext(AdbConnectRequirementsStateEnum.ADB_CONNECT_REQUIREMENT_WIFI_NOT_ENABLED);
                } else if(!wifiConnected) {
                    subscriber.onNext(AdbConnectRequirementsStateEnum.ADB_CONNECT_REQUIREMENT_WIFI_STATE_NOT_CONNECTED);
                } else if (!adbDebuggingStateActive) {
                    if(!developerOptionsEnabled) {
                        subscriber.onNext(AdbConnectRequirementsStateEnum.ADB_CONNECT_REQUIREMENT_DEVELOPER_OPTIONS_DISABLED);
                    } else {
                        subscriber.onNext(AdbConnectRequirementsStateEnum.ADB_CONNECT_REQUIREMENT_USB_DEBUGGING_DISABLED);
                    }
                } else {
                    subscriber.onNext(AdbConnectRequirementsStateEnum.ADB_CONNECT_REQUIREMENTS_AVAILABLE);
                }

                subscriber.onCompleted();

            }
        });

    }
}
