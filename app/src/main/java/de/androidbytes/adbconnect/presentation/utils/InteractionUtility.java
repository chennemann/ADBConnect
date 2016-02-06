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
import android.support.annotation.IntDef;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



public class InteractionUtility {

    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ToastDuration {}

    public static void showToast(Context context, String message, @ToastDuration int duration) {

        Toast.makeText(context, message, duration).show();
    }

    @IntDef({Snackbar.LENGTH_SHORT, Snackbar.LENGTH_LONG, Snackbar.LENGTH_INDEFINITE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SnackbarDuration {}

    public static void showSnackbar(View rootView, String message, @SnackbarDuration int duration) {

        Snackbar.make(rootView, message, duration).show();
    }


}
