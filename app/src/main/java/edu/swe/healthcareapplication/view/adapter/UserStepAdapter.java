package edu.swe.healthcareapplication.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import edu.swe.healthcareapplication.view.fragment.UserStepFragment;

public class UserStepAdapter extends FragmentPagerAdapter {

  public UserStepAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override
  public Fragment getItem(int position) {
    return UserStepFragment.newInstance(position);
  }

  @Override
  public int getCount() {
    return 2;
  }
}
