package edu.swe.healthcareapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;

public class SplashActivity extends AppCompatActivity {

  private static final String TAG = SplashActivity.class.getSimpleName();

  private FirebaseAuth mFirebaseAuth;
  private DatabaseReference mFirebaseDatabaseReference;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initFirebase();
    FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
    if (currentUser != null) {
      checkSignUpUser(mFirebaseAuth.getCurrentUser());
    } else {
      navigateTypeSelect();
    }
  }

  private void initFirebase() {
    mFirebaseAuth = FirebaseAuth.getInstance();
    mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
  }

  private void checkSignUpUser(@NonNull FirebaseUser user) {
    String uid = user.getUid();
    DatabaseReference userReference = mFirebaseDatabaseReference.child("user_types").child(uid);
    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        String value = (String) dataSnapshot.getValue();
        UserType userType = UserType.USER;
        if (value != null && value.equals(DatabaseConstants.USER_TYPE_TRAINER)) {
          userType = UserType.TRAINER;
        }
        navigateMain(userType);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: " + databaseError.toString());
      }
    });
  }

  private void navigateMain(@NonNull UserType userType) {
    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra(BundleConstants.BUNDLE_USER_TYPE, userType);
    startActivity(intent);
    finish();
  }

  private void navigateTypeSelect() {
    startActivity(new Intent(this, TypeSelectActivity.class));
    finish();
  }
}
