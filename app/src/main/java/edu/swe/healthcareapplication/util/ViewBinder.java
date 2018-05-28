package edu.swe.healthcareapplication.util;

import android.view.View;

public abstract class ViewBinder<T> {

  protected View rootView;

  public ViewBinder(View rootView) {
    this.rootView = rootView;
  }

  public abstract void bind(T t);
}
