package de.androidbytes.adbconnect.domain.interactor.result;


/**
 * Created by Christoph on 26.09.2015.
 */
public enum RootAvailabilityEnum {

    ROOT_AVAILABILITY_AVAILABLE("ROOT_AVAILABILITY_AVAILABLE"),
    ROOT_AVAILABILITY_NOT_AVAILABLE("ROOT_AVAILABILITY_NOT_AVAILABLE");

    private final String fAvailabilityValue;

    RootAvailabilityEnum(final String stateValue) {
        fAvailabilityValue = stateValue;
    }

    public String toString() {
        return fAvailabilityValue;
    }

}
