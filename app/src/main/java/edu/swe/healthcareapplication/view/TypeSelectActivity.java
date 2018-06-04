package edu.swe.healthcareapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import java.util.Arrays;
import java.util.List;

public class TypeSelectActivity extends AppCompatActivity implements OnClickListener {

  private static final String TAG = TypeSelectActivity.class.getSimpleName();
  private static final int RC_SIGN_IN = 123;

  private UserType mSelectUserType;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_type_select);
    initView();
  }

  private void initView() {
    findViewById(R.id.btn_type_user).setOnClickListener(this);
    findViewById(R.id.btn_type_trainer).setOnClickListener(this);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_SIGN_IN) {
      IdpResponse response = IdpResponse.fromResultIntent(data);
      if (resultCode == RESULT_OK) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        checkSignUpUser(currentUser);
      } else {
        if (response == null) {
          showToast(R.string.msg_login_cancelled);
          return;
        }
        if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
          showToast(R.string.msg_login_no_connection);
          return;
        }
        showToast(R.string.msg_unknown_error);
        Log.e(TAG, "Sign-in error: ", response.getError());
      }
    }
  }

  @Override
  public void onBackPressed() {
    // TODO 종료할지 물어보는 다이얼로그 추가
    super.onBackPressed();
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.btn_type_user) {
      mSelectUserType = UserType.USER;
    } else if (v.getId() == R.id.btn_type_trainer) {
      mSelectUserType = UserType.TRAINER;
    }
    signIn();
  }

  private void showToast(@StringRes int message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  private void signIn() {
    List<IdpConfig> providers = Arrays.asList(
        new AuthUI.IdpConfig.EmailBuilder().build(),
        new AuthUI.IdpConfig.GoogleBuilder().build());

    startActivityForResult(
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(false)
            .setAvailableProviders(providers)
            .setLogo(R.mipmap.ic_launcher_round)
            .build(),
        RC_SIGN_IN);
  }

  private void checkSignUpUser(@NonNull FirebaseUser user) {
    String uid = user.getUid();

    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference()
        .child(DatabaseConstants.CHILD_USER_TYPES)
        .child(uid);

    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        String value = (String) dataSnapshot.getValue();
        if (value != null) {
          UserType userType;
          if (value.equals(DatabaseConstants.USER_TYPE_TRAINER)) {
            userType = UserType.TRAINER;
          } else {
            userType = UserType.USER;
          }
          navigateMain(userType);
        } else {
          navigateSignUp();
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: " + databaseError.toString());
      }
    });
  }

  private void navigateMain(UserType userType) {
    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra(BundleConstants.BUNDLE_USER_TYPE, userType);
    startActivity(intent);
    finish();
  }

  private void navigateSignUp() {
    Intent intent = new Intent(this, SignUpActivity.class);
    Bundle bundle = new Bundle();
    bundle.putSerializable(BundleConstants.BUNDLE_USER_TYPE, mSelectUserType);
    intent.putExtras(bundle);
    startActivity(intent);
    finish();
  }
}
