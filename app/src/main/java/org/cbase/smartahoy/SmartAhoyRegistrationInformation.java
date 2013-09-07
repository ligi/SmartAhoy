package org.cbase.smartahoy;

import android.content.ContentValues;
import android.content.Context;

import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

public class SmartAhoyRegistrationInformation extends RegistrationInformation {

    private Context mContext;

      /**
     * Create notification registration object
     *
     * @param context The context
     */
    protected SmartAhoyRegistrationInformation(Context context) {
	if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        mContext = context;
    }


    @Override
    public int getRequiredNotificationApiVersion() {
        return 1;
    }

    @Override
    public ContentValues getExtensionRegistrationConfiguration() {


        String extensionIcon = ExtensionUtils.getUriString(mContext,
                        R.drawable.ic_launcher);
        String iconHostapp = ExtensionUtils.getUriString(mContext,
                R.drawable.ic_launcher);
        String extensionIcon48 = ExtensionUtils.getUriString(mContext,
                R.drawable.ic_launcher);

        String configurationText = "configuration text";
        String extensionName = "SmartAhoy";

        ContentValues values = new ContentValues();
        /*values.put(Registration.ExtensionColumns.CONFIGURATION_ACTIVITY,
                SamplePreferenceActivity.class.getName());*/
        values.put(Registration.ExtensionColumns.CONFIGURATION_TEXT, configurationText);
        values.put(Registration.ExtensionColumns.EXTENSION_ICON_URI, extensionIcon);
        values.put(Registration.ExtensionColumns.EXTENSION_48PX_ICON_URI, extensionIcon48);


        values.put(Registration.ExtensionColumns.EXTENSION_KEY,
                "org.cbase.smartahoy");

        values.put(Registration.ExtensionColumns.HOST_APP_ICON_URI, iconHostapp);
        values.put(Registration.ExtensionColumns.NAME, extensionName);
        values.put(Registration.ExtensionColumns.NOTIFICATION_API_VERSION,
                getRequiredNotificationApiVersion());
        values.put(Registration.ExtensionColumns.PACKAGE_NAME, mContext.getPackageName());


        return values;
    }

    @Override
    public int getRequiredWidgetApiVersion() {
        return 0;
    }

    @Override
    public int getRequiredControlApiVersion() {
        return 0;
    }

    @Override
    public int getRequiredSensorApiVersion() {
        return 0;
    }
}
