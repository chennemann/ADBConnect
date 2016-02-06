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


import de.androidbytes.adbconnect.domain.interactor.UseCase;
import de.androidbytes.adbconnect.domain.interactor.result.ConnectionInformation;
import de.androidbytes.adbconnect.presentation.observer.result.EvaluateCurrentIpAddressObserverResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Accessors(prefix = "m")
public class EvaluateCurrentIpAddressObserver extends UseCaseObserver<ConnectionInformation, ObserverHandler> {

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private ConnectionInformation mConnectionInformation;

    public EvaluateCurrentIpAddressObserver(ObserverHandler observerHandler, UseCase useCase) {

        super(observerHandler, useCase, true, false);
    }

    @Override
    protected void completed() {
        getResultHandler().passUseCaseResult(new EvaluateCurrentIpAddressObserverResult(getConnectionInformation()));
    }

    @Override
    protected void error(Throwable e) {
    }

    @Override
    protected void next(ConnectionInformation ipAddress) {
        setConnectionInformation(ipAddress);
    }
}
