package de.androidbytes.adbconnect.presentation.observer;


import de.androidbytes.adbconnect.domain.interactor.result.AdbConnectRequirementsStateEnum;
import de.androidbytes.adbconnect.domain.interactor.UseCase;
import de.androidbytes.adbconnect.presentation.observer.result.AdbConnectRequirementsObserverResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * Created by Christoph on 26.09.2015.
 */
@Accessors(prefix = "m")
public class AdbConnectRequirementsObserver extends UseCaseObserver<AdbConnectRequirementsStateEnum, ObserverHandler> {

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private AdbConnectRequirementsStateEnum mAdbConnectRequirements;

    public AdbConnectRequirementsObserver(ObserverHandler observerHandler, UseCase useCase, boolean multipleExecutionsPossible, boolean multipleSimultaneousExecutionsPossible) {

        super(observerHandler, useCase, multipleExecutionsPossible, multipleSimultaneousExecutionsPossible);
    }


    @Override
    public void error(Throwable e) {
    }

    @Override
    public void next(AdbConnectRequirementsStateEnum adbConnectRequirement) {
        setAdbConnectRequirements(adbConnectRequirement);
    }

    @Override
    public void completed() {
        getResultHandler().passUseCaseResult(new AdbConnectRequirementsObserverResult(getAdbConnectRequirements()));
    }
}
