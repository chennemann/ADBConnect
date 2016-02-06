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
package de.androidbytes.adbconnect.presentation.di.modules;


import dagger.Module;
import dagger.Provides;
import de.androidbytes.adbconnect.domain.interactor.*;
import de.androidbytes.adbconnect.presentation.di.OperatorScope;

import javax.inject.Named;


/**
 * Dagger module that provides Adb related UseCases.
 */
@Module
public class AdbModule {

    public AdbModule() {}

    @Provides
    @OperatorScope
    @Named("turnWirelessAdbOn")
    UseCase provideTurnWirelessAdbOnUseCase(TurnWirelessAdbOnUseCase turnWirelessAdbOnUseCase) {

        return turnWirelessAdbOnUseCase;
    }

    @Provides
    @OperatorScope
    @Named("turnWirelessAdbOff")
    UseCase provideTurnWirelessAdbOffUseCase(TurnWirelessAdbOffUseCase turnWirelessAdbOffUseCase) {

        return turnWirelessAdbOffUseCase;
    }

    @Provides
    @OperatorScope
    @Named("checkWirelessAdbState")
    UseCase provideCheckWirelessAdbStateUseCase(CheckWirelessAdbStateUseCase checkWirelessAdbStateUseCase) {

        return checkWirelessAdbStateUseCase;
    }

    @Provides
    @OperatorScope
    @Named("evaluateCurrentIpAddress")
    UseCase provideEvaluateCurrentIpAddressUseCase(EvaluateCurrentIpAddressUseCase evaluateCurrentIpAddressUseCase) {

        return evaluateCurrentIpAddressUseCase;
    }
}