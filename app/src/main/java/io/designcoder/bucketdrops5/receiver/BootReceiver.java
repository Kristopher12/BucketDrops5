package io.designcoder.bucketdrops5.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.designcoder.bucketdrops5.extras.Util;

public class BootReceiver extends BroadcastReceiver {
    public static final String TAG = "Christo";

    public BootReceiver() {
        Log.d(TAG, "BootReceiver: ");
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        Util.scheduleAlarm(context);
    }
}
