package de.androidbytes.adbconnect.presentation.observer.result;


import de.androidbytes.adbconnect.domain.interactor.result.RootAvailabilityEnum;
import lombok.Getter;
import lombok.experimental.Accessors;


/**
 * Created by Christoph on 28.09.2015.
 */
@Accessors(prefix = "m")
public class CheckRootAvailabilityObserverResult implements ObserverResult {

    @Getter
    RootAvailabilityEnum mRootAvailability;

    public CheckRootAvailabilityObserverResult(RootAvailabilityEnum rootAvailability) {
        mRootAvailability = rootAvailability;
    }
}
