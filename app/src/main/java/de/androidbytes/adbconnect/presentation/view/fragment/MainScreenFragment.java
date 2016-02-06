package de.androidbytes.adbconnect.presentation.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Switch;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
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


/**
 * Created by Christoph on 19.09.2015.
 */
@Accessors(prefix = "m")
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class MainScreenFragment extends BaseFragment implements ViewInterface, InAppBillingFragment {

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

    @Bind(R.id.fab_actionButton)
    @Nullable
    FloatingActionButton mActionButton;

    private boolean mCheckedChangeActionPrevented;


//    region Lifecycle Methods

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container);


        ViewStub contentContainer = (ViewStub) rootView.findViewById(R.id.content_container);

        if(PreferenceUtility.useSimplifiedLayout(getContext())) {
            contentContainer.setLayoutResource(R.layout.content_main_simple);
//            rootView = inflater.inflate(R.layout.fragment_main_simple, container);
        }  else {
            contentContainer.setLayoutResource(R.layout.content_main);
//            rootView = inflater.inflate(R.layout.fragment_main, container);
        }
        contentContainer.inflate();


        ViewStub actionButtonContainer = (ViewStub) rootView.findViewById(R.id.action_button_container);

        if(PreferenceUtility.shouldDisplayDonationButton(getContext())) {
            actionButtonContainer.setLayoutResource(R.layout.floating_action_button);
            actionButtonContainer.inflate();
        } else {
//            actionButtonContainer.setLayoutResource(R.layout.floating_action_button);
//            actionButtonContainer.inflate();
            actionButtonContainer.setVisibility(View.GONE);
        }

        ButterKnife.bind(this, rootView);

        if(getActionButton() != null) {
            getActionButton().setImageResource(R.drawable.ic_donation_white_48dp);
        }

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

    @Nullable
    @OnClick(R.id.fab_actionButton)
    public void onReloadButtonClicked(View view) {
        getPresenter().actionButtonClicked(view);
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

    @Override
    public boolean handleInAppBillingResult(int requestCode, int resultCode, Intent data) {
        return getPresenter().isInAppBillingResult(requestCode, resultCode, data);
    }
}
