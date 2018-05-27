package edu.swe.healthcareapplication.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.step.DataManager;
import edu.swe.healthcareapplication.util.step.Navigator;
import edu.swe.healthcareapplication.view.adapter.TrainerStepAdapter;
import edu.swe.healthcareapplication.view.adapter.UserStepAdapter;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements DataManager, Navigator {

  private UserType mUserType;
  private Map<String, Object> mStepData;
  private ViewPager mViewPager;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signup);
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      mUserType = (UserType) bundle.getSerializable(BundleConstants.BUNDLE_USER_TYPE);
    }
    mStepData = new HashMap<>();
    initTitle();
    initView();
  }

  private void initTitle() {
    if (mUserType == UserType.USER) {
      setTitle(R.string.activity_signup_user_name);
    } else if (mUserType == UserType.TRAINER) {
      setTitle(R.string.activity_signup_trainer_name);
    }
  }

  private void initView() {
    mViewPager = findViewById(R.id.view_pager);
    if (mUserType == UserType.USER) {
      UserStepAdapter adapter = new UserStepAdapter(getSupportFragmentManager());
      mViewPager.setAdapter(adapter);
    } else {
      TrainerStepAdapter adapter = new TrainerStepAdapter(getSupportFragmentManager());
      mViewPager.setAdapter(adapter);
    }
  }

  @Override
  public void saveData(String key, Object value) {
    mStepData.put(key, value);
  }

  @Override
  public Object getData(String key) {
    return mStepData.get(key);
  }

  @Override
  public Map<String, Object> getAllData() {
    return mStepData;
  }

  @Override
  public boolean nextStep() {
    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
    hideKeyboard();
    return true;
  }

  @Override
  public boolean backStep() {
    if (mViewPager.getCurrentItem() != 0) {
      mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
      hideKeyboard();
      return true;
    }
    return false;
  }

  @Override
  public void completeStep() {
    if (mUserType == UserType.USER) {
      Intent intent = new Intent(this, SelectTrainerActivity.class);
      startActivity(intent);
      finish();
    } else {
      Intent intent = new Intent(this, EditTimeActivity.class);
      startActivity(intent);
      finish();
    }
  }

  @Override
  public void onBackPressed() {
    if (!backStep()) {
      super.onBackPressed();
    }
  }

  private void hideKeyboard() {
    View view = this.getCurrentFocus();
    if (view != null) {
      InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }
}
