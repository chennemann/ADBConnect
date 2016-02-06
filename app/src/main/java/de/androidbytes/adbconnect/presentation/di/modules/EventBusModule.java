package de.androidbytes.adbconnect.presentation.di.modules;


import com.squareup.otto.ThreadEnforcer;

import dagger.Module;
import dagger.Provides;
import de.androidbytes.adbconnect.presentation.di.ApplicationScope;
import de.androidbytes.adbconnect.presentation.eventbus.EventBus;


/**
 * Created by Christoph on 27.09.2015.
 */
@Module
public class EventBusModule {

    @Provides
    @ApplicationScope
    public EventBus provideEventBus() {
        return new EventBus(ThreadEnforcer.ANY);
    }

}
