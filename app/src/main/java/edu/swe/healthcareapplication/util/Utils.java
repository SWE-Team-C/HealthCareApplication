package edu.swe.healthcareapplication.util;

import android.content.Context;
import edu.swe.healthcareapplication.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

  public static String formatHour(Context context, int hour) {
    if (hour > 12) {
      return context.getString(R.string.hour_format_pm, hour - 12);
    } else {
      return context.getString(R.string.hour_format_am, hour);
    }
  }

  public static String formatTimestamp(Context context, long timestamp) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(
        context.getString(R.string.timestamp_format), Locale.getDefault());
    Date date = new Date(timestamp);
    return dateFormat.format(date);
  }
}
