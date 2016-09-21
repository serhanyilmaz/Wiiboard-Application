package com.tfkb.mobileapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class  	DovizTakipReceiver extends BroadcastReceiver {

	private static final long BIR_DAKIKA = 60 * 1000;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent serviceIntent = new Intent(context, DovizTakipIntentService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, 1, serviceIntent, 0);
		manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, BIR_DAKIKA, BIR_DAKIKA, pendingIntent);
		
	}
	
}
