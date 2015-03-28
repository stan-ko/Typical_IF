package typical_if.android.util;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.TimeZone;

import typical_if.android.Constants;

/**
 * Created by CTAC on 28.03.2015.
 */
public class DateUtils {

    public static String getFormattedDate(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeZone(TimeZone.getTimeZone("Europe/Kiev"));
        smsTime.setTimeInMillis(smsTimeInMilis * 1000);

        Calendar now = Calendar.getInstance();
        now.setTimeZone(TimeZone.getTimeZone("Europe/Kiev"));

        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return String.format(Constants.TODAY, DateFormat.format(Constants.TIME_FORMAT_STRING, smsTime));
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return String.format(Constants.YESTERDAY, DateFormat.format(Constants.TIME_FORMAT_STRING, smsTime));
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(Constants.DATE_TIME_FORMAT_STRING, smsTime).toString();
        } else
            return DateFormat.format(Constants.OTHER_FORMAT_STRING, smsTime).toString();
    }

    public static boolean isToday(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeZone(TimeZone.getTimeZone("Europe/Kiev"));
        smsTime.setTimeInMillis(smsTimeInMilis * 1000);

        Calendar now = Calendar.getInstance();
        now.setTimeZone(TimeZone.getTimeZone("Europe/Kiev"));

        return now.get(Calendar.DATE) == smsTime.get(Calendar.DATE);
    }

}
