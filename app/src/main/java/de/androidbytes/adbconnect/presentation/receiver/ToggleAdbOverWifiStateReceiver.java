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
