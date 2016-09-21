package com.tfkb.mobileapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class  	WifiReceiver extends BroadcastReceiver {

    private static final long BIR_GUN= 10 * 1000 ;

    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent serviceIntent = new Intent(context, WifiService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, serviceIntent, 0);
        // manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, BIR_GUN, BIR_GUN, pendingIntent);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmManager.INTERVAL_HALF_DAY,
                AlarmManager.INTERVAL_DAY, pendingIntent);
        context.startService(serviceIntent);

    }


}
