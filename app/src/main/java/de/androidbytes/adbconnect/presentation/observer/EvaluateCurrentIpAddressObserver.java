package de.androidbytes.adbconnect.presentation.observer;


import de.androidbytes.adbconnect.domain.interactor.UseCase;
import de.androidbytes.adbconnect.domain.interactor.result.ConnectionInformation;
import de.androidbytes.adbconnect.presentation.observer.result.EvaluateCurrentIpAddressObserverResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * Created by Christoph on 28.09.2015.
 */
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
