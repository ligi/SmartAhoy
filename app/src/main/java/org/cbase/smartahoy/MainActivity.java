package org.cbase.smartahoy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);

        CheckBox activeCheckBox = (CheckBox) findViewById(R.id.activeCheckBox);

        activeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor edit = getPreferences().edit();
                edit.putBoolean("active", isChecked);
                edit.commit();

                if (isChecked) {
                    startSampleExtensionService();
                } else {
                    stopSampleExtensionService();
                }

            }
        });

        activeCheckBox.setChecked(getPreferences().getBoolean("active", false));

    }

    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }


    /**
     * Activate event generation
     */
    private void startSampleExtensionService() {
        Intent serviceIntent = new Intent(this, SmartAhoyExtensionService.class);
        serviceIntent.setAction(SmartAhoyExtensionService.INTENT_ACTION_START);
        startService(serviceIntent);
    }

    /**
     * Cancel event generation
     */
    private void stopSampleExtensionService() {
        Intent serviceIntent = new Intent(this, SmartAhoyExtensionService.class);
        serviceIntent.setAction(SmartAhoyExtensionService.INTENT_ACTION_STOP);
        startService(serviceIntent);
    }


}
