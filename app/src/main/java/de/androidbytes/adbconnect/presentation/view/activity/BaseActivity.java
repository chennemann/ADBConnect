package de.androidbytes.adbconnect.presentation.view.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import de.androidbytes.adbconnect.presentation.AdbConnectApplication;
import de.androidbytes.adbconnect.presentation.di.components.ApplicationComponent;
import de.androidbytes.adbconnect.presentation.di.modules.ActivityModule;


/**
 * Created by Christoph on 17.09.2015.
 */
public abstract class BaseActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getApplicationComponent().inject(this);

        setContentView(getActivityLayout());

        ButterKnife.bind(this);

        initialize(savedInstanceState);
    }

    protected abstract int getActivityLayout();

    protected abstract void initialize(Bundle savedInstanceState);

    /**
     * Get the Main Application component for dependency injection.
     *
     * @return {@link de.androidbytes.adbconnect.presentation.di.components.ApplicationComponent}
     */
    protected ApplicationComponent getApplicationComponent() {
        return ((AdbConnectApplication) getApplication()).getApplicationComponent();
    }

    /**
     * Get an Activity module for dependency injection.
     *
     * @return {@link de.androidbytes.adbconnect.presentation.di.modules.ActivityModule}
     */
    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

}
