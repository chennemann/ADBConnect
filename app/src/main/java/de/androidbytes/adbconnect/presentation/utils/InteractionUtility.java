package de.androidbytes.adbconnect.presentation.utils;


import android.content.Context;
import android.support.annotation.IntDef;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Created by Christoph on 27.09.2015.
 */
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
