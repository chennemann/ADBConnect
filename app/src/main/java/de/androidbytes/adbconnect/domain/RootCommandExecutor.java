package de.androidbytes.adbconnect.domain;


import java.io.DataOutputStream;

import rx.Observable;
import rx.Subscriber;


/**
 * Created by Christoph on 18.09.2015.
 */
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