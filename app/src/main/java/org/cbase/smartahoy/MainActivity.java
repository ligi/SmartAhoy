package org.cbase.smartahoy;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.zip.CheckedInputStream;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);

        CheckBox activeCheckBox=(CheckBox)findViewById(R.id.activeCheckBox);

        activeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor edit = getPreferences().edit();
                edit.putBoolean("active",isChecked);
            }
        });

        activeCheckBox.setChecked(getPreferences().getBoolean("active", false));

    }

    public SharedPreferences getPreferences() {
        return  PreferenceManager.getDefaultSharedPreferences(this);
    }

}
