package org.cbase.smartahoy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.sonyericsson.extras.liveware.extension.util.notification.NotificationUtil;

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

        findViewById(R.id.clearNotifications).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClearDialog().show();
            }
        });
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

    /**
     * Create the Clear events dialog
     *
     * @return the Dialog
     */
    private Dialog createClearDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete the messages?")
                .setTitle("RLLY?")
                .setIcon(android.R.drawable.ic_input_delete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        new ClearEventsTask().execute();
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        return builder.create();
    }

    /**
     * Clear all messaging events
     */
    private class ClearEventsTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int nbrDeleted = 0;
            nbrDeleted = NotificationUtil.deleteAllEvents(MainActivity.this);
            return nbrDeleted;
        }

        @Override
        protected void onPostExecute(Integer id) {
            if (id != NotificationUtil.INVALID_ID) {
                Toast.makeText(MainActivity.this,"clear done",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "problem clearing",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

}
