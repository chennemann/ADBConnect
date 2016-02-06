package de.androidbytes.adbconnect.presentation;


import android.app.Application;

import de.androidbytes.adbconnect.presentation.di.components.ApplicationComponent;
import de.androidbytes.adbconnect.presentation.di.components.DaggerApplicationComponent;
import de.androidbytes.adbconnect.presentation.di.modules.ApplicationModule;


/**
 * Created by Christoph on 17.09.2015.
 */
public class AdbConnectApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override public void onCreate() {
        super.onCreate();
        this.initializeInjector();
    }

    private void initializeInjector() {
        this.applicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();

    }

    public ApplicationComponent getApplicationComponent() {
        return this.applicationComponent;
    }
}
