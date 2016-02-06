package de.androidbytes.adbconnect.presentation.di.components;


import dagger.Component;
import de.androidbytes.adbconnect.presentation.di.OperatorScope;
import de.androidbytes.adbconnect.presentation.di.modules.AdbModule;
import de.androidbytes.adbconnect.presentation.di.modules.RequirementModule;
import de.androidbytes.adbconnect.presentation.di.modules.RootModule;
import de.androidbytes.adbconnect.presentation.services.WirelessAdbManagingService;


/**
 * Created by Christoph on 26.09.2015.
 */
@OperatorScope
@Component(
            dependencies = ApplicationComponent.class,
            modules = {RequirementModule.class, AdbModule.class, RootModule.class}
)
public interface AdbManagingServiceComponent {
    void inject(WirelessAdbManagingService wirelessAdbManagingService);
}
