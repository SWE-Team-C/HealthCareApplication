package edu.swe.healthcareapplication.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import edu.swe.healthcareapplication.view.fragment.TrainerStepFragment;

public class TrainerStepAdapter extends FragmentPagerAdapter {

  public TrainerStepAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override
  public Fragment getItem(int position) {
    return TrainerStepFragment.newInstance(position);
  }

  @Override
  public int getCount() {
    return 2;
  }
}
