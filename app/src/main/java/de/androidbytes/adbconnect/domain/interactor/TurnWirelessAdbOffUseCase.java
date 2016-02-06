package de.androidbytes.adbconnect.domain.interactor;


import javax.inject.Inject;

import de.androidbytes.adbconnect.domain.executor.PostExecutionThread;
import de.androidbytes.adbconnect.domain.executor.ThreadExecutor;
import rx.Observable;
import trikita.log.Log;


/**
 * Created by Christoph on 19.09.2015.
 */
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
