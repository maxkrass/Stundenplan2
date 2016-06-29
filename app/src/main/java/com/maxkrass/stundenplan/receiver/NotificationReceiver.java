package com.maxkrass.stundenplan.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.maxkrass.stundenplan.objects.Lesson;
import com.maxkrass.stundenplan.objects.Period;
import com.maxkrass.stundenplan.objects.Weekday;
import com.orm.SugarRecord;
import com.orm.query.Select;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Max made this for Stundenplan2 on 28.06.2016.
 */

public class NotificationReceiver extends BroadcastReceiver {
	private NotificationManager mNotificationManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		Calendar calendar = GregorianCalendar.getInstance();

		int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

		Weekday currentWeekday = Weekday.MONDAY;                                                    // If it's the weekend we want to plan a notification for the next day available, which is monday

		boolean unfinishedLessonsRemainingToday = false;

		if (currentDayOfWeek > Calendar.SUNDAY && currentDayOfWeek < Calendar.SATURDAY) {
			currentWeekday = Weekday.values()[currentDayOfWeek - 2];
		}

		List<Period> periods = SugarRecord.listAll(Period.class);

		if (periods.size() > 0) {

			Period period = null;

			for (Period p : periods) {
				boolean earlierThanEnd = p.getEndHour() > calendar.get(Calendar.HOUR_OF_DAY) || (p.getEndHour() == calendar.get(Calendar.HOUR_OF_DAY) && p.getEndMinute() > calendar.get(Calendar.MINUTE));
				//boolean laterThanStart = p.getStartHour() < calendar.get(Calendar.HOUR_OF_DAY) || (p.getStartHour() == calendar.get(Calendar.HOUR_OF_DAY) && p.getStartMinute() < calendar.get(Calendar.MINUTE));
				if (earlierThanEnd) {
					period = p;
					unfinishedLessonsRemainingToday = true;
					break;
				}
			}

			if (period == null) {                                                                   // At this point, there are no remaining periods for today
				unfinishedLessonsRemainingToday = false;
				period = periods.get(0);                                                            // So we get the first period of the next day, so we can plan the notification
				int indexOfTodaysWeekday = Arrays.binarySearch(Weekday.values(), currentWeekday);
				if (indexOfTodaysWeekday + 1 > Weekday.values().length) {
					currentWeekday = Weekday.MONDAY;
				} else
					currentWeekday = Weekday.values()[indexOfTodaysWeekday + 1];
			}

			//period = Select.from(Period.class).where("(end_hour > strftime('%H', 'now', 'localtime') or (end_hour = strftime('%H', 'now', 'localtime') and end_minute >= strftime('%M', 'now', 'localtime'))) and (start_hour < strftime('%H', 'now', 'localtime') or (start_hour = strftime('%H', 'now', 'localtime') and start_minute <= strftime('%M', 'now', 'localtime')))").first();

			mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
			if (period != null) {

				Lesson currentLesson = Select.from(Lesson.class).where("weekday = \'" + currentWeekday.toString() + "\' and (period >= " + period.getId() + " or (period >= " + (period.getId() - 1) + " and double_period = 1))").first();

				if (currentLesson != null) {                                                        //This will be the next lesson, either the current one or the next one today or the first one tomorrow

					if (unfinishedLessonsRemainingToday) {

					} else {
						nextDayOfWeek(Arrays.binarySearch(Weekday.values(), currentWeekday) + 2);
					}

				}
			}
		}
	}

	public static Calendar nextDayOfWeek(int dow) {
		Calendar date = Calendar.getInstance();
		int diff = dow - date.get(Calendar.DAY_OF_WEEK);
		if (!(diff > 0)) {
			diff += 7;
		}
		date.add(Calendar.DAY_OF_MONTH, diff);
		return date;
	}
}
