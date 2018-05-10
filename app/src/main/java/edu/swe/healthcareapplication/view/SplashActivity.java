package edu.swe.healthcareapplication.view;

import android.content.Intent;
import android.os.Bundle;
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

public class SplashActivity extends AppCompatActivity {

  private static final String TAG = SplashActivity.class.getSimpleName();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    if (currentUser != null) {
      String uid = currentUser.getUid();
      FirebaseDatabase database = FirebaseDatabase.getInstance();
      DatabaseReference usersReference = database.getReference().child("user_types").child(uid);
      usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          String userType = (String) dataSnapshot.getValue();
          Intent intent;
          if (userType != null) {
            Bundle bundle = new Bundle();
            if (userType.equals("user")) {
              bundle.putSerializable(BundleConstants.BUNDLE_USER_TYPE, UserType.USER);
            } else if (userType.equals("trainer")) {
              bundle.putSerializable(BundleConstants.BUNDLE_USER_TYPE, UserType.TRAINER);
            }
            intent = new Intent(SplashActivity.this, TimeTableActivity.class);
            intent.putExtras(bundle);
          } else {
            intent = new Intent(SplashActivity.this, TypeSelectActivity.class);
          }
          startActivity(intent);
          finish();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
          Log.e(TAG, "onCancelled: " + databaseError.toString());
        }
      });
    } else {
      startActivity(new Intent(this, TypeSelectActivity.class));
      finish();
    }
  }
}
