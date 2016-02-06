package de.androidbytes.adbconnect.domain.interactor;


import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import de.androidbytes.adbconnect.domain.ShellInterface;
import de.androidbytes.adbconnect.domain.executor.PostExecutionThread;
import de.androidbytes.adbconnect.domain.executor.ThreadExecutor;
import de.androidbytes.adbconnect.domain.interactor.result.AdbStateEnum;
import rx.Observable;
import rx.Subscriber;
import trikita.log.Log;


/**
 * Created by Christoph on 26.09.2015.
 */
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
