package de.androidbytes.adbconnect.presentation.utils;


import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.List;


/**
 * Created by Christoph on 04.10.2015.
 */
public class NetworkUtility {

    public static List<WifiConfiguration> getConfiguredNetworks(Context context) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConfiguredNetworks();
    }
}
