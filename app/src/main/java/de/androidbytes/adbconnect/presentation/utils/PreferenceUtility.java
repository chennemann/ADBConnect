package de.androidbytes.adbconnect.presentation.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.BoolRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.StringRes;

import java.util.Set;

import de.androidbytes.adbconnect.R;


/**
 * Created by Christoph on 01.10.2015.
 */
public class PreferenceUtility {

    private static SharedPreferences getSharedPreferences(Context context) {
        context = context.getApplicationContext();
        return context.getSharedPreferences(context.getString(R.string.application_preferences), Context.MODE_PRIVATE);
    }

    private static boolean getBoolean(Context context, @StringRes int preferenceKey, @BoolRes int preferenceDefaultValue) {
        return getSharedPreferences(context).getBoolean(context.getString(preferenceKey), context.getResources().getBoolean(preferenceDefaultValue));
    }

    private static void setBoolean(Context context, @StringRes int preferenceKey, boolean newValue) {
        getSharedPreferences(context).edit().putBoolean(context.getString(preferenceKey), newValue).apply();
    }

    private static int getInteger(Context context, @StringRes int preferenceKey, @IntegerRes int preferenceDefaultValue) {
        return getSharedPreferences(context).getInt(context.getString(preferenceKey), context.getResources().getInteger(preferenceDefaultValue));
    }

    private static void setInteger(Context context, @StringRes int preferenceKey, int newValue) {
        getSharedPreferences(context).edit().putInt(context.getString(preferenceKey), newValue).apply();
    }

    private static Set<String> getStringSet(Context context, @StringRes int preferenceKey) {
        return getSharedPreferences(context).getStringSet(context.getString(preferenceKey), null);
    }




    public static boolean canUseRootPermissions(Context context) {
        return getBoolean(context, R.string.pref_use_root_key, R.bool.pref_use_root_key_default_value);
    }


    public static void canUseRootPermissions(Context context, boolean canUseRootPermissions) {
        setBoolean(context, R.string.pref_use_root_key, canUseRootPermissions);
    }

    public static boolean useSimplifiedLayout(Context context) {
        return getBoolean(context, R.string.pref_use_simplified_layout_key, R.bool.pref_use_simplified_layout_default_value);
    }

    public static boolean shouldShowNotification(Context context) {

        return getBoolean(context, R.string.pref_show_notification_key, R.bool.pref_show_notification_default_value);
    }

    public static int listenOnDynamicPort(Context context) {

        return getInteger(context, R.string.pref_listen_on_dynamic_port_key, R.integer.pref_listen_on_dynamic_port_default_value);
    }

    public static boolean showPersistentNotification(Context context) {

        return getBoolean(context, R.string.pref_persistent_notification_key, R.bool.pref_persistent_notification_default_value);
    }

    public static Set<String> getTrustedNetworks(Context context) {

        return getStringSet(context, R.string.pref_trusted_networks_key);
    }

    public static int getNotificationPriority(Context context) {

        return getInteger(context, R.string.pref_notification_priority_key, R.integer.pref_notification_priority_default_value);
    }

    public static boolean showNotificationOnlyOnActiveAdbOverWifi(Context context) {

        return getBoolean(context, R.string.pref_show_only_on_adb_over_wifi_active_key, R.bool.pref_show_on_adb_over_wifi_active_default_value);
    }

    public static boolean shouldAutostartOnTrustedNetworks(Context context) {

        return getBoolean(context, R.string.pref_autostart_on_known_wifi_key, R.bool.pref_autostart_on_known_wifi_default_value);
    }

    public static void hasLayoutChanged(Context context, boolean hasChanged) {
        setBoolean(context, R.string.pref_layout_changed_key, hasChanged);

    }

    public static boolean hasLayoutChanged(Context context) {
        return getBoolean(context, R.string.pref_layout_changed_key, R.bool.pref_layout_changed_default_value);
    }

    public static void addToUsageCount(Context context) {
        int currentCount = getInteger(context, R.string.pref_usage_count_key, R.integer.pref_usage_count_default_value);
        setInteger(context, R.string.pref_usage_count_key, ++currentCount);
    }

    public static boolean persistNotificationOnlyOnActiveAdbOverWifi(Context context) {

        return getBoolean(context, R.string.pref_persistent_only_if_adb_over_wifi_active_key, R.bool.pref_persistent_only_if_adb_over_wifi_active_default_value);
    }

    public static void hasUserDonated(Context context, boolean hasUserDonated) {
        setBoolean(context, R.string.pref_user_has_donated_key, hasUserDonated);
    }

    public static boolean hasUserDonated(Context context) {
        return getBoolean(context, R.string.pref_user_has_donated_key, R.bool.pref_user_has_donated_default_value);
    }

    public static boolean shouldDisplayDonationButton(Context context) {
        return getBoolean(context, R.string.pref_should_display_donation_button_key, R.bool.pref_should_display_donation_button_default_value);
    }
}
