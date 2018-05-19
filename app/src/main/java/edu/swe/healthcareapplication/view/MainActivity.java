package edu.swe.healthcareapplication.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.databinding.ActivityMainBinding;
import edu.swe.healthcareapplication.view.adapter.MainPagerAdapter;

public class MainActivity extends AppCompatActivity {

  private ActivityMainBinding mBinding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    initView();
  }

  private void initView() {
    MainPagerAdapter mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
    mBinding.viewPager.setAdapter(mPagerAdapter);
    mBinding.viewPager.setCurrentItem(1);
    mBinding.viewPager.addOnPageChangeListener(new OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // No-op
      }

      @Override
      public void onPageSelected(int position) {
        if (position == 0) {
          mBinding.bottomNavigation.setSelectedItemId(R.id.action_profile);
        } else if (position == 1) {
          mBinding.bottomNavigation.setSelectedItemId(R.id.action_timetable);
        } else if (position == 2) {
          mBinding.bottomNavigation.setSelectedItemId(R.id.action_chat);
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {
        // No-op
      }
    });
    mBinding.bottomNavigation.setSelectedItemId(R.id.action_timetable);
    mBinding.bottomNavigation.setOnNavigationItemSelectedListener(
        item -> {
          if (item.getItemId() == R.id.action_profile) {
            mBinding.viewPager.setCurrentItem(0, true);
            return true;
          } else if (item.getItemId() == R.id.action_timetable) {
            mBinding.viewPager.setCurrentItem(1, true);
            return true;
          } else if (item.getItemId() == R.id.action_chat) {
            mBinding.viewPager.setCurrentItem(2, true);
            return true;
          } else {
            return false;
          }
        });
  }
}
