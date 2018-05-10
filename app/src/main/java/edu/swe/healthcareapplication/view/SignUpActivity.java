package edu.swe.healthcareapplication.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.view.fragment.TrainerStepFragment;
import edu.swe.healthcareapplication.view.fragment.UserStepFragment;

public class SignUpActivity extends AppCompatActivity {

  private UserType mUserType;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signup);
  }

  @Override
  protected void onStart() {
    super.onStart();
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      mUserType = (UserType) bundle.getSerializable(BundleConstants.BUNDLE_USER_TYPE);
    }
    initStepView();
  }

  private void initStepView() {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    if (mUserType == UserType.USER) {
      transaction.add(R.id.step_container, UserStepFragment.newInstance(mUserType));
    } else if (mUserType == UserType.TRAINER) {
      transaction.add(R.id.step_container, TrainerStepFragment.newInstance(mUserType));
    }
    transaction.commit();
  }
}
