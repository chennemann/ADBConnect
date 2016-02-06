package de.androidbytes.adbconnect.domain.interactor.result;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * Created by Christoph on 29.09.2015.
 */
@Accessors(prefix = "m")
@Getter
@Setter(AccessLevel.PRIVATE)
public class ConnectionInformation {

    private String mIpAddress;
    private String mAdbPort;

    public ConnectionInformation() {
        this("","");
    }

    public ConnectionInformation(String ipAddress, String adbPort) {
        setIpAddress(ipAddress);
        setAdbPort(adbPort);
    }

    public String toString() {
        return getIpAddress() + ":" + getAdbPort();
    }

}
