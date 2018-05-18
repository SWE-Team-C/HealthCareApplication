package edu.swe.healthcareapplication.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.databinding.ActivitySigninBinding;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;

public class SignInActivity extends AppCompatActivity {

  public static final int RC_GOOGLE_SIGN_IN = 101;
  private static final String TAG = SignInActivity.class.getSimpleName();

  private FirebaseAuth mAuth;
  private UserType mUserType;
  private ActivitySigninBinding mBinding;

  @Override
  protected void onStart() {
    super.onStart();
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      mUserType = (UserType) bundle.getSerializable(BundleConstants.BUNDLE_USER_TYPE);
    }
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding = DataBindingUtil.setContentView(this, R.layout.activity_signin);
    mAuth = FirebaseAuth.getInstance();
    mBinding.googleSigninButton.setOnClickListener((v) -> signInWithGoogle());
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_GOOGLE_SIGN_IN) {
      Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
      handleSignInResult(task);
    }
  }

  private void signInWithGoogle() {
    GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(
        GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .requestProfile()
        .build();

    GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

    Intent signInIntent = client.getSignInIntent();
    startActivityForResult(signInIntent, SignInActivity.RC_GOOGLE_SIGN_IN);
  }

  private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
    Log.d(TAG, "firebaseAuthWithGoogle: " + account.getId());
    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
    mAuth.signInWithCredential(credential)
        .addOnCompleteListener(this, (task) -> {
          if (task.isSuccessful()) {
            Log.d(TAG, "signInWithCredential:success");
            FirebaseUser user = mAuth.getCurrentUser();
            handleSignIn(user);
          } else {
            Log.w(TAG, "signInWithCredential:failure", task.getException());
            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            handleSignIn(null);
          }
        });
  }

  private void handleSignInResult(Task<GoogleSignInAccount> task) {
    try {
      GoogleSignInAccount account = task.getResult(ApiException.class);
      firebaseAuthWithGoogle(account);
    } catch (ApiException e) {
      Log.w(TAG, "handleSignInResult:failed code= " + e.getStatusCode());
    }
  }

  private void handleSignIn(FirebaseUser user) {
    if (user != null) {
      Toast.makeText(this,
          getString(R.string.msg_login_successful) + " (" + user.getEmail() + ")",
          Toast.LENGTH_LONG)
          .show();

      String uid = user.getUid();
      FirebaseDatabase database = FirebaseDatabase.getInstance();
      DatabaseReference usersReference = database.getReference()
          .child(DatabaseConstants.CHILD_USER_TYPES).child(uid);
      usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          String userType = (String) dataSnapshot.getValue();
          Intent intent;
          Bundle bundle = new Bundle();
          if (userType != null) {
            if (userType.equals(DatabaseConstants.USER_TYPE_USER)) {
              bundle.putSerializable(BundleConstants.BUNDLE_USER_TYPE, UserType.USER);
            } else if (userType.equals(DatabaseConstants.USER_TYPE_TRAINER)) {
              bundle.putSerializable(BundleConstants.BUNDLE_USER_TYPE, UserType.TRAINER);
            }
            intent = new Intent(SignInActivity.this, MainActivity.class);
            Toast.makeText(SignInActivity.this, R.string.msg_already_signup, Toast.LENGTH_SHORT)
                .show();
          } else {
            bundle.putSerializable(BundleConstants.BUNDLE_USER_TYPE, mUserType);
            intent = new Intent(SignInActivity.this, SignUpActivity.class);
          }
          intent.putExtras(bundle);
          startActivity(intent);
          finish();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
          Log.e(TAG, "onCancelled: " + databaseError.toString());
        }
      });
    } else {
      Toast.makeText(this,
          getString(R.string.msg_login_failed), Toast.LENGTH_LONG)
          .show();
    }
  }
}
