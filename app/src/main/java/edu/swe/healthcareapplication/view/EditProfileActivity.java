package edu.swe.healthcareapplication.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.view.fragment.TrainerEditProfileFragment;
import edu.swe.healthcareapplication.view.fragment.UserEditProfileFragment;

public class EditProfileActivity extends AppCompatActivity {

  private UserType mUserType;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_profile);
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      mUserType = (UserType) bundle.getSerializable(BundleConstants.BUNDLE_USER_TYPE);
    }
    initView();
  }

  private void initView() {
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    if (mUserType == UserType.USER) {
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.container, new UserEditProfileFragment())
          .commit();
    } else if (mUserType == UserType.TRAINER) {
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.container, new TrainerEditProfileFragment())
          .commit();
    }
  }

  @Override
  public boolean onSupportNavigateUp() {
    onBackPressed();
    return super.onSupportNavigateUp();
  }
}
