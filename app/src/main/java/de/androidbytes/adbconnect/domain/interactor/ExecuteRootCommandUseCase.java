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


import de.androidbytes.adbconnect.domain.ShellInterface;
import de.androidbytes.adbconnect.domain.interactor.result.AdbStateEnum;
import de.androidbytes.adbconnect.domain.executor.PostExecutionThread;
import de.androidbytes.adbconnect.domain.executor.ThreadExecutor;
import rx.Observable;
import rx.Subscriber;
import trikita.log.Log;



public abstract class ExecuteRootCommandUseCase extends UseCase {

    protected ExecuteRootCommandUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {

        super(threadExecutor, postExecutionThread);
    }

    protected abstract String[] getRootCommands();

    protected static boolean runRootCommands(String[] commands) {

        for (String command : commands) {
            Log.i("Execution Commands: \t\t\t" + command);
        }

        return ShellInterface.executeRootCommands(commands);

    }

    @Override
    protected Observable buildUseCaseObservable() {
        return Observable.create(new Observable.OnSubscribe<AdbStateEnum>() {
            @Override
            public void call(Subscriber<? super AdbStateEnum> subscriber) {
                if(runRootCommands(getRootCommands())) {
                    if (ExecuteRootCommandUseCase.this instanceof TurnWirelessAdbOnUseCase) {
                        Log.i("ADB over WIFI turned on");
                        subscriber.onNext(AdbStateEnum.ADB_STATE_WIRELESS_ADB_TURNED_ON);
                    } else if (ExecuteRootCommandUseCase.this instanceof TurnWirelessAdbOffUseCase) {
                        Log.i("ADB over WIFI turned off");
                        subscriber.onNext(AdbStateEnum.ADB_STATE_WIRELESS_ADB_TURNED_OFF);
                    }

                } else {
                    Log.i("No root permissions granted");
                    subscriber.onNext(AdbStateEnum.ADB_STATE_NO_ROOT_PERMISSIONS_GRANTED);
                }
                subscriber.onCompleted();
            }
        });
    }
}
