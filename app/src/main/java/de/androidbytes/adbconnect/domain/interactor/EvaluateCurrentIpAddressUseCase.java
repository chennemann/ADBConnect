package de.androidbytes.adbconnect.domain.interactor;


import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

import javax.inject.Inject;

import de.androidbytes.adbconnect.domain.ShellInterface;
import de.androidbytes.adbconnect.domain.executor.PostExecutionThread;
import de.androidbytes.adbconnect.domain.executor.ThreadExecutor;
import de.androidbytes.adbconnect.domain.interactor.result.ConnectionInformation;
import rx.Observable;
import rx.Subscriber;
import trikita.log.Log;


/**
 * Created by Christoph on 28.09.2015.
 */
public class EvaluateCurrentIpAddressUseCase extends UseCase {

    @Inject
    protected EvaluateCurrentIpAddressUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {

        super(threadExecutor, postExecutionThread);
    }

    @Override
    protected Observable buildUseCaseObservable() {

        return Observable.create(new Observable.OnSubscribe<ConnectionInformation>() {

            @Override
            public void call(Subscriber<? super ConnectionInformation> subscriber) {

                try {
                    for (final Enumeration<NetworkInterface> enumerationNetworkInterface = NetworkInterface.getNetworkInterfaces(); enumerationNetworkInterface.hasMoreElements(); ) {
                        final NetworkInterface networkInterface = enumerationNetworkInterface.nextElement();
                        for (Enumeration<InetAddress> enumerationInetAddress = networkInterface.getInetAddresses(); enumerationInetAddress.hasMoreElements(); ) {

                            final List<String> shellExecutionOutput;
                            shellExecutionOutput = ShellInterface.executeShellCommands(new String[]{"getprop service.adb.tcp.port"});


                            final InetAddress inetAddress = enumerationInetAddress.nextElement();
                            String ipAddress = inetAddress.getHostAddress();

                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                Log.i("Current IP Address: " + ipAddress);

                                final String adbPort;
                                if (null != shellExecutionOutput && shellExecutionOutput.size() > 0 && !shellExecutionOutput.get(0).equalsIgnoreCase("-1") && !shellExecutionOutput.get(0).equalsIgnoreCase("0")) {
                                    adbPort = shellExecutionOutput.get(0);
                                    Log.i("Current ADB TCP Port: " + adbPort);
                                } else {
                                    adbPort = "";
                                }

                                subscriber.onNext(new ConnectionInformation(ipAddress, adbPort));
                            }
                        }
                    }
                    subscriber.onCompleted();
                } catch (final Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

}
