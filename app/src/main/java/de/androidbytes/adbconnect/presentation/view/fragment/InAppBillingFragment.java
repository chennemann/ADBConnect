package de.androidbytes.adbconnect.presentation.view.fragment;


import android.content.Intent;


/**
 * Created by Christoph on 09.10.2015.
 */
public interface InAppBillingFragment {

    boolean handleInAppBillingResult(int requestCode, int resultCode, Intent data);
}
