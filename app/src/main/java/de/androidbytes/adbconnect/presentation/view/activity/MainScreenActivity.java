package de.androidbytes.adbconnect.presentation.view.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.Bind;
import de.androidbytes.adbconnect.R;
import de.androidbytes.adbconnect.presentation.di.HasComponent;
import de.androidbytes.adbconnect.presentation.di.components.DaggerMainScreenComponent;
import de.androidbytes.adbconnect.presentation.di.components.MainScreenComponent;
import de.androidbytes.adbconnect.presentation.utils.PreferenceUtility;
import de.androidbytes.adbconnect.presentation.view.fragment.InAppBillingFragment;
import trikita.log.Log;


public class MainScreenActivity extends BaseActivity implements HasComponent<MainScreenComponent> {

    @Nullable
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private MainScreenComponent mMainScreenComponent;

    public static void start(Context context) {

        Intent launchIntent = new Intent(context, MainScreenActivity.class);
        context.startActivity(launchIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {

        if (PreferenceUtility.hasLayoutChanged(getApplicationContext())) {

            Log.i("Restart after Layout Change");
            PreferenceUtility.hasLayoutChanged(getApplicationContext(), false);

            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize(savedInstanceState);
    }

    @Override
    protected int getActivityLayout() {

        return R.layout.activity_main;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {

        if(mCollapsingToolbarLayout != null) {
            mCollapsingToolbarLayout.setTitle(getTitle());
        }
        setSupportActionBar(mToolbar);

        mMainScreenComponent = DaggerMainScreenComponent.builder()
                    .applicationComponent(getApplicationComponent())
                    .activityModule(getActivityModule())
                    .build();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_settings:
                SettingsActivity.start(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_main_screen);
        boolean handled = false;

        if(fragment instanceof InAppBillingFragment) {
            handled = ((InAppBillingFragment) fragment).handleInAppBillingResult(requestCode, resultCode, data);
        }

        if(!handled) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public MainScreenComponent getComponent() {
        return mMainScreenComponent;
    }
}
