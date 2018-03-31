package march.marchappl.utils;

import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Date;

import march.marchappl.R;

public class GlobalConstants {

    public static final String SERVER_SSL_URL = "http://www.mocky.io/";
    public static final int COLOR_NORMAL = android.R.color.transparent;
    public static final int COLOR_SELECTED = R.color.gray_light2;

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