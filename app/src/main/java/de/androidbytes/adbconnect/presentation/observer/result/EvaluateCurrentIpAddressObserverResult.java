package de.androidbytes.adbconnect.presentation.observer.result;


import de.androidbytes.adbconnect.domain.interactor.result.ConnectionInformation;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * Created by Christoph on 28.09.2015.
 */
@Accessors(prefix = "m")
public class EvaluateCurrentIpAddressObserverResult implements ObserverResult {

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PRIVATE)
    private ConnectionInformation mConnectionInformation;


    public EvaluateCurrentIpAddressObserverResult(ConnectionInformation ipAddress) {
        setConnectionInformation(ipAddress);
    }

}
