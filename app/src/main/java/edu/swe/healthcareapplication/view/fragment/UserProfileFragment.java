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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.User;
import edu.swe.healthcareapplication.model.UserExtra;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.util.Utils;
import edu.swe.healthcareapplication.view.EditProfileActivity;

public class UserProfileFragment extends Fragment {

  private static final String TAG = UserProfileFragment.class.getSimpleName();

  private TextView mNameView;
  private TextView mAgeView;
  private TextView mGenderView;
  private FloatingActionButton mFab;

  private TextView mHeightDescView;
  private TextView mHeightView;
  private TextView mWeightDescView;
  private TextView mWeightView;
  private TextView mBodyFatDescView;
  private TextView mBodyFatView;
  private TextView mSkeletalMuscleDescView;
  private TextView mSkeletalMuscleView;
  private TextView mLastModifiedView;
  private TextView mEmptyUserExtraView;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
    mNameView = rootView.findViewById(R.id.tv_name);
    mAgeView = rootView.findViewById(R.id.tv_age);
    mGenderView = rootView.findViewById(R.id.tv_gender);

    mHeightDescView = rootView.findViewById(R.id.tv_height_desc);
    mHeightView = rootView.findViewById(R.id.tv_height);
    mWeightDescView = rootView.findViewById(R.id.tv_weight_desc);
    mWeightView = rootView.findViewById(R.id.tv_weight);
    mBodyFatDescView = rootView.findViewById(R.id.tv_body_fat_desc);
    mBodyFatView = rootView.findViewById(R.id.tv_body_fat);
    mSkeletalMuscleDescView = rootView.findViewById(R.id.tv_skeletal_muscle_desc);
    mSkeletalMuscleView = rootView.findViewById(R.id.tv_skeletal_muscle);
    mLastModifiedView = rootView.findViewById(R.id.tv_last_modified);
    mEmptyUserExtraView = rootView.findViewById(R.id.empty_user_extra);

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
    readUserExtra();
  }

  private void readProfile() {
    String uid = fetchUid();

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

  private void readUserExtra() {
    String uid = fetchUid();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
        .child(DatabaseConstants.CHILD_USER_EXTRA);

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        DataSnapshot child = dataSnapshot.child(uid);
        if (child.exists()) {
          UserExtra userExtra = child.getValue(UserExtra.class);
          if (userExtra != null) {
            String lastModified =
                String.format(getString(R.string.last_modified_format), Utils
                    .formatTimestamp(getContext(), userExtra.lastModified));
            mLastModifiedView.setText(lastModified);
            mHeightView.setText(String.valueOf(userExtra.height));
            mWeightView.setText(String.valueOf(userExtra.weight));
            mBodyFatView.setText(String.valueOf(userExtra.bodyFat));
            mSkeletalMuscleView.setText(String.valueOf(userExtra.skeletalMuscle));
            mEmptyUserExtraView.setVisibility(View.GONE);
            setVisibilityUserExtraViews(View.VISIBLE);
          }
        } else {
          String lastModified =
              String.format(getString(R.string.last_modified_format),
                  getString(R.string.empty_last_modified));
          mLastModifiedView.setText(lastModified);
          mEmptyUserExtraView.setVisibility(View.VISIBLE);
          setVisibilityUserExtraViews(View.INVISIBLE);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.e(TAG, databaseError.toString());
      }
    });
  }

  private void setVisibilityUserExtraViews(int visibility) {
    mHeightDescView.setVisibility(visibility);
    mHeightView.setVisibility(visibility);
    mWeightDescView.setVisibility(visibility);
    mWeightView.setVisibility(visibility);
    mBodyFatDescView.setVisibility(visibility);
    mBodyFatView.setVisibility(visibility);
    mSkeletalMuscleDescView.setVisibility(visibility);
    mSkeletalMuscleView.setVisibility(visibility);
    mLastModifiedView.setVisibility(visibility);
  }

  @NonNull
  private String fetchUid() {
    return FirebaseAuth.getInstance().getCurrentUser().getUid();
  }
}