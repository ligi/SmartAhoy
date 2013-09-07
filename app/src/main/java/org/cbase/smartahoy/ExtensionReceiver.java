package org.cbase.smartahoy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ExtensionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d("SmartAhoy", "onReceive: " + intent.getAction());
        intent.setClass(context, SmartAhoyExtensionService.class);
        context.startService(intent);
    }
}
