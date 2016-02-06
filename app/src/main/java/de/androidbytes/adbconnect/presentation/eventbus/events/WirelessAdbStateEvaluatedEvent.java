package de.androidbytes.adbconnect.presentation.eventbus.events;


import de.androidbytes.adbconnect.domain.interactor.result.AdbStateEnum;
import lombok.Getter;
import lombok.experimental.Accessors;


/**
 * Created by Christoph on 27.09.2015.
 */
@Accessors(prefix = "m")
public class WirelessAdbStateEvaluatedEvent {

    @Getter
    private AdbStateEnum mWirelessAdbState;

    public WirelessAdbStateEvaluatedEvent(AdbStateEnum wirelessAdbState) {
        mWirelessAdbState = wirelessAdbState;
    }

}
