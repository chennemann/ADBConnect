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
package de.androidbytes.adbconnect.presentation.view.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import de.androidbytes.adbconnect.presentation.AdbConnectApplication;
import de.androidbytes.adbconnect.presentation.di.components.ApplicationComponent;
import de.androidbytes.adbconnect.presentation.di.modules.ActivityModule;



public abstract class BaseActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getApplicationComponent().inject(this);

        setContentView(getActivityLayout());

        ButterKnife.bind(this);

        initialize(savedInstanceState);
    }

    protected abstract int getActivityLayout();

    protected abstract void initialize(Bundle savedInstanceState);

    /**
     * Get the Main Application component for dependency injection.
     *
     * @return {@link de.androidbytes.adbconnect.presentation.di.components.ApplicationComponent}
     */
    protected ApplicationComponent getApplicationComponent() {
        return ((AdbConnectApplication) getApplication()).getApplicationComponent();
    }

    /**
     * Get an Activity module for dependency injection.
     *
     * @return {@link de.androidbytes.adbconnect.presentation.di.modules.ActivityModule}
     */
    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

}
