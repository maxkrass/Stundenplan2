package com.maxkrass.stundenplan.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.activities.MainActivity;
import com.maxkrass.stundenplan.objects.Lesson;
import com.maxkrass.stundenplan.objects.Period;
import com.maxkrass.stundenplan.objects.Weekday;
import com.orm.query.Select;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Max made this for Stundenplan2 on 18.06.2016.
 */
public class NotificationService extends IntentService {
	private static final String TAG = "NotificationService";
	private NotificationManager mNotificationManager;

	private int mNotificationId = 001;

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 */
	public NotificationService() {
		super("StundenplanNotificationService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Calendar calendar = GregorianCalendar.getInstance();

		int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

		if (currentDayOfWeek > Calendar.SUNDAY && currentDayOfWeek < Calendar.SATURDAY) {
			Weekday currentWeekday = Weekday.values()[currentDayOfWeek - 2];

			Period currentPeriod = Select.from(Period.class).where("(end_hour > strftime('%H', 'now', 'localtime') or (end_hour = strftime('%H', 'now', 'localtime') and end_minute >= strftime('%M', 'now', 'localtime'))) and (start_hour < strftime('%H', 'now', 'localtime') or (start_hour = strftime('%H', 'now', 'localtime') and start_minute <= strftime('%M', 'now', 'localtime')))").first();

			mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			if (currentPeriod != null) {

				Lesson currentLesson = Select.from(Lesson.class).where("weekday = \'" + currentWeekday.toString() + "\' and (period = " + currentPeriod.getId() + " or (period = " + (currentPeriod.getId() - 1) + " and double_period = 1))").first();

				if (currentLesson != null) {

					Calendar periodEndTime = Calendar.getInstance();
					periodEndTime.set(Calendar.HOUR_OF_DAY, currentPeriod.getEndHour());
					periodEndTime.set(Calendar.MINUTE, currentPeriod.getEndMinute());

					int minutesLeft = (int) ((periodEndTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 60000);

					Intent resultIntent = new Intent(this, MainActivity.class);

					PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

					NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this);
					builder.setContentTitle(currentLesson.getSubject().getName() + " (" + currentLesson.getLocation() + ")")
							.setContentText(minutesLeft + " Min. left")
							.setSmallIcon(R.mipmap.ic_launcher)
							.setOngoing(true)
							.setContentIntent(resultPendingIntent);

					Notification notification = builder.build();
					notification.defaults |= Notification.DEFAULT_VIBRATE;
					notification.defaults |= Notification.DEFAULT_SOUND;

					mNotificationManager.notify(mNotificationId, notification);
				} else {
					mNotificationManager.cancel(mNotificationId);
				}
			} else {
				mNotificationManager.cancel(mNotificationId);
			}
		}

	}


}
