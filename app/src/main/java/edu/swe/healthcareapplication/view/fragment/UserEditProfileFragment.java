package edu.swe.healthcareapplication.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.User;
import edu.swe.healthcareapplication.util.DatabaseConstants;

public class UserEditProfileFragment extends Fragment {

  private static final String TAG = UserEditProfileFragment.class.getSimpleName();

  private EditText mEditName;
  private EditText mEditAge;
  private EditText mEditGender;
  private FloatingActionButton mFab;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_user_edit_profile, container, false);
    mEditName = rootView.findViewById(R.id.edit_name);
    mEditAge = rootView.findViewById(R.id.edit_age);
    mEditGender = rootView.findViewById(R.id.edit_gender);
    mFab = rootView.findViewById(R.id.fab);

    mFab.setOnClickListener(v -> writeProfile());
    return rootView;
  }

  @Override
  public void onStart() {
    super.onStart();
    readProfile();
  }

  private void readProfile() {
    String uid = fetchUid();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
        .child(DatabaseConstants.CHILD_USERS)
        .child(uid);

    reference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        if (user != null) {
          mEditName.setText(user.name);
          mEditAge.setText(String.valueOf(user.age));
          mEditGender.setText(user.gender);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.e(TAG, databaseError.toString());
      }
    });
  }

  private void writeProfile() {
    String uid = fetchUid();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
        .child(DatabaseConstants.CHILD_USERS)
        .child(uid);

    String name = mEditName.getText().toString();
    String age = mEditAge.getText().toString();
    String gender = mEditGender.getText().toString();

    if (TextUtils.isEmpty(name)) {
      return;
    }

    if (TextUtils.isEmpty(age)) {
      return;
    }

    User user = new User(name, Integer.parseInt(age), gender);
    Task<Void> task = reference.setValue(user);
    task.addOnCompleteListener(task1 -> {
      Toast.makeText(getContext(), R.string.msg_profile_saved, Toast.LENGTH_SHORT).show();
      getActivity().finish();
    });
    task.addOnFailureListener(
        e -> Toast.makeText(getContext(), R.string.msg_profile_save_failed, Toast.LENGTH_SHORT)
            .show());
  }

  @NonNull
  private String fetchUid() {
    return FirebaseAuth.getInstance().getCurrentUser().getUid();
  }
}
