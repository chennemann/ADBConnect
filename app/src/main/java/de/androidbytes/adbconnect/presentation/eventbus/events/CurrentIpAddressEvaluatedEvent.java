package de.androidbytes.adbconnect.presentation.eventbus.events;


import de.androidbytes.adbconnect.domain.interactor.result.ConnectionInformation;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * Created by Christoph on 28.09.2015.
 */
@Accessors(prefix = "m")
public class CurrentIpAddressEvaluatedEvent {

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private ConnectionInformation mConnectionInformation;

    public CurrentIpAddressEvaluatedEvent(ConnectionInformation currentIpAddress) {
        setConnectionInformation(currentIpAddress);
    }

}
