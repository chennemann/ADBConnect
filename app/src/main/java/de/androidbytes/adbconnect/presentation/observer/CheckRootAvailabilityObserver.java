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
package de.androidbytes.adbconnect.presentation.observer;


import de.androidbytes.adbconnect.domain.interactor.result.RootAvailabilityEnum;
import de.androidbytes.adbconnect.domain.interactor.UseCase;
import de.androidbytes.adbconnect.presentation.observer.result.CheckRootAvailabilityObserverResult;



public class CheckRootAvailabilityObserver extends UseCaseObserver<RootAvailabilityEnum, ObserverHandler> {

    private RootAvailabilityEnum mRootAvailability;

    public CheckRootAvailabilityObserver(ObserverHandler observerHandler, UseCase useCase, boolean multipleExecutionsPossible ,boolean multipleSimultaneousExecutionsPossible) {

        super(observerHandler, useCase, multipleExecutionsPossible, multipleSimultaneousExecutionsPossible);
    }


    @Override
    public void error(Throwable e) {
    }

    @Override
    public void next(RootAvailabilityEnum rootAvailability) {
        mRootAvailability = rootAvailability;
    }

    @Override
    public void completed() {
        getResultHandler().passUseCaseResult(new CheckRootAvailabilityObserverResult(mRootAvailability));
    }

}
