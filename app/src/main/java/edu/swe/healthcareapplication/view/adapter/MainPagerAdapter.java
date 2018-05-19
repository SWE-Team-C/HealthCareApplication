package edu.swe.healthcareapplication.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import edu.swe.healthcareapplication.view.fragment.ChatFragment;
import edu.swe.healthcareapplication.view.fragment.ProfileFragment;
import edu.swe.healthcareapplication.view.fragment.TimeTableFragment;

public class MainPagerAdapter extends FragmentStatePagerAdapter {

  public MainPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override
  public Fragment getItem(int position) {
    if (position == 0) {
      return new ProfileFragment();
    } else if (position == 1) {
      return new TimeTableFragment();
    } else if (position == 2) {
      return new ChatFragment();
    }
    return null;
  }

  @Override
  public int getCount() {
    return 3;
  }
}
