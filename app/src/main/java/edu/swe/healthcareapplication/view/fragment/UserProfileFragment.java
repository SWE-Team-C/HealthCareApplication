package edu.swe.healthcareapplication.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.User;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.view.EditProfileActivity;

public class UserProfileFragment extends Fragment {

  private static final String TAG = UserProfileFragment.class.getSimpleName();

  private TextView mNameView;
  private TextView mAgeView;
  private TextView mGenderView;
  private FloatingActionButton mFab;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
    mNameView = rootView.findViewById(R.id.tv_name);
    mAgeView = rootView.findViewById(R.id.tv_age);
    mGenderView = rootView.findViewById(R.id.tv_gender);
    mFab = rootView.findViewById(R.id.fab);
    mFab.setOnClickListener(view -> {
      Intent intent = new Intent(getActivity(), EditProfileActivity.class);
      intent.putExtra(BundleConstants.BUNDLE_USER_TYPE, UserType.USER);
      startActivity(intent);
    });
    return rootView;
  }

  @Override
  public void onStart() {
    super.onStart();
    readProfile();
  }

  private void readProfile() {
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    Query query = FirebaseDatabase.getInstance().getReference()
        .child(DatabaseConstants.CHILD_USERS)
        .child(uid);

    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        if (user != null) {
          mNameView.setText(user.name);
          mAgeView.setText(String.valueOf(user.age));
          mGenderView.setText(user.gender);
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: " + databaseError.toString());
      }
    });
  }
}