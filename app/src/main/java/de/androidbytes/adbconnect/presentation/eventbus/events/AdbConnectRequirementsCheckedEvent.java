package de.androidbytes.adbconnect.presentation.eventbus.events;


import de.androidbytes.adbconnect.domain.interactor.result.AdbConnectRequirementsStateEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * Created by Christoph on 28.09.2015.
 */
@Accessors(prefix = "m")
public class AdbConnectRequirementsCheckedEvent {

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private AdbConnectRequirementsStateEnum mAdbConnectRequirementsState;

    public AdbConnectRequirementsCheckedEvent(AdbConnectRequirementsStateEnum adbConnectRequirementsState) {
        setAdbConnectRequirementsState(adbConnectRequirementsState);
    }
}
