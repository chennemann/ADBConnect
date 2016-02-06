package de.androidbytes.adbconnect.presentation.view;


import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.view.View;

import java.util.List;


/**
 * Created by Christoph on 04.10.2015.
 */
public interface SettingsViewInterface {

    List<Preference> hideSettings(String categoryKey, List<String> preferenceKeys);
    void unhideSettings(String categoryKey, List<Preference> preferences);
    List<PreferenceCategory> hideCategories(List<String> categoryKeys);
    void unhideCategories(List<PreferenceCategory> categories);
    Preference hideSetting(String categoryKey, String preferenceKey);
    View getRoot();
}
