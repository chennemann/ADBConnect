package de.androidbytes.adbconnect.domain.interactor;


import android.content.Context;

import javax.inject.Inject;

import de.androidbytes.adbconnect.domain.executor.PostExecutionThread;
import de.androidbytes.adbconnect.domain.executor.ThreadExecutor;
import de.androidbytes.adbconnect.presentation.utils.PreferenceUtility;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import rx.Observable;
import trikita.log.Log;


/**
 * Created by Christoph on 19.09.2015.
 */
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
