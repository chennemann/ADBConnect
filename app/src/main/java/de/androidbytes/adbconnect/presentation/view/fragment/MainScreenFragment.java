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
package de.androidbytes.adbconnect.presentation.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Switch;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import de.androidbytes.adbconnect.R;
import de.androidbytes.adbconnect.presentation.di.components.MainScreenComponent;
import de.androidbytes.adbconnect.presentation.presenter.MainScreenPresenter;
import de.androidbytes.adbconnect.presentation.utils.PreferenceUtility;
import de.androidbytes.adbconnect.presentation.view.ApplicationState;
import de.androidbytes.adbconnect.presentation.view.ViewInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import trikita.log.Log;

import javax.inject.Inject;



@Accessors(prefix = "m")
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class MainScreenFragment extends BaseFragment implements ViewInterface {

    @Inject
    @Getter(AccessLevel.PUBLIC)
    MainScreenPresenter mPresenter;

    @Nullable
    @Bind(R.id.textview_applicationStateInformation)
    TextView mApplicationStateInformationTextView;

    @Nullable
    @Bind(R.id.textview_executionSourceInformation)
    TextView mExecutionSourceInformationTextView;

    @Bind(R.id.textview_executionCommandsInformation)
    TextView mExecutionCommandsInformationTextView;

    @Bind(R.id.switch_wirelessAdb)
    Switch mWirelessAdbSwitch;

    private boolean mCheckedChangeActionPrevented;


//    region Lifecycle Methods

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container);


        ViewStub contentContainer = (ViewStub) rootView.findViewById(R.id.content_container);

        if(PreferenceUtility.useSimplifiedLayout(getContext())) {
            contentContainer.setLayoutResource(R.layout.content_main_simple);
        }  else {
            contentContainer.setLayoutResource(R.layout.content_main);
        }
        contentContainer.inflate();

        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().start();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPresenter().pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPresenter().destroy();
    }

//    endregion


    private void initialize() {
        this.getComponent(MainScreenComponent.class).inject(this);
        getPresenter().setView(this);
    }

    @OnCheckedChanged(R.id.switch_wirelessAdb)
    public void onWirelessAdbSwitchClicked(boolean isChecked) {
        if(!isCheckedChangeActionPrevented()) {
            Log.i("Wireless Adb Switch clicked by User");
            getPresenter().wirelessAdbSwitchClicked(isChecked);
        } else {
            Log.i("Wireless Adb Switch State changed programmatically. Therefore no action was performed");
            // empty
        }
    }


    @Override
    public void updateViewData(ApplicationState applicationState) {

        if(getApplicationStateInformationTextView() != null) {
            getApplicationStateInformationTextView().setText(applicationState.getApplicationStateInformation());
        }

        if(getExecutionSourceInformationTextView() != null) {
            getExecutionSourceInformationTextView().setText(applicationState.getExecutionSourceInformation());
        }

        getExecutionCommandsInformationTextView().setText(applicationState.getExecutionCommandsInformation());
        setWirelessAdbSwitchEnabled(applicationState.isAdbOverWifiSwitchEnabled());
        setWirelessAdbSwitchChecked(applicationState.isAdbOverWifiSwitchChecked());
    }

    @Override
    public View getRoot() {

        View view = null;

        if(getView() != null) {
            view = getView().getRootView();
        }

        return view;
    }

    public void setWirelessAdbSwitchEnabled(boolean wirelessAdbSwitchEnabled) {
        getWirelessAdbSwitch().setEnabled(wirelessAdbSwitchEnabled);
    }


    public void setWirelessAdbSwitchChecked(boolean wirelessAdbSwitchChecked) {

        setCheckedChangeActionPrevented(true);
        getWirelessAdbSwitch().setChecked(wirelessAdbSwitchChecked);
        setCheckedChangeActionPrevented(false);
    }
}
