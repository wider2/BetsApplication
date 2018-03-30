package march.marchappl.utils;

import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Date;

public class GlobalConstants {

    public static final String SERVER_SSL_URL = "http://www.mocky.io/";

    public static Date addMinute() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, -1);
        return now.getTime();
    }

    public static long addMinuteTimestamp() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, -1);
        return now.getTimeInMillis();
    }

}