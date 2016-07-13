package com.maxkrass.stundenplan.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.inject.Inject;
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.activities.MainActivity;
import com.maxkrass.stundenplan.objects.Lesson;
import com.maxkrass.stundenplan.objects.Weekday;
import com.orm.SugarRecord;
import com.orm.query.Select;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Max made this for Stundenplan2 on 18.06.2016.
 */
public class NotificationService extends IntentService {
	private static final String TAG = "NotificationService";

	Calendar calendar = GregorianCalendar.getInstance();

	public NotificationService() {
		super("StundenplanNotificationService");
	}

	@Inject
	AlarmManager alarmManager;

	@Override
	protected void onHandleIntent(Intent intent) {
		Intent resultIntent = new Intent(this, MainActivity.class);

		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Intent rescheduleIntent = new Intent(this, NotificationService.class);

		long lessonID = intent.getLongExtra("lessonID", 0);

		final int UPDATE_ONGOING_LESSON = 0x100;
		final int SHOW_UPCOMING_LESSON = 0x200;
		final int NO_REQUEST_CODE = 0x000;

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		int mNotificationId = 1;
		if (lessonID > 0) {
			Lesson lessonToShow = SugarRecord.findById(Lesson.class, lessonID);
			int requestCode = intent.getIntExtra("requestCode", NO_REQUEST_CODE);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

			Log.d(TAG, "onHandleIntent: Retrieved Lesson: " + lessonToShow);

			switch (requestCode) {
				case NO_REQUEST_CODE:
					startService(new Intent(this, NotificationService.class));
					return;
				case UPDATE_ONGOING_LESSON:
					Log.d(TAG, "onHandleIntent: Request Code is UPDATE_ONGOING_LESSON");

					Calendar lessonCalendar = Calendar.getInstance();
					lessonCalendar.set(Calendar.DAY_OF_WEEK, lessonToShow.getWeekday().ordinal() + 2);
					lessonCalendar.set(Calendar.HOUR_OF_DAY, lessonToShow.getPeriod().getStartHour());
					lessonCalendar.set(Calendar.MINUTE, lessonToShow.getPeriod().getStartMinute());
					lessonCalendar.set(Calendar.SECOND, 0);
					lessonCalendar.set(Calendar.MILLISECOND, 0);

					long minutesToLesson = Math.round((lessonCalendar.getTimeInMillis() - calendar.getTimeInMillis()) / 60000);

					Log.d(TAG, "onHandleIntent: " + minutesToLesson + " Minutes until the lesson starts");

					if (minutesToLesson < 5) {
						builder.setContentTitle(lessonToShow.getSubject().getName() + " (" + lessonToShow.getLocation() + ")")
								.setSmallIcon(R.mipmap.ic_launcher)
								.setOngoing(true)
								.setCategory(NotificationCompat.CATEGORY_EVENT)
								.setPriority(NotificationCompat.PRIORITY_LOW)
								.setContentIntent(resultPendingIntent);

						if (minutesToLesson > 0) {

							builder.setContentText("In " + minutesToLesson + " minutes");
						} else {

							Calendar periodEndTime = Calendar.getInstance();
							periodEndTime.set(Calendar.HOUR_OF_DAY, lessonToShow.getPeriod().getEndHour());
							periodEndTime.set(Calendar.MINUTE, lessonToShow.getPeriod().getEndMinute());

							int minutesLeftUntilEnd = (int) ((periodEndTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 60000);
							builder.setContentText(minutesLeftUntilEnd + " Min. left");

							if (minutesLeftUntilEnd <= 0) {
								startService(rescheduleIntent);
								return;
							}
						}

						mNotificationManager.notify(mNotificationId, builder.build());

						Log.d(TAG, "onHandleIntent: notification updated");
						Calendar calendar = Calendar.getInstance();
						calendar.set(Calendar.SECOND, 0);
						calendar.set(Calendar.MILLISECOND, 0);
						calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1);

						rescheduleIntent.putExtra("lessonID", lessonToShow.getId());
						rescheduleIntent.putExtra("requestCode", UPDATE_ONGOING_LESSON);
						PendingIntent pendingIntent = PendingIntent.getService(this, 0, rescheduleIntent, PendingIntent.FLAG_UPDATE_CURRENT);
						alarmManager.setExact(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

						Log.d(TAG, "onHandleIntent: alarm set to update the notification for : " + new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.GERMANY).format(calendar.getTime()));
					}

					break;
//				case SHOW_UPCOMING_LESSON:
//					Log.d(TAG, "onHandleIntent: Request Code is SHOW_UPCOMING_LESSON");
//
//					break;
			}

		} else {

			Log.d(TAG, "onHandleIntent: gonna handle this Intent the default way");

			int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

			boolean nextWeek = true;

			Weekday currentWeekday = Weekday.MONDAY;                                                    // If it's the weekend we want to plan a notification for the next day available, which is monday

			Lesson nextOrCurrentLesson;
			if (currentDayOfWeek > Calendar.SUNDAY && currentDayOfWeek < Calendar.SATURDAY) {
				currentWeekday = Weekday.values()[currentDayOfWeek - 2];
				nextWeek = false;
				nextOrCurrentLesson = findFirstUnfinishedLesson(currentWeekday, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
				while (nextOrCurrentLesson == null) {
					if (currentWeekday.ordinal() + 1 > Weekday.FRIDAY.ordinal() && !nextWeek) {
						currentWeekday = Weekday.MONDAY;
						nextWeek = true;
					} else if (currentWeekday.ordinal() + 1 > Weekday.FRIDAY.ordinal() && nextWeek) {
						Log.d(TAG, "onHandleIntent: no lessons found for this or next week");
						return;
					} else {
						currentWeekday = Weekday.values()[currentWeekday.ordinal() + 1];
					}
					nextOrCurrentLesson = findFirstLesson(currentWeekday);
				}
			} else {
				do {
					nextOrCurrentLesson = findFirstLesson(currentWeekday);
					if (currentWeekday.ordinal() + 1 > Weekday.FRIDAY.ordinal() && nextOrCurrentLesson == null) {
						Log.d(TAG, "onHandleIntent: no lessons found for this nor next week");
						return;
					} else {
						currentWeekday = Weekday.values()[currentWeekday.ordinal() + 1];
					}
				} while (nextOrCurrentLesson == null);
			}

			Log.d(TAG, "onHandleIntent: ok, got my next Lesson: " + nextOrCurrentLesson);


			Calendar lessonCalendar = Calendar.getInstance();
			lessonCalendar.set(Calendar.DAY_OF_WEEK, nextOrCurrentLesson.getWeekday().ordinal() + 2);
			lessonCalendar.set(Calendar.HOUR_OF_DAY, nextOrCurrentLesson.getPeriod().getStartHour());
			lessonCalendar.set(Calendar.MINUTE, nextOrCurrentLesson.getPeriod().getStartMinute());
			lessonCalendar.set(Calendar.SECOND, 0);
			lessonCalendar.set(Calendar.MILLISECOND, 0);
			lessonCalendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR) + (nextWeek ? 1 : 0));

			long minutesToLesson = (lessonCalendar.getTimeInMillis() - calendar.getTimeInMillis()) / 60000;

			Log.d(TAG, "onHandleIntent: " + minutesToLesson + " Minutes until the lesson starts");

			if (minutesToLesson < 5) {

				NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this);
				builder.setContentTitle(nextOrCurrentLesson.getSubject().getName() + " (" + nextOrCurrentLesson.getLocation() + ")")
						.setSmallIcon(R.mipmap.ic_launcher)
						.setOngoing(true)
						.setDefaults(NotificationCompat.DEFAULT_ALL)
						.setContentIntent(resultPendingIntent);

				if (minutesToLesson > 0) {

					builder.setContentText("In " + minutesToLesson + " minutes");
				} else {

					Calendar periodEndTime = Calendar.getInstance();
					periodEndTime.set(Calendar.HOUR_OF_DAY, nextOrCurrentLesson.getPeriod().getEndHour());
					periodEndTime.set(Calendar.MINUTE, nextOrCurrentLesson.getPeriod().getEndMinute());

					int minutesLeftUntilEnd = (int) ((periodEndTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 60000);
					builder.setContentText(minutesLeftUntilEnd + " Min. left");
				}

				mNotificationManager.notify(mNotificationId, builder.build());

				Log.d(TAG, "onHandleIntent: notification issued");
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1);

				rescheduleIntent.putExtra("lessonID", nextOrCurrentLesson.getId());
				rescheduleIntent.putExtra("requestCode", UPDATE_ONGOING_LESSON);
				PendingIntent pendingIntent = PendingIntent.getService(this, 0, rescheduleIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				alarmManager.setExact(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

				Log.d(TAG, "onHandleIntent: alarm set to update the notification for : " + new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.GERMANY).format(calendar.getTime()));
			} else {
				mNotificationManager.cancelAll();

				rescheduleIntent.putExtra("lessonID", nextOrCurrentLesson.getId());
				rescheduleIntent.putExtra("requestCode", SHOW_UPCOMING_LESSON);
				PendingIntent pendingIntent = PendingIntent.getService(this, 0, rescheduleIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				long triggerTime = lessonCalendar.getTimeInMillis() - (5 * 60000);
				alarmManager.setExact(AlarmManager.RTC, triggerTime, pendingIntent);

				Log.d(TAG, "onHandleIntent: alarm set to plan the notification for : " + new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.GERMANY).format(new Date(triggerTime)));
			}
		}

//		/*long lessonID = intent.getLongExtra("lessonID", 0);
//		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//		if (lessonID > 0) {
//			Lesson lesson = SugarRecord.findById(Lesson.class, lessonID);
//
//			Calendar periodStartTime = Calendar.getInstance();
//			periodStartTime.set(Calendar.HOUR_OF_DAY, lesson.getPeriod().getStartHour());
//			periodStartTime.set(Calendar.MINUTE, lesson.getPeriod().getStartMinute());
//
//			int minutesLeftUntilStart = (int) ((periodStartTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 60000);
//
//		} else {
//
//			Calendar calendar = GregorianCalendar.getInstance();
//
//			int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
//
//			if (currentDayOfWeek > Calendar.SUNDAY && currentDayOfWeek < Calendar.SATURDAY) {
//				Weekday currentWeekday = Weekday.values()[currentDayOfWeek - 2];
//
//				Period currentPeriod = Select.from(Period.class).where("(end_hour > strftime('%H', 'now', 'localtime') or (end_hour = strftime('%H', 'now', 'localtime') and end_minute >= strftime('%M', 'now', 'localtime'))) and (start_hour < strftime('%H', 'now', 'localtime') or (start_hour = strftime('%H', 'now', 'localtime') and start_minute <= strftime('%M', 'now', 'localtime')))").first();
//
//				if (currentPeriod != null) {
//
//					Lesson currentLesson = Select.from(Lesson.class).where("weekday = \'" + currentWeekday.toString() + "\' and (period = " + currentPeriod.getId() + " or (period = " + (currentPeriod.getId() - 1) + " and double_period = 1))").first();
//
//					if (currentLesson != null) {
//
//						Calendar periodEndTime = Calendar.getInstance();
//						periodEndTime.set(Calendar.HOUR_OF_DAY, currentPeriod.getEndHour());
//						periodEndTime.set(Calendar.MINUTE, currentPeriod.getEndMinute());
//
//						int minutesLeft = (int) ((periodEndTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 60000);
//
//						Intent resultIntent = new Intent(this, MainActivity.class);
//
//						PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//						NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this);
//						builder.setContentTitle(currentLesson.getSubject().getName() + " (" + currentLesson.getLocation() + ")")
//								.setContentText(minutesLeft + " Min. left")
//								.setSmallIcon(R.mipmap.ic_launcher)
//								.setOngoing(true)
//								.setContentIntent(resultPendingIntent);
//
//						Notification notification = builder.build();
//						notification.defaults |= Notification.DEFAULT_VIBRATE;
//						notification.defaults |= Notification.DEFAULT_SOUND;
//
//						mNotificationManager.notify(mNotificationId, notification);
//					} else {
//						mNotificationManager.cancel(mNotificationId);
//					}
//				} else {
//					mNotificationManager.cancel(mNotificationId);
//				}
//			}
//		}*/

	}

	private Lesson findFirstUnfinishedLesson(Weekday weekday, int hour, int minute) {
		List<Lesson> lessonsToday = Select.from(Lesson.class).where("weekday = \'" + weekday.toString() + "\'").orderBy("period").list();

		Lesson nextOrCurrentLesson = null;

		for (Lesson l : lessonsToday) {
			if (l.getPeriod().getEndHour() > hour || (l.getPeriod().getEndHour() == hour && l.getPeriod().getEndMinute() > minute)) {
				nextOrCurrentLesson = l;
				break;
			}
		}

		return nextOrCurrentLesson;
	}

	private Lesson findFirstLesson(Weekday weekday) {
		return Select.from(Lesson.class).where("weekday = \'" + weekday.toString() + "\'").orderBy("period").first();
	}


}
