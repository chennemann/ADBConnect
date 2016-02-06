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


import android.content.Context;

import dagger.Component;
import de.androidbytes.adbconnect.domain.executor.PostExecutionThread;
import de.androidbytes.adbconnect.domain.executor.ThreadExecutor;
import de.androidbytes.adbconnect.presentation.AdbConnectApplication;
import de.androidbytes.adbconnect.presentation.di.ApplicationScope;
import de.androidbytes.adbconnect.presentation.di.modules.ApplicationModule;
import de.androidbytes.adbconnect.presentation.di.modules.EventBusModule;
import de.androidbytes.adbconnect.presentation.eventbus.EventBus;
import de.androidbytes.adbconnect.presentation.view.activity.BaseActivity;

/**
 * A component whose lifetime is the life of the application.
 */
@ApplicationScope // Constraints this component to one-per-application or unscoped bindings.
@Component(modules = {ApplicationModule.class, EventBusModule.class})
public interface ApplicationComponent {
    void inject(AdbConnectApplication application);
    void inject(BaseActivity baseActivity);

    //Exposed to sub-graphs.
    Context context();
    EventBus eventBus();
    ThreadExecutor threadExecutor();
    PostExecutionThread postExecutionThread();

}