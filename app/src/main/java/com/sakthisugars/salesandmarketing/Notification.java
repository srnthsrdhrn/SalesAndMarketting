package com.sakthisugars.salesandmarketing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by singapore on 29-06-2016.
 */
public class Notification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.stopService(new Intent(context,LocationService.class));
        LocationService.START=0;

    }
}
