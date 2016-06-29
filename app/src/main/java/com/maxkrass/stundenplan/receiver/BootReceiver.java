package com.maxkrass.stundenplan.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.maxkrass.stundenplan.services.NotificationService;

import java.util.Calendar;

/**
 * Max made this for Stundenplan2 on 18.06.2016.
 */
public class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent rescheduleIntent = new Intent(context, BootReceiver.class);
		context.startService(new Intent(context, NotificationService.class));
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, rescheduleIntent, 0);
		//alarmManager.setExact(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
	}
}
