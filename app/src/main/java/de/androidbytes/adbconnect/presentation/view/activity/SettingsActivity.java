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


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import de.androidbytes.adbconnect.R;
import de.androidbytes.adbconnect.presentation.di.HasComponent;
import de.androidbytes.adbconnect.presentation.di.components.DaggerSettingsComponent;
import de.androidbytes.adbconnect.presentation.di.components.SettingsComponent;



public class SettingsActivity extends BaseActivity implements HasComponent<SettingsComponent> {

    @Nullable
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    
    private SettingsComponent mSettingsComponent;

    public static void start(Context context) {

        Intent launchIntent = new Intent(context, SettingsActivity.class);
        context.startActivity(launchIntent);
    }

    @Override
    protected int getActivityLayout() {

        return R.layout.activity_settings;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {

        if(mCollapsingToolbarLayout != null) {
            mCollapsingToolbarLayout.setTitle(getTitle());
        }
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSettingsComponent = DaggerSettingsComponent
                    .builder()
                    .applicationComponent(getApplicationComponent())
                    .activityModule(getActivityModule())
                    .build();
    }

    @Override
    public SettingsComponent getComponent() {

        return mSettingsComponent;
    }
}
