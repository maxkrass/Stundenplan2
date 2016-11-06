package com.maxkrass.stundenplan.services;


import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.activities.MainActivity;
import com.maxkrass.stundenplan.tools.Tools;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

	private static String[] stringsFromString(String string) {
		String[] strings = string.replace("[", "").replace("]", "").split(",");
		for (int i = 0; i < strings.length; i++) {
			strings[i] = strings[i].replaceAll("\"", "");
		}
		return strings;
	}

	private static Integer[] intsFromString(String string) {
		String[] strings = string.replace("[", "").replace("]", "").split(",");
		Integer result[] = new Integer[strings.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = Integer.parseInt(strings[i]);
		}
		return result;
	}

	/**
	 * Called when message is received.
	 *
	 * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
	 */
	// [START receive_message]
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {

		// Check if message contains a data payload. (Aka is from the heroku server)
		if (remoteMessage.getData().size() > 0) {

			Map<String, String> data = remoteMessage.getData();

			String stand = data.get("stand");
			Integer amountsOfEvents[] = intsFromString(data.get("amountsOfEvents"));
			String[] dates = stringsFromString(data.get("dates"));

			String bigText = "";
			int substitutions = 0;

			for (int i = 0; i < amountsOfEvents.length; i++) {

				String dateArray[] = dates[i].split(" ");
				String dateWeekday = dateArray[1];
				String dateTime = dateArray[0];
				String shortDate = dateTime.substring(0, dateTime.lastIndexOf(".")) + ".";

				if (amountsOfEvents[i] > 0) {
					bigText = bigText.concat("Vertretungen für " + dateWeekday + ", " + shortDate + ": " + amountsOfEvents[i] + "\n");
					substitutions++;
				} else {
					bigText = bigText.concat("Keine Vertretungen für " + dateWeekday + ", " + shortDate + "\n");
				}
			}

			Intent resultIntent = new Intent(this, MainActivity.class);

			resultIntent.putExtra("requestCode", MainActivity.OPEN_SUBSTITUTIONS_REQUEST_CODE);

			PendingIntent resultPendingIntent = PendingIntent.getActivity(this, MainActivity.OPEN_SUBSTITUTIONS_REQUEST_CODE, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
			notificationBuilder
					.setStyle(
							new NotificationCompat.BigTextStyle(notificationBuilder)
									.setBigContentTitle("Neue Vertretungspläne")
									.bigText(bigText.trim())
									.setSummaryText("Stand: " + stand))
					.setSmallIcon(R.drawable.ic_launcher_notification_small)
					.setColor(ContextCompat.getColor(this, R.color.material_teal))
					//.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
					.setContentTitle("Neue Vertretungspläne (" + substitutions + ")")
					.setContentText("Aktualisiert: " + stand)
					.setAutoCancel(true)
					.setContentIntent(resultPendingIntent);

			NotificationManagerCompat.from(this).notify(Tools.SUBSTITUTION_NOTIFICATION_ID, notificationBuilder.build());

		}
	}
}