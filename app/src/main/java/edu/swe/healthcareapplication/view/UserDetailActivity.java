package edu.swe.healthcareapplication.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.User;
import edu.swe.healthcareapplication.model.UserExtra;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.util.Utils;

public class UserDetailActivity extends AppCompatActivity {

  private static final String TAG = UserDetailActivity.class.getSimpleName();

  private EditText mEditHeight;
  private EditText mEditWeight;
  private EditText mEditBodyFat;
  private EditText mEditSkeletalMuscle;
  private TextView mLastModifiedView;
  private FloatingActionButton mFab;

  private String mUserId;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_detail);
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      mUserId = bundle.getString(BundleConstants.BUNDLE_USER_ID);
    }
    initView();
  }

  @Override
  protected void onStart() {
    super.onStart();
    readUserName();
    readUserExtra();
  }

  @Override
  public boolean onSupportNavigateUp() {
    onBackPressed();
    return super.onSupportNavigateUp();
  }

  private void initView() {
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    mEditHeight = findViewById(R.id.edit_height);
    mEditWeight = findViewById(R.id.edit_weight);
    mEditBodyFat = findViewById(R.id.edit_body_fat);
    mEditSkeletalMuscle = findViewById(R.id.edit_skeletal_muscle);
    mLastModifiedView = findViewById(R.id.tv_last_modified);
    mFab = findViewById(R.id.fab);

    mFab.setOnClickListener(v -> writeUserExtra());
  }

  private void readUserName() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
        .child(DatabaseConstants.CHILD_USERS)
        .child(mUserId);

    reference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        if (user != null) {
          setTitle(user.name);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.e(TAG, databaseError.toString());
      }
    });
  }

  private void readUserExtra() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
        .child(DatabaseConstants.CHILD_USER_EXTRA);

    reference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        DataSnapshot child = dataSnapshot.child(mUserId);
        if (child.exists()) {
          UserExtra userExtra = child.getValue(UserExtra.class);
          if (userExtra != null) {
            String lastModified =
                String.format(getString(R.string.last_modified_format), Utils
                    .formatTimestamp(UserDetailActivity.this, userExtra.lastModified));
            mLastModifiedView.setText(lastModified);
            mEditHeight.setText(String.valueOf(userExtra.height));
            mEditWeight.setText(String.valueOf(userExtra.weight));
            mEditBodyFat.setText(String.valueOf(userExtra.bodyFat));
            mEditSkeletalMuscle.setText(String.valueOf(userExtra.skeletalMuscle));
          }
        } else {
          String lastModified =
              String.format(getString(R.string.last_modified_format),
                  getString(R.string.empty_last_modified));
          mLastModifiedView.setText(lastModified);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.e(TAG, databaseError.toString());
      }
    });
  }

  private void writeUserExtra() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
        .child(DatabaseConstants.CHILD_USER_EXTRA)
        .child(mUserId);

    long lastModified = System.currentTimeMillis();
    String height = mEditHeight.getText().toString();
    String weight = mEditWeight.getText().toString();
    String bodyFat = mEditBodyFat.getText().toString();
    String skeletalMuscle = mEditSkeletalMuscle.getText().toString();

    if (TextUtils.isEmpty(height) ||
        TextUtils.isEmpty(weight) || TextUtils.isEmpty(bodyFat) || TextUtils
        .isEmpty(skeletalMuscle)) {
      Toast.makeText(this, R.string.msg_user_extra_empty, Toast.LENGTH_SHORT).show();
      return;
    }

    UserExtra userExtra = new UserExtra(lastModified,
        Integer.parseInt(height),
        Double.parseDouble(weight),
        Double.parseDouble(bodyFat),
        Double.parseDouble(skeletalMuscle));

    Task<Void> task = reference.setValue(userExtra);
    task.addOnCompleteListener(task1 -> {
      Toast.makeText(this, R.string.msg_user_extra_saved, Toast.LENGTH_SHORT).show();
      finish();
    });
    task.addOnFailureListener(
        e -> Toast.makeText(this, R.string.msg_user_extra_failed, Toast.LENGTH_SHORT).show());
  }
}
