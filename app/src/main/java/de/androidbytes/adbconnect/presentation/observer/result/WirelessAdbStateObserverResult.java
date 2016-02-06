package de.androidbytes.adbconnect.presentation.observer.result;


import de.androidbytes.adbconnect.domain.interactor.result.AdbStateEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * Created by Christoph on 27.09.2015.
 */
@Accessors(prefix = "m")
public class WirelessAdbStateObserverResult implements ObserverResult {

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PRIVATE)
    AdbStateEnum mWirelessAdbState;

    public WirelessAdbStateObserverResult(AdbStateEnum wirelessAdbState) {

        setWirelessAdbState(wirelessAdbState);
    }
}
