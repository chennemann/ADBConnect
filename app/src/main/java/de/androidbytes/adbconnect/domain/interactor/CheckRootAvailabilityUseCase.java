package de.androidbytes.adbconnect.domain.interactor;


import javax.inject.Inject;

import de.androidbytes.adbconnect.domain.ShellInterface;
import de.androidbytes.adbconnect.domain.interactor.result.RootAvailabilityEnum;
import de.androidbytes.adbconnect.domain.executor.PostExecutionThread;
import de.androidbytes.adbconnect.domain.executor.ThreadExecutor;
import rx.Observable;
import rx.Subscriber;


/**
 * Created by Christoph on 26.09.2015.
 */
public class CheckRootAvailabilityUseCase extends UseCase {

    @Inject
    protected CheckRootAvailabilityUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {

        super(threadExecutor, postExecutionThread);
    }

    @Override
    protected Observable buildUseCaseObservable() {

        return Observable.create(new Observable.OnSubscribe<RootAvailabilityEnum>() {

            @Override
            public void call(Subscriber<? super RootAvailabilityEnum> subscriber) {
                if(ShellInterface.isRootAvailable()) {
                    subscriber.onNext(RootAvailabilityEnum.ROOT_AVAILABILITY_AVAILABLE);
                } else {
                    subscriber.onNext(RootAvailabilityEnum.ROOT_AVAILABILITY_NOT_AVAILABLE);
                }

                subscriber.onCompleted();
            }
        });
    }
}
