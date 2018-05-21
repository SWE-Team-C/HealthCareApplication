package edu.swe.healthcareapplication.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.view.fragment.ChatFragment;
import edu.swe.healthcareapplication.view.fragment.ProfileFragment;
import edu.swe.healthcareapplication.view.fragment.TimeTableFragment;

public class MainPagerAdapter extends FragmentStatePagerAdapter {

  private UserType mUserType;
  
  public MainPagerAdapter(FragmentManager fm, UserType mUserType) {
    super(fm);
    this.mUserType = mUserType;
  }

  @Override
  public Fragment getItem(int position) {
    if (position == 0) {
      return ProfileFragment.newInstance(mUserType);
    } else if (position == 1) {
      return TimeTableFragment.newInstance(mUserType);
    } else if (position == 2) {
      return ChatFragment.newInstance(mUserType);
    }
    return null;
  }

  @Override
  public int getCount() {
    return 3;
  }
}
