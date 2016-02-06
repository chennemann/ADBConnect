package de.androidbytes.adbconnect.presentation.eventbus.events;


import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * Created by Christoph on 27.09.2015.
 */
@Accessors(prefix = "m")
public class ServiceConnectionStateChangedEvent {

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private int mServiceConnectionState;

    public static final int SERVICE_CONNECTION_ESTABLISHED = 0x00000001;
    public static final int SERVICE_CONNECTION_INVALIDATED = 0x00000000;

    @IntDef({SERVICE_CONNECTION_ESTABLISHED, SERVICE_CONNECTION_INVALIDATED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ServiceConnectionState {}

    public ServiceConnectionStateChangedEvent(@ServiceConnectionState int serviceConnectionState) {
        setServiceConnectionState(serviceConnectionState);
    }

}
