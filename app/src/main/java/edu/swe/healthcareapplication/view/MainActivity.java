package edu.swe.healthcareapplication.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.view.adapter.MainPagerAdapter;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();

  private ViewPager mViewPager;
  private BottomNavigationView mBottomNavigationView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Bundle bundle = getIntent().getExtras();
    UserType userType = null;
    if (bundle != null) {
      userType = (UserType) bundle.getSerializable(BundleConstants.BUNDLE_USER_TYPE);
    }
    initView(userType);
  }

  @Override
  protected void onStart() {
    super.onStart();
    registerToken();
  }

  private void initView(UserType userType) {
    mViewPager = findViewById(R.id.view_pager);
    mBottomNavigationView = findViewById(R.id.bottom_navigation);

    MainPagerAdapter mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), userType);
    mViewPager.setAdapter(mPagerAdapter);
    mViewPager.setCurrentItem(1);
    mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // No-op
      }

      @Override
      public void onPageSelected(int position) {
        if (position == 0) {
          mBottomNavigationView.setSelectedItemId(R.id.action_profile);
        } else if (position == 1) {
          mBottomNavigationView.setSelectedItemId(R.id.action_timetable);
        } else if (position == 2) {
          mBottomNavigationView.setSelectedItemId(R.id.action_chat);
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {
        // No-op
      }
    });
    mBottomNavigationView.setSelectedItemId(R.id.action_timetable);
    mBottomNavigationView.setOnNavigationItemSelectedListener(
        item -> {
          if (item.getItemId() == R.id.action_profile) {
            mViewPager.setCurrentItem(0, false);
            return true;
          } else if (item.getItemId() == R.id.action_timetable) {
            mViewPager.setCurrentItem(1, false);
            return true;
          } else if (item.getItemId() == R.id.action_chat) {
            mViewPager.setCurrentItem(2, false);
            return true;
          } else {
            return false;
          }
        });
  }

  private void registerToken() {
    String token = FirebaseInstanceId.getInstance().getToken();
    Log.d(TAG, "Register Token: " + token);
    String uid = fetchUid();
    if (uid != null) {
      FirebaseDatabase.getInstance().getReference()
          .child(DatabaseConstants.CHILD_FCM_TOKEN)
          .child(uid)
          .setValue(token);
    }
  }

  @Nullable
  private String fetchUid() {
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    if (currentUser != null) {
      return currentUser.getUid();
    }
    return null;
  }
}
