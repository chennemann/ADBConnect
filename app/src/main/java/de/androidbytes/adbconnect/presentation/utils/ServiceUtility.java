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
package de.androidbytes.adbconnect.presentation.utils;


import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import de.androidbytes.adbconnect.R;
import de.androidbytes.adbconnect.presentation.services.WirelessAdbManagingService;


/**
 * Collection of ServiceUtilitiesMethods to support managing {@link WirelessAdbManagingService}
 *
 * @author Christoph Hennemann
 */
public class ServiceUtility {

    /**
     * Binds the {@link WirelessAdbManagingService} to a {@link ServiceConnection}
     * @param applicationContext The Application Context to bind the {@link WirelessAdbManagingService} to.
     * @param serviceConnection The {@link ServiceConnection} that is used for Communication to the {@link WirelessAdbManagingService}.
     * @return True if the Connection is successfully established.
     */
    public static boolean bindService(Context applicationContext, ServiceConnection serviceConnection) {

        final Intent intent = new Intent(applicationContext, WirelessAdbManagingService.class);
        intent.setAction("ACTION_BIND_SERVICE");
        return applicationContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    /**
     * Invalidates the Binding of the {@link ServiceConnection} to the {@link WirelessAdbManagingService}.
     * @param applicationContext The Application Context where the {@link WirelessAdbManagingService} is currently bound to.
     * @param serviceConnection The {@link ServiceConnection} that was used for Communication to the {@link WirelessAdbManagingService}.
     * @return True if the Connection is successfully invalidated.
     */
    public static boolean unbindService(Context applicationContext, ServiceConnection serviceConnection) {

        if(null == serviceConnection) {
            return false;
        }

        applicationContext.unbindService(serviceConnection);
        return true;
    }


    public static void runServiceAction(Context context, String action) {
        runServiceAction(context, action, null);
    }

    private static void runServiceAction(Context context, String action, Bundle bundle) {
        Intent serviceIntent = new Intent(context, WirelessAdbManagingService.class);
        serviceIntent.setAction(action);
        context.startService(serviceIntent);
    }

    public static Intent getToggleIntent(Context context, boolean shouldActivateAdbOverWifi) {

        String serviceAction = shouldActivateAdbOverWifi ? context.getResources().getString(R.string.service_action_start_adb_over_wifi) : context.getResources().getString(R.string.service_action_stop_adb_over_wifi);

        Intent serviceIntent = new Intent(context.getApplicationContext(), WirelessAdbManagingService.class);
        serviceIntent.setAction(serviceAction);

        return serviceIntent;
    }
}
