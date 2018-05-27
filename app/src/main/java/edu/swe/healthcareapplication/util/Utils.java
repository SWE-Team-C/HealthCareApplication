package edu.swe.healthcareapplication.util;

import android.content.Context;
import edu.swe.healthcareapplication.R;

public class Utils {

  public static String formatHour(Context context, int hour) {
    if (hour > 12) {
      return context.getString(R.string.hour_format_pm, hour - 12);
    } else {
      return context.getString(R.string.hour_format_am, hour);
    }
  }
}
