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
import de.androidbytes.adbconnect.domain.ShellInterface;
import de.androidbytes.adbconnect.domain.executor.PostExecutionThread;
import de.androidbytes.adbconnect.domain.executor.ThreadExecutor;
import de.androidbytes.adbconnect.domain.interactor.result.AdbStateEnum;
import rx.Observable;
import rx.Subscriber;
import trikita.log.Log;

import javax.inject.Inject;
import java.util.List;



public class CheckWirelessAdbStateUseCase extends UseCase {

    Context context;

    @Inject
    public CheckWirelessAdbStateUseCase(Context context, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {

        super(threadExecutor, postExecutionThread);
        this.context = context;
    }

    @Override
    protected Observable buildUseCaseObservable() {

        return Observable.create(new Observable.OnSubscribe<AdbStateEnum>() {

            @Override
            public void call(Subscriber<? super AdbStateEnum> subscriber) {

                Log.i("Check Current Wireless ADB State");
                final List<String> shellExecutionOutput;
                shellExecutionOutput = ShellInterface.executeShellCommands(new String[]{"getprop service.adb.tcp.port"});

                if(null != shellExecutionOutput &&
                            shellExecutionOutput.size() > 0
                            ) {
                    Log.i("ADB Port: " + shellExecutionOutput.get(0));
                }

                if (null != shellExecutionOutput &&
                            shellExecutionOutput.size() > 0 &&
                            !shellExecutionOutput.get(0).isEmpty() &&
                            !shellExecutionOutput.get(0).equalsIgnoreCase("-1") &&
                            !shellExecutionOutput.get(0).equalsIgnoreCase("0")) {
                    subscriber.onNext(AdbStateEnum.ADB_STATE_WIRELESS_ADB_ACTIVE);
                } else {
                    subscriber.onNext(AdbStateEnum.ADB_STATE_WIRELESS_ADB_INACTIVE);
                }

                subscriber.onCompleted();
            }
        });
    }
}
