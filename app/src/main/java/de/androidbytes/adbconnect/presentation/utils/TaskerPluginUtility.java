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
package de.androidbytes.adbconnect.presentation.utils;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import trikita.log.Log;


public class TaskerPluginUtility {

    public static final String BUNDLE = "com.twofortyfouram.locale.intent.extra.BUNDLE";
    public static final String BUNDLE_EXTRA_BLURB = "com.twofortyfouram.locale.intent.extra.BLURB";
    public static final String BUNDLE_EXTRA_BOOLEAN_MESSAGE = "de.androidbytes.adbconnect.extra.BOOLEAN_MESSAGE";
    public static final String BUNDLE_EXTRA_INT_VERSION_CODE = "de.androidbytes.adbconnect.extra.INT_VERSION_CODE";
    public static final String ACTION_FIRE_SETTING = "com.twofortyfouram.locale.intent.action.FIRE_SETTING";


    public static boolean isBundleValid(final Bundle bundle) {

        if (null == bundle) {
            return false;
        }

        if (!bundle.containsKey(BUNDLE_EXTRA_BOOLEAN_MESSAGE)) {
            Log.e(String.format("bundle must contain extra %s", BUNDLE_EXTRA_BOOLEAN_MESSAGE)); //$NON-NLS-1$
            return false;
        }
        if (!bundle.containsKey(BUNDLE_EXTRA_INT_VERSION_CODE)) {
            Log.e(String.format("bundle must contain extra %s", BUNDLE_EXTRA_INT_VERSION_CODE)); //$NON-NLS-1$
            return false;
        }

        if (2 != bundle.keySet().size()) {
            Log.e(String.format("bundle must contain 2 keys, but currently contains %d keys: %s", bundle.keySet().size(), bundle.keySet())); //$NON-NLS-1$
            return false;
        }

        if (bundle.getInt(BUNDLE_EXTRA_INT_VERSION_CODE, 0) != bundle.getInt(BUNDLE_EXTRA_INT_VERSION_CODE, 1)) {
            Log.e(String.format("bundle extra %s appears to be the wrong type.  It must be an int", BUNDLE_EXTRA_INT_VERSION_CODE)); //$NON-NLS-1$
            return false;
        }

        return true;
    }

    /**
     * @param context Application context.
     * @param message The toast message to be displayed by the plug-in. Cannot be null.
     * @return A plug-in bundle.
     */
    public static Bundle generateBundle(final Context context, final boolean message) {

        final Bundle result = new Bundle();
        result.putInt(BUNDLE_EXTRA_INT_VERSION_CODE, getVersionCode(context));
        result.putBoolean(BUNDLE_EXTRA_BOOLEAN_MESSAGE, message);

        return result;
    }


    public static int getVersionCode(final Context context) {

        if (null == context) {
            throw new IllegalArgumentException("context cannot be null"); //$NON-NLS-1$
        }

        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (final UnsupportedOperationException e) {
            /*
             * This exception is thrown by test contexts
             */

            return 1;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Scrubs Intents for private serializable subclasses in the Intent extras. If the Intent's extras contain
     * a private serializable subclass, the Bundle is cleared. The Bundle will not be set to null. If the
     * Bundle is null, has no extras, or the extras do not contain a private serializable subclass, the Bundle
     * is not mutated.
     *
     * @param intent {@code Intent} to scrub. This parameter may be mutated if scrubbing is necessary. This
     *            parameter may be null.
     * @return true if the Intent was scrubbed, false if the Intent was not modified.
     */
    public static boolean scrub(final Intent intent) {
        return null != intent && scrub(intent.getExtras());

    }

    /**
     * Scrubs Bundles for private serializable subclasses in the extras. If the Bundle's extras contain a
     * private serializable subclass, the Bundle is cleared. If the Bundle is null, has no extras, or the
     * extras do not contain a private serializable subclass, the Bundle is not mutated.
     *
     * @param bundle {@code Bundle} to scrub. This parameter may be mutated if scrubbing is necessary. This
     *            parameter may be null.
     * @return true if the Bundle was scrubbed, false if the Bundle was not modified.
     */
    public static boolean scrub(final Bundle bundle)
    {
        if (null == bundle)
        {
            return false;
        }

        /*
         * Note: This is a hack to work around a private serializable classloader attack
         */
        try
        {
            // if a private serializable exists, this will throw an exception
            bundle.containsKey(null);
        }
        catch (final Exception e)
        {
            bundle.clear();
            return true;
        }

        return false;
    }

}
