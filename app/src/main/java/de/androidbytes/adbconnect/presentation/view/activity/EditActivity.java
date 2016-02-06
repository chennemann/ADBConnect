package de.androidbytes.adbconnect.presentation.view.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Switch;

import butterknife.Bind;
import de.androidbytes.adbconnect.R;
import de.androidbytes.adbconnect.presentation.utils.TaskerPluginUtility;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import trikita.log.Log;


/**
 * Created by Christoph on 06.10.2015.
 */
@Accessors(prefix = "m")
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class EditActivity extends BaseActivity {

    private boolean mCancelled;

    @Bind(R.id.switch_wirelessAdb)
    Switch mWirelessAdbSwitch;


    @Override
    protected int getActivityLayout() {
        return R.layout.activity_edit;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final int id = item.getItemId();

        if (android.R.id.home == id)
        {
            finish();
            return true;
        }
        else if (R.id.action_delete == id)
        {
            setCancelled(true);
            finish();
            return true;
        }
        else if (R.id.action_save == id)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {

        if(getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TaskerPluginUtility.scrub(getIntent());

        final Bundle localBundle = getIntent().getBundleExtra(TaskerPluginUtility.BUNDLE);
        TaskerPluginUtility.scrub(localBundle);

        if(localBundle != null){
            final boolean shouldActivate = localBundle.getBoolean(TaskerPluginUtility.BUNDLE_EXTRA_BOOLEAN_MESSAGE);
            Log.i("Set Switch checked" + shouldActivate);
            mWirelessAdbSwitch.setChecked(shouldActivate);
        }

    }

    @Override
    public void finish() {

        if(!isCancelled()) {

            final boolean shouldActivateAdbOverWifi = getWirelessAdbSwitch().isChecked();
            Log.i("Should Activate: " + shouldActivateAdbOverWifi);
            final Intent resultIntent = new Intent();
            final Bundle resultBundle = TaskerPluginUtility.generateBundle(getApplicationContext(), shouldActivateAdbOverWifi);
            resultIntent.putExtra(TaskerPluginUtility.BUNDLE, resultBundle);

            String state = shouldActivateAdbOverWifi ? "ON" : "OFF";

            final String blurb = String.format("ADB over WIFI will be turned %s", state);
            resultIntent.putExtra(TaskerPluginUtility.BUNDLE_EXTRA_BLURB, blurb);

            setResult(RESULT_OK, resultIntent);

        }

        super.finish();
    }
}
