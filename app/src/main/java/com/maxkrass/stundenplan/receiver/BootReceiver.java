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
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			context.startService(new Intent(context, NotificationService.class));
		}
	}
}
