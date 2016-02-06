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
package de.androidbytes.adbconnect.presentation.presenter;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import de.androidbytes.adbconnect.R;
import de.androidbytes.adbconnect.presentation.di.OperatorScope;
import de.androidbytes.adbconnect.presentation.eventbus.EventBus;
import de.androidbytes.adbconnect.presentation.utils.InteractionUtility;
import de.androidbytes.adbconnect.presentation.utils.NetworkUtility;
import de.androidbytes.adbconnect.presentation.utils.PreferenceUtility;
import de.androidbytes.adbconnect.presentation.view.SettingsViewInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.xpece.android.support.preference.MultiSelectListPreference;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


/**
 * {@link Presenter} for the MainLayoutPage {@link de.androidbytes.adbconnect.presentation.view.activity.MainScreenActivity}
 *
 * @author Christoph Hennemann
 */
@OperatorScope
@Accessors(prefix = "m")
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class SettingsPresenter implements Presenter {

    private Context mApplicationContext;
    private EventBus mEventBus;

    private List<PreferenceCategory> mHiddenCategories = new ArrayList<>();
    private List<Preference> mHiddenConnectionPreferences = new ArrayList<>();
    private List<Preference> mHiddenNotificationPreferences = new ArrayList<>();

    @Setter(AccessLevel.PUBLIC)
    private SettingsViewInterface mView;


    @Inject
    public SettingsPresenter(Context context, EventBus eventBus) {
        setApplicationContext(context);
        setEventBus(eventBus);
    }


//region Lifecycls Methods

    @Override
    public void start() {
    }

    @Override
    public void resume() {
        getEventBus().register(this);
    }

    @Override
    public void pause() {
        getEventBus().unregister(this);
    }

    @Override
    public void destroy() {
    }

    //    endregion

    public void initializeTrustedNetworks(MultiSelectListPreference selectTrustedNetworksPreference) {

        List<WifiConfiguration> configurations = NetworkUtility.getConfiguredNetworks(getApplicationContext());

        if(configurations != null) {
            CharSequence[] entries = new CharSequence[configurations.size()];
            CharSequence[] entryValues = new CharSequence[configurations.size()];

            for (int i = 0; i < configurations.size(); i++) {
                entries[i] = configurations.get(i).SSID.replaceAll("\"", "");
                entryValues[i] = configurations.get(i).SSID.replaceAll("\"", "");
            }
            selectTrustedNetworksPreference.setEntries(entries);
            selectTrustedNetworksPreference.setEntryValues(entryValues);
            selectTrustedNetworksPreference.setDefaultValue(new CharSequence[configurations.size()]);
            selectTrustedNetworksPreference.setSummary(R.string.pref_trusted_networks_summary);
            selectTrustedNetworksPreference.setEnabled(true);
        } else {
            selectTrustedNetworksPreference.setSummary(R.string.pref_trusted_networks_summary_error);
            selectTrustedNetworksPreference.setEnabled(false);
        }
    }

    public void initializeAdvancedSettings(boolean showAdvancedSettings) {
        showAdvancedSettings(showAdvancedSettings, true);
    }

    public void initializeDisplayDonationButtonSetting() {
        if(!PreferenceUtility.hasUserDonated(getApplicationContext())) {
            hideDisplayDonationButtonSetting();
        }
    }

    public void onUseSimplifiedLayoutSettingChanged() {

        setLayoutChanged(true);

    }

    private void setLayoutChanged(boolean hasLayoutChanged) {

        PreferenceUtility.hasLayoutChanged(getApplicationContext(), hasLayoutChanged);
    }

    @SuppressWarnings("deprecation")
    public void onRatePreferenceClicked() {
        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }

        try {
            ((PreferenceFragment) getView()).startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ((PreferenceFragment) getView()).startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
        }

    }

    public void onContactMePreferenceClicked() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"adbconnect@androidbytes.de"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Question about ADB Connect");
        try {
            ((PreferenceFragment) getView()).startActivity(Intent.createChooser(i, mApplicationContext.getString(R.string.pref_contact_developer_share_dialog_title)));
        } catch (android.content.ActivityNotFoundException ex) {
            InteractionUtility.showSnackbar(getView().getRoot(), mApplicationContext.getString(R.string.pref_contact_developer_error_message), Snackbar.LENGTH_LONG);
        }
    }

    public void onShowAdvancedSettingsChanged(boolean showAdvancedSettings) {

        showAdvancedSettings(showAdvancedSettings, false);

    }



    private void showAdvancedSettings(boolean showAdvancedSettings, boolean startUp) {

        if(showAdvancedSettings) {
            getView().unhideCategories(getHiddenCategories());
            unhideConnectionSettings();
            unhideNotificationSettings();
        } else {

            if(startUp) {

                resetSettingsBeforeHiding();
                setHiddenCategories(hidePreferenceCategories());
                setHiddenConnectionPreferences(hideConnectionSettings());
                setHiddenNotificationPreferences(hideNotificationSettings());

            } else {
                new AlertDialog.Builder(((PreferenceFragment) getView()).getActivity())
                            .setTitle("Reset Advanced Settings")
                            .setMessage("The Advanced Settings will be set to their defaults")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    resetSettingsBeforeHiding();
                                    setHiddenCategories(hidePreferenceCategories());
                                    setHiddenConnectionPreferences(hideConnectionSettings());
                                    setHiddenNotificationPreferences(hideNotificationSettings());
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    ((PreferenceFragment) getView()).getPreferenceManager().getSharedPreferences().edit().putBoolean(getApplicationContext().getString(R.string.pref_show_advanced_settings_key), true).commit();
                                }
                            })
                            .show();
            }
        }
    }

    private void resetSettingsBeforeHiding() {
        resetPreference(getApplicationContext().getString(R.string.pref_autostart_on_known_wifi_key), getApplicationContext().getResources().getBoolean(R.bool.pref_autostart_on_known_wifi_default_value));
        resetPreference(getApplicationContext().getString(R.string.pref_listen_on_dynamic_port_key), getApplicationContext().getResources().getInteger(R.integer.pref_listen_on_dynamic_port_default_value));
        resetPreference(getApplicationContext().getString(R.string.pref_notification_priority_key), getApplicationContext().getResources().getInteger(R.integer.pref_notification_priority_default_value));
        resetPreference(getApplicationContext().getString(R.string.pref_show_only_on_adb_over_wifi_active_key), getApplicationContext().getResources().getBoolean(R.bool.pref_show_on_adb_over_wifi_active_default_value));
        resetPreference(getApplicationContext().getString(R.string.pref_persistent_only_if_adb_over_wifi_active_key), getApplicationContext().getResources().getBoolean(R.bool.pref_persistent_only_if_adb_over_wifi_active_default_value));
    }

    private void resetPreference(String key, boolean defaultValue) {
        ((PreferenceFragment) getView()).getPreferenceManager().getSharedPreferences().edit().putBoolean(key, defaultValue).apply();
    }

    private void resetPreference(String key, int defaultValue) {
        ((PreferenceFragment) getView()).getPreferenceManager().getSharedPreferences().edit().putInt(key, defaultValue).apply();
    }


    private void unhideConnectionSettings() {
        getView().unhideSettings(getApplicationContext().getString(R.string.pref_category_connection_settings_key), getHiddenConnectionPreferences());
    }

    private List<Preference> hideConnectionSettings() {

        List<String> preferencesToHide = new ArrayList<>();
        preferencesToHide.add(getApplicationContext().getString(R.string.pref_listen_on_dynamic_port_key));

        return getView().hideSettings(getApplicationContext().getString(R.string.pref_category_connection_settings_key), preferencesToHide);
    }

    private void unhideNotificationSettings() {
        getView().unhideSettings(getApplicationContext().getString(R.string.pref_category_notification_settings_key), getHiddenNotificationPreferences());
    }

    private List<Preference> hideNotificationSettings() {

        List<String> preferencesToHide = new ArrayList<>();
        preferencesToHide.add(getApplicationContext().getString(R.string.pref_notification_priority_key));
        preferencesToHide.add(getApplicationContext().getString(R.string.pref_show_only_on_adb_over_wifi_active_key));
        preferencesToHide.add(getApplicationContext().getString(R.string.pref_persistent_only_if_adb_over_wifi_active_key));

        return getView().hideSettings(getApplicationContext().getString(R.string.pref_category_notification_settings_key), preferencesToHide);
    }

    private Preference hideDisplayDonationButtonSetting() {
        return getView().hideSetting(getApplicationContext().getString(R.string.pref_category_about_key), getApplicationContext().getString(R.string.pref_should_display_donation_button_key));
    }

    private List<PreferenceCategory> hidePreferenceCategories() {

        List<String> preferenceCategoryKeys = new ArrayList<>();
        preferenceCategoryKeys.add(getApplicationContext().getString(R.string.pref_category_service_settings_key));

        return getView().hideCategories(preferenceCategoryKeys);
    }

    public void onDisplayDonationButtonPreferenceChanged() {
        setLayoutChanged(true);
    }

//endregion

}
