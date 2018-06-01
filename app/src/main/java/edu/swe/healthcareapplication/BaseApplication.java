package edu.swe.healthcareapplication;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;

public class BaseApplication extends Application implements ActivityLifecycleCallbacks {

  private boolean isBackground = false;

  @Override
  public void onCreate() {
    super.onCreate();
    registerActivityLifecycleCallbacks(this);
  }

  @Override
  public void onActivityCreated(Activity activity, Bundle bundle) {
    isBackground = false;
  }

  @Override
  public void onActivityStarted(Activity activity) {
    // No-op
  }

  @Override
  public void onActivityResumed(Activity activity) {
    // No-op
  }

  @Override
  public void onActivityPaused(Activity activity) {
    // No-op
  }

  @Override
  public void onActivityStopped(Activity activity) {
    isBackground = true;
  }

  @Override
  public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    // No-op
  }

  @Override
  public void onActivityDestroyed(Activity activity) {
    // No-op
  }

  public boolean isBackground() {
    return isBackground;
  }
}
