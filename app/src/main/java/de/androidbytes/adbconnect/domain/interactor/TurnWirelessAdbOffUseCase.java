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


import de.androidbytes.adbconnect.domain.executor.PostExecutionThread;
import de.androidbytes.adbconnect.domain.executor.ThreadExecutor;
import rx.Observable;
import trikita.log.Log;

import javax.inject.Inject;



public class TurnWirelessAdbOffUseCase extends ExecuteRootCommandUseCase {

    @Inject
    public TurnWirelessAdbOffUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {

        super(threadExecutor, postExecutionThread);
    }

    @Override
    protected String[] getRootCommands() {

        return new String[] {
                    "setprop service.adb.tcp.port 0",   // Sets the ADB TCP Port to 0
                    "restart adbd",                            // Restarts the ADB Deamon
                    "echo 'ADB over WIFI turned off'"
        };
    }

    @Override
    protected Observable buildUseCaseObservable() {
        Log.i("Creating Observable to turn off Wireless Adb");
        return super.buildUseCaseObservable();
    }
}
