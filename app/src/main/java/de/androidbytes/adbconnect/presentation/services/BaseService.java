package de.androidbytes.adbconnect.presentation.services;

import android.app.Service;

/**
 * Created by chennemann on 29.09.2015.
 */
public abstract class BaseService extends Service {

    public abstract void checkCurrentWirelessAdbState();
    public abstract void toggleWirelessAdbState(boolean setActive);
    public abstract void evaluateCurrentIpAddress();
    public abstract void checkAdbConnectRequirements();

}
