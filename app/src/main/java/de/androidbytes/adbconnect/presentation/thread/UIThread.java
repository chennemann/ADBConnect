package de.androidbytes.adbconnect.presentation.thread;


import javax.inject.Inject;

import de.androidbytes.adbconnect.domain.executor.PostExecutionThread;
import de.androidbytes.adbconnect.presentation.di.ApplicationScope;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Created by Christoph on 19.09.2015.
 */
@ApplicationScope
public class UIThread implements PostExecutionThread {

    @Inject
    public UIThread() {}

    @Override
    public Scheduler getScheduler() {
        return AndroidSchedulers.mainThread();
    }
}
