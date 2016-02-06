/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
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
package de.androidbytes.adbconnect.presentation.di.modules;


import android.content.Context;

import dagger.Module;
import dagger.Provides;
import de.androidbytes.adbconnect.domain.executor.PostExecutionThread;
import de.androidbytes.adbconnect.domain.executor.TaskExecutor;
import de.androidbytes.adbconnect.domain.executor.ThreadExecutor;
import de.androidbytes.adbconnect.presentation.AdbConnectApplication;
import de.androidbytes.adbconnect.presentation.di.ApplicationScope;
import de.androidbytes.adbconnect.presentation.thread.UIThread;


/**
 * Dagger module that provides objects which will live during the fApplication lifecycle.
 */
@Module
public class ApplicationModule {

    private final AdbConnectApplication fApplication;

    static final String APPLICATION_PREFERENCES = "adbConnectPreferences";

    public ApplicationModule(AdbConnectApplication application) {
        fApplication = application;
    }

    @Provides
    @ApplicationScope
    Context provideApplicationContext() {
        return fApplication.getApplicationContext();
    }

    @Provides
    @ApplicationScope
    ThreadExecutor provideThreadExecutor(TaskExecutor taskExecutor) {
        return taskExecutor;
    }

    @Provides
    @ApplicationScope
    PostExecutionThread providePostExecutionThread(UIThread uiThread) {
        return uiThread;
    }
}