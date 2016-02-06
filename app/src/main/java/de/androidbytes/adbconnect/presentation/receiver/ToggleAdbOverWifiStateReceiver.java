package de.androidbytes.adbconnect.presentation.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import de.androidbytes.adbconnect.R;
import de.androidbytes.adbconnect.presentation.utils.TaskerPluginUtility;
import de.androidbytes.adbconnect.presentation.utils.ServiceUtility;
import trikita.log.Log;


/**
 * Created by Christoph on 01.10.2015.
 */
public class ToggleAdbOverWifiStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getExtras() != null) {

            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

                if (activeNetworkInfo != null && activeNetworkInfo.isConnected() && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                    Log.i("Network " + activeNetworkInfo.getTypeName() + " connected");
                    ServiceUtility.runServiceAction(context, context.getString(R.string.service_action_network_connected));

                } else {
                    Log.i("There's no wifi network connectivity");
                    ServiceUtility.runServiceAction(context, context.getString(R.string.service_action_network_not_connected));
                }
            } else if (TaskerPluginUtility.ACTION_FIRE_SETTING.equals(intent.getAction())) {

                TaskerPluginUtility.scrub(intent);

                final Bundle bundle = intent.getBundleExtra(TaskerPluginUtility.BUNDLE);
                TaskerPluginUtility.scrub(bundle);

                if (TaskerPluginUtility.isBundleValid(bundle)) {
                    boolean shouldActivateAdbOverWifi = bundle.getBoolean(TaskerPluginUtility.BUNDLE_EXTRA_BOOLEAN_MESSAGE);

                    Log.i("Should the WirelessAdbManagingService activate ADB over WIFI? : " + shouldActivateAdbOverWifi);
                    String serviceAction = shouldActivateAdbOverWifi ? context.getResources().getString(R.string.service_action_start_adb_over_wifi) : context.getResources().getString(R.string.service_action_stop_adb_over_wifi);
                    ServiceUtility.runServiceAction(context, serviceAction);

                }
            }
        }

    }
}
