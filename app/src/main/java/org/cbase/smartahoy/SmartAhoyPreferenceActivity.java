package org.cbase.smartahoy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.sonyericsson.extras.liveware.extension.util.notification.NotificationUtil;

public class SmartAhoyPreferenceActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPreferenceScreen(createPreferenceHierarchy());
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
            nbrDeleted = NotificationUtil.deleteAllEvents(SmartAhoyPreferenceActivity.this);
            return nbrDeleted;
        }

        @Override
        protected void onPostExecute(Integer id) {
            if (id != NotificationUtil.INVALID_ID) {
                Toast.makeText(SmartAhoyPreferenceActivity.this, "clear done",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SmartAhoyPreferenceActivity.this, "problem clearing",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    private PreferenceScreen createPreferenceHierarchy() {
        @SuppressWarnings("deprecation")
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

        CheckBoxPreference isActiveCheckBox = new CheckBoxPreference(this);

        isActiveCheckBox.setKey("active");
        isActiveCheckBox.setTitle("active");
        isActiveCheckBox.setSummary("should we show Ahoy notifications!");
        isActiveCheckBox.setDefaultValue(false);

        Preference about = new Preference(this);
        about.setTitle("about");
        about.setSummary("WTF is this all about?");
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent intent = new Intent(SmartAhoyPreferenceActivity.this, AboutDialog.class);
                SmartAhoyPreferenceActivity.this.startActivity(intent);

                return false;
            }
        });

        Preference clearNotificationsPreference = new Preference(this);
        clearNotificationsPreference.setTitle("clear");
        clearNotificationsPreference.setSummary("removes all notifications");
        clearNotificationsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                createClearDialog().show();
                return false;
            }
        });

        isActiveCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean)newValue) {
                    startSampleExtensionService();
                } else {
                    stopSampleExtensionService();
                }
                return true;
            }
        });


        root.addPreference(isActiveCheckBox);
        root.addPreference(clearNotificationsPreference);
        root.addPreference(about);

        return root;
    }
}
