package de.androidbytes.adbconnect.domain.interactor;


import de.androidbytes.adbconnect.domain.ShellInterface;
import de.androidbytes.adbconnect.domain.interactor.result.AdbStateEnum;
import de.androidbytes.adbconnect.domain.executor.PostExecutionThread;
import de.androidbytes.adbconnect.domain.executor.ThreadExecutor;
import rx.Observable;
import rx.Subscriber;
import trikita.log.Log;


/**
 * Created by Christoph on 21.09.2015.
 */
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
