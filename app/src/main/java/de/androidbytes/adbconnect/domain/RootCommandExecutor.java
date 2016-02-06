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
package de.androidbytes.adbconnect.domain;


import rx.Observable;
import rx.Subscriber;

import java.io.DataOutputStream;



public class RootCommandExecutor {

    public Observable<String> getStartWirelessAdbObservable() {

        return Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                runRootCommand("setprop service.adb.tcp.port 5555");
                if(subscriber.isUnsubscribed()) {
                    return;
                }
                runRootCommand("start adbd");
                if(subscriber.isUnsubscribed()) {
                    return;
                }
                subscriber.onCompleted();
            }
        });
    }

    public Observable<String> getEndWirelessAdbObservable() {

        return Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                runRootCommand("stop adbd");
                subscriber.onCompleted();
            }
        });
    }

    private static boolean runRootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                //TODO ExceptionHandling
                e.printStackTrace();
            }
        }
        return true;
    }

}