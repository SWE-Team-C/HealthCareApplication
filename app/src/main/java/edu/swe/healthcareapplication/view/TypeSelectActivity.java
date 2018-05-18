package edu.swe.healthcareapplication.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.databinding.ActivityTypeSelectBinding;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;

public class TypeSelectActivity extends AppCompatActivity implements OnClickListener {

  private ActivityTypeSelectBinding mBinding;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding = DataBindingUtil.setContentView(this, R.layout.activity_type_select);
    mBinding.btnTypeUser.setOnClickListener(this);
    mBinding.btnTypeTrainer.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    Intent intent = new Intent(this, SignInActivity.class);
    Bundle bundle = new Bundle();
    if (v.getId() == R.id.btn_type_user) {
      bundle.putSerializable(BundleConstants.BUNDLE_USER_TYPE, UserType.USER);
    } else if (v.getId() == R.id.btn_type_trainer) {
      bundle.putSerializable(BundleConstants.BUNDLE_USER_TYPE, UserType.TRAINER);
    }
    intent.putExtras(bundle);
    startActivity(intent);
    finish();
  }

  @Override
  public void onBackPressed() {
    // TODO 종료할지 물어보는 다이얼로그 추가
    super.onBackPressed();
  }
}
