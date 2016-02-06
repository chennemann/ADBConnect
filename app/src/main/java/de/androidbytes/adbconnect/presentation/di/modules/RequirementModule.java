package de.androidbytes.adbconnect.presentation.di.modules;


import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import de.androidbytes.adbconnect.domain.interactor.CheckAdbConnectRequirementsUseCase;
import de.androidbytes.adbconnect.domain.interactor.UseCase;
import de.androidbytes.adbconnect.presentation.di.OperatorScope;


/**
 * Created by Christoph on 28.09.2015.
 */
@Module
public class RequirementModule {

    public RequirementModule() {}

    @Provides
    @OperatorScope
    @Named("checkAdbConnectRequirements")
    UseCase provideCheckAdbConnectRequirementsUseCase(CheckAdbConnectRequirementsUseCase checkAdbConnectRequirementsUseCase) {

        return checkAdbConnectRequirementsUseCase;
    }
}
