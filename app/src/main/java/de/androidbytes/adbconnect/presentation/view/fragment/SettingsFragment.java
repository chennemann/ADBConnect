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


import android.content.Context;
import android.os.Bundle;
import android.preference.*;
import android.view.View;
import de.androidbytes.adbconnect.R;
import de.androidbytes.adbconnect.presentation.di.HasComponent;
import de.androidbytes.adbconnect.presentation.di.components.SettingsComponent;
import de.androidbytes.adbconnect.presentation.presenter.SettingsPresenter;
import de.androidbytes.adbconnect.presentation.view.SettingsViewInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.xpece.android.support.preference.MultiSelectListPreference;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


@Accessors(prefix = "m")
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class SettingsFragment extends PreferenceFragment implements SettingsViewInterface {

    @Inject
    SettingsPresenter mPresenter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName(getString(R.string.application_preferences));
        preferenceManager.setSharedPreferencesMode(Context.MODE_PRIVATE);
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

    private void initialize() {
        this.getComponent(SettingsComponent.class).inject(this);
        getPresenter().setView(this);

        addPreferencesFromResource(R.xml.preferences);

        MultiSelectListPreference selectTrustedNetworksPreference = (MultiSelectListPreference) findPreference(getString(R.string.pref_trusted_networks_key));
        getPresenter().initializeTrustedNetworks(selectTrustedNetworksPreference);

        CheckBoxPreference useSimplifiedLayoutPreference = (CheckBoxPreference) findPreference(getString(R.string.pref_use_simplified_layout_key));
        useSimplifiedLayoutPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                getPresenter().onUseSimplifiedLayoutSettingChanged();
                return true;
            }
        });

        Preference ratePreference = findPreference(getString(R.string.pref_rate_application_key));
        ratePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                getPresenter().onRatePreferenceClicked();
                return true;
            }
        });

        Preference contactPreference = findPreference(getString(R.string.pref_contact_developer_key));
        contactPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                getPresenter().onContactMePreferenceClicked();
                return true;
            }
        });

        CheckBoxPreference showAdvancedSettingsPreference = (CheckBoxPreference) findPreference(getString(R.string.pref_show_advanced_settings_key));
        getPresenter().initializeAdvancedSettings(showAdvancedSettingsPreference.isChecked());
        showAdvancedSettingsPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                getPresenter().onShowAdvancedSettingsChanged((boolean) newValue);
                return true;
            }
        });

        CheckBoxPreference displayDonationButtonPreference = (CheckBoxPreference) findPreference(getString(R.string.pref_should_display_donation_button_key));
        getPresenter().initializeDisplayDonationButtonSetting();
        displayDonationButtonPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                getPresenter().onDisplayDonationButtonPreferenceChanged();
                return true;
            }
        });
    }

    @Override
    public Preference hideSetting(String categoryKey, String preferenceKey) {

        PreferenceCategory category = (PreferenceCategory) findPreference(categoryKey);
        Preference preferenceToHide = findPreference(preferenceKey);
        category.removePreference(preferenceToHide);

        return preferenceToHide;
    }

    @Override
    public List<Preference> hideSettings(String categoryKey, List<String> preferenceKeys) {

        List<Preference> hiddenPreferences = new ArrayList<>();

        for (String key : preferenceKeys) {
            hiddenPreferences.add(hideSetting(categoryKey, key));
        }

        return hiddenPreferences;
    }

    @Override
    public void unhideSettings(String categoryKey, List<Preference> preferences) {

        PreferenceCategory category = (PreferenceCategory) findPreference(categoryKey);

        for (Preference preference : preferences) {
            category.addPreference(preference);
        }

    }

    @Override
    public List<PreferenceCategory> hideCategories(List<String> categoryKeys) {

        List<PreferenceCategory> hiddenCategories = new ArrayList<>();

        for (String key : categoryKeys) {
            PreferenceCategory category = (PreferenceCategory) findPreference(key);
            hiddenCategories.add(category);
            getPreferenceScreen().removePreference(category);
        }

        return hiddenCategories;
    }

    @Override
    public void unhideCategories(List<PreferenceCategory> categories) {

        for (PreferenceCategory category : categories) {
            getPreferenceScreen().addPreference(category);
        }
    }

    @Override
    public View getRoot() {

        View view = null;

        if(getView() != null) {
            view = getView().getRootView();
        }

        return view;
    }


    /**
     * Gets a component for dependency injection by its type.
     */
    @SuppressWarnings("unchecked")
    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>)getActivity()).getComponent());
    }
}
