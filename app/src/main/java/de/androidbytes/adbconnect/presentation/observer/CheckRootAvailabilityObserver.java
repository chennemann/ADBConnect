package de.androidbytes.adbconnect.presentation.observer;


import de.androidbytes.adbconnect.domain.interactor.result.RootAvailabilityEnum;
import de.androidbytes.adbconnect.domain.interactor.UseCase;
import de.androidbytes.adbconnect.presentation.observer.result.CheckRootAvailabilityObserverResult;


/**
 * Created by Christoph on 26.09.2015.
 */
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
