package de.androidbytes.adbconnect.presentation.eventbus;


import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Inject;


/**
 * Created by Christoph on 27.09.2015.
 */
public class EventBus extends Bus {

    @Inject
    public EventBus(ThreadEnforcer threadEnforcer) {
        super(threadEnforcer);
    }
}
