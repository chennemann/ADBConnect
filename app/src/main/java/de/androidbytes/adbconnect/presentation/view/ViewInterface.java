package de.androidbytes.adbconnect.presentation.view;


import android.view.View;


/**
 * Created by Christoph on 20.09.2015.
 */
public interface ViewInterface {
    void updateViewData(ApplicationState applicationState);
    View getRoot();
}
