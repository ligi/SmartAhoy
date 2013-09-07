package org.cbase.smartahoy;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.notification.NotificationUtil;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfoHelper;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

import java.util.HashMap;

public class SmartAhoyExtensionService extends ExtensionService {


    private Long lastFirstSeen =0L;

    private static final String AHOY_UPDATE_INTENT = "AhoyActivityUpdate";

    /**
     * Extensions specific id for the source
     */
    public static final String EXTENSION_SPECIFIC_ID = "AHOY_ID_NEW_MESSAGE";

    /**
     * Extension key
     */
    public static final String EXTENSION_KEY = "com.cbase.smartahoy";

    /**
     * Log tag
     */
    public static final String LOG_TAG = "SmartAhoy";

    /**
     * Starts periodic insert of data handled in onStartCommand()
     */
    public static final String INTENT_ACTION_START = "com.sonymobile.smartconnect.extension.notificationsample.action.start";

    /**
     * Stop periodic insert of data, handled in onStartCommand()
     */
    public static final String INTENT_ACTION_STOP = "com.sonymobile.smartconnect.extension.notificationsample.action.stop";

    /**
     * Add data, handled in onStartCommand()
     */
    private static final String INTENT_ACTION_ADD = "com.sonymobile.smartconnect.extension.notificationsample.action.add";
    private BroadcastReceiver mReceiver;

    public SmartAhoyExtensionService() {
        super(EXTENSION_KEY);
    }

    /**
     * {@inheritDoc}
     *
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int retVal = super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            if (INTENT_ACTION_START.equals(intent.getAction())) {
                Log.d(LOG_TAG, "onStart action: INTENT_ACTION_START");
                startBroadcastReceiver();
                stopSelfCheck();
            } else if (INTENT_ACTION_STOP.equals(intent.getAction())) {
                Log.d(LOG_TAG, "onStart action: INTENT_ACTION_STOP");
                stopBroadcastReceiver();
                stopSelfCheck();
            } else if (INTENT_ACTION_ADD.equals(intent.getAction())) {
                Log.d(LOG_TAG, "onStart action: INTENT_ACTION_ADD");
                stopSelfCheck();
            }
        }

        return retVal;
    }

    /**
     * {@inheritDoc}
     *
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        //super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onViewEvent(Intent intent) {
        String action = intent.getStringExtra(Notification.Intents.EXTRA_ACTION);
        String hostAppPackageName = intent
                .getStringExtra(Registration.Intents.EXTRA_AHA_PACKAGE_NAME);
        boolean advancedFeaturesSupported = DeviceInfoHelper.isSmartWatch2ApiAndScreenDetected(
                this, hostAppPackageName);

        int eventId = intent.getIntExtra(Notification.Intents.EXTRA_EVENT_ID, -1);
        if (Notification.SourceColumns.ACTION_1.equals(action)) {
            doAction1(eventId);
        } else if (Notification.SourceColumns.ACTION_2.equals(action)) {
            // Here we can take different actions depending on the device.
            if (advancedFeaturesSupported) {
                Toast.makeText(this, "Action 2 API level 2", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Action 2", Toast.LENGTH_LONG).show();
            }
        } else if (Notification.SourceColumns.ACTION_3.equals(action)) {
            Toast.makeText(this, "Action 3", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onRefreshRequest() {
        // Do nothing here, only relevant for polling extensions, this
        // extension is always up to date
    }

    /**
     * Show toast with event information
     *
     * @param eventId The event id
     */
    public void doAction1(int eventId) {
        Log.d(LOG_TAG, "doAction1 event id: " + eventId);
        Cursor cursor = null;
        try {
            String name = "";
            String message = "";
            cursor = getContentResolver().query(Notification.Event.URI, null,
                    Notification.EventColumns._ID + " = " + eventId, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(Notification.EventColumns.DISPLAY_NAME);
                int messageIndex = cursor.getColumnIndex(Notification.EventColumns.MESSAGE);
                name = cursor.getString(nameIndex);
                message = cursor.getString(messageIndex);
            }

            String toastMessage = "Event" + ", Event: " + eventId
                    + ", Name: " + name + ", Message: " + message;
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Failed to query event", e);
        } catch (SecurityException e) {
            Log.e(LOG_TAG, "Failed to query event", e);
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, "Failed to query event", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Called when extension and sources has been successfully registered.
     * Override this method to take action after a successful registration.
     */
    @Override
    public void onRegisterResult(boolean result) {
        super.onRegisterResult(result);
        Log.d(LOG_TAG, "onRegisterResult");

        // Start adding data if extension is active in preferences
        if (result) {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(this);
            boolean isActive = prefs.getBoolean("active", true);
            if (isActive) {
                startBroadcastReceiver();
            } else {
                stopBroadcastReceiver();
            }
        }
    }

    @Override
    protected RegistrationInformation getRegistrationInformation() {
        return new SmartAhoyRegistrationInformation(this);
    }

    /*
     * (non-Javadoc)
     * @see com.sonyericsson.extras.liveware.aef.util.ExtensionService#
     * keepRunningWhenConnected()
     */
    @Override
    protected boolean keepRunningWhenConnected() {
        return false;
    }

    private void startBroadcastReceiver() {
        Log.d(LOG_TAG, "Registering Ahoy broadcast receiver");
        mReceiver = new AhoyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AHOY_UPDATE_INTENT);
        registerReceiver(mReceiver, filter);
    }

    private void stopBroadcastReceiver() {
        if (mReceiver != null) {
            Log.d(LOG_TAG, "Unregistering Ahoy broadcast receiver");
            unregisterReceiver(mReceiver);
        }
    }

    private void createNotification(String message) {
        Log.d(LOG_TAG, "Creating notification for new Ahoy message: " + message);

        long time = System.currentTimeMillis();
        long sourceId = NotificationUtil
                .getSourceId(this, EXTENSION_SPECIFIC_ID);
        if (sourceId == NotificationUtil.INVALID_ID) {
            Log.e(LOG_TAG, "Failed to insert data");
            return;
        }
        String profileImage = ExtensionUtils.getUriString(this,
                R.drawable.ic_launcher);

        ContentValues eventValues = new ContentValues();
        eventValues.put(Notification.EventColumns.EVENT_READ_STATUS, false);
        eventValues.put(Notification.EventColumns.DISPLAY_NAME, message);
        eventValues.put(Notification.EventColumns.MESSAGE, message);
        eventValues.put(Notification.EventColumns.PERSONAL, 1);
        eventValues.put(Notification.EventColumns.PROFILE_IMAGE_URI, profileImage);
        eventValues.put(Notification.EventColumns.PUBLISHED_TIME, time);
        eventValues.put(Notification.EventColumns.SOURCE_ID, sourceId);

        try {
            getContentResolver().insert(Notification.Event.URI, eventValues);
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, "Failed to insert event", e);
        } catch (SecurityException e) {
            Log.e(LOG_TAG, "Failed to insert event, is Live Ware Manager installed?", e);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Failed to insert event", e);
        }
    }



    class AhoyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            HashMap<String, HashMap<String, Long>> messageHash =
                    (HashMap<String, HashMap<String, Long>>) intent.getSerializableExtra("messageHash");



            if (messageHash != null) {
                Long newLastFirstSeen=lastFirstSeen;

                for ( String msg : messageHash.keySet()) {
                    HashMap<String, Long> messageMeta = messageHash.get(msg);
                    Long firstSeen = messageMeta.get("firstSeen");

                    if (firstSeen>lastFirstSeen) {
                        newLastFirstSeen=Math.max(firstSeen,newLastFirstSeen);
                        createNotification(msg);
                    }

                }

                lastFirstSeen=newLastFirstSeen;
            }
        }
    }
}
