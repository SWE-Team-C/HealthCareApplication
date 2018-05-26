package edu.swe.healthcareapplication.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.User;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

  private static final String TAG = EditProfileActivity.class.getSimpleName();

  private EditText mEditName;
  private EditText mEditAge;
  private EditText mEditGender;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_profile);
    initView();
    readProfile();
  }

  private void initView() {
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    mEditName = findViewById(R.id.edit_name);
    mEditAge = findViewById(R.id.edit_age);
    mEditGender = findViewById(R.id.edit_gender);

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(v -> {
          writeProfile();
          finish();
        }
    );
  }

  @Override
  public boolean onSupportNavigateUp() {
    onBackPressed();
    return super.onSupportNavigateUp();
  }

  private void readProfile() {
    String uid = getUserUid();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
        .child(DatabaseConstants.CHILD_USERS)
        .child(uid);

    reference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        mEditName.setText(user.name);
        mEditAge.setText(String.valueOf(user.age));
        mEditGender.setText(user.gender);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: " + databaseError.toString());
      }
    });
  }

  private void writeProfile() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String uid = getUserUid();
    User user = new User(mEditName.getText().toString(),
        Integer.parseInt(mEditAge.getText().toString()),
        mEditGender.getText().toString());
    Map<String, Object> userValues = user.toMap();
    Map<String, Object> childUpdates = new HashMap<>();
    childUpdates.put("/users/" + uid, userValues);
    reference.updateChildren(childUpdates);

    Toast.makeText(this, R.string.msg_profile_saved, Toast.LENGTH_SHORT).show();
  }

  private String getUserUid() {
    return FirebaseAuth.getInstance().getCurrentUser().getUid();
  }
}
