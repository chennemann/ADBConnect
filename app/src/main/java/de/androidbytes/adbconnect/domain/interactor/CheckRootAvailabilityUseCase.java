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
import de.androidbytes.adbconnect.domain.executor.PostExecutionThread;
import de.androidbytes.adbconnect.domain.executor.ThreadExecutor;
import de.androidbytes.adbconnect.domain.interactor.result.RootAvailabilityEnum;
import rx.Observable;
import rx.Subscriber;

import javax.inject.Inject;



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
