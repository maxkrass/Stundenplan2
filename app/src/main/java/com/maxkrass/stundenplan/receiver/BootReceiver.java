package com.maxkrass.stundenplan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.maxkrass.stundenplan.services.NotificationService;

/**
 * Max made this for Stundenplan2 on 18.06.2016.
 */
public class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, NotificationService.class));
	}
}
