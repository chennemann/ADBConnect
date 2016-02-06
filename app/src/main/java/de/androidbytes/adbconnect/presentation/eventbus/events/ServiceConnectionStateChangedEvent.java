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
package de.androidbytes.adbconnect.presentation.eventbus.events;


import android.support.annotation.IntDef;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



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
