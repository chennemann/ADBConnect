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
package de.androidbytes.adbconnect.domain.interactor;


import android.content.Context;
import de.androidbytes.adbconnect.domain.executor.PostExecutionThread;
import de.androidbytes.adbconnect.domain.executor.ThreadExecutor;
import de.androidbytes.adbconnect.presentation.utils.PreferenceUtility;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import rx.Observable;
import trikita.log.Log;

import javax.inject.Inject;


@Accessors(prefix = "m")
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class TurnWirelessAdbOnUseCase extends ExecuteRootCommandUseCase {

    private Context mContext;

    @Inject
    public TurnWirelessAdbOnUseCase(Context context, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {

        super(threadExecutor, postExecutionThread);
        setContext(context);
    }

    @Override
    protected String[] getRootCommands() {

        String dynamicPort = String.valueOf(PreferenceUtility.listenOnDynamicPort(getContext()));
        Log.i("The Adb Service will listen to port: " + dynamicPort);
        return new String[] {
                    String.format("setprop service.adb.tcp.port %s", dynamicPort),  // Sets the ADB TCP Port to {$dynamicPort}
                    "restart adbd",                            // Restarts the ADB Deamon
                    "echo 'ADB over WIFI turned on'"
        };
    }

    @Override
    protected Observable buildUseCaseObservable() {
        Log.i("Creating Observable to turn on Wireless Adb");
        return super.buildUseCaseObservable();
    }
}
