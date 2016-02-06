/**
 * Copyright (C) 2016 Christoph Hennemann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.androidbytes.adbconnect.presentation.di.components;


import dagger.Component;
import de.androidbytes.adbconnect.presentation.di.OperatorScope;
import de.androidbytes.adbconnect.presentation.di.modules.AdbModule;
import de.androidbytes.adbconnect.presentation.di.modules.RequirementModule;
import de.androidbytes.adbconnect.presentation.di.modules.RootModule;
import de.androidbytes.adbconnect.presentation.services.WirelessAdbManagingService;



@OperatorScope
@Component(
            dependencies = ApplicationComponent.class,
            modules = {RequirementModule.class, AdbModule.class, RootModule.class}
)
public interface AdbManagingServiceComponent {
    void inject(WirelessAdbManagingService wirelessAdbManagingService);
}
