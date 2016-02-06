package de.androidbytes.adbconnect.presentation.observer;


import de.androidbytes.adbconnect.domain.interactor.result.AdbStateEnum;
import de.androidbytes.adbconnect.domain.interactor.UseCase;
import de.androidbytes.adbconnect.presentation.observer.result.WirelessAdbStateObserverResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * Created by Christoph on 26.09.2015.
 */
@Accessors(prefix = "m")
public class AdbStateObserver extends UseCaseObserver<AdbStateEnum, ObserverHandler> {

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private AdbStateEnum mWirelessAdbState;

    public AdbStateObserver(ObserverHandler observerHandler, UseCase useCase, boolean multipleExecutionsPossible, boolean multipleSimultaneousExecutionsPossible) {

        super(observerHandler, useCase, multipleExecutionsPossible, multipleSimultaneousExecutionsPossible);
    }


    @Override
    public void error(Throwable e) {
    }

    @Override
    public void next(AdbStateEnum wirelessAdbState) {
        setWirelessAdbState(wirelessAdbState);
    }

    @Override
    public void completed() {
        getResultHandler().passUseCaseResult(new WirelessAdbStateObserverResult(getWirelessAdbState()));
    }
}
