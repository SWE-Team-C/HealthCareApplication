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
import edu.swe.healthcareapplication.model.Trainer;
import edu.swe.healthcareapplication.model.User;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.util.ViewBinder;
import edu.swe.healthcareapplication.view.EditProfileActivity;

public class ProfileFragment extends Fragment {

  private static final String TAG = ProfileFragment.class.getSimpleName();

  private UserType mUserType;
  private ViewBinder mViewBinder;
  private FloatingActionButton mFab;

  public static ProfileFragment newInstance(UserType userType) {
    ProfileFragment instance = new ProfileFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(BundleConstants.BUNDLE_USER_TYPE, userType);
    instance.setArguments(bundle);
    return instance;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle != null) {
      mUserType = (UserType) bundle.getSerializable(BundleConstants.BUNDLE_USER_TYPE);
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
    if (mUserType == UserType.USER) {
      ((TextView) rootView.findViewById(R.id.tv_item1_desc)).setText(R.string.hint_name);
      ((TextView) rootView.findViewById(R.id.tv_item2_desc)).setText(R.string.hint_age);
      ((TextView) rootView.findViewById(R.id.tv_item3_desc)).setText(R.string.hint_gender);
      mViewBinder = new UserViewBinder(rootView);
    } else if (mUserType == UserType.TRAINER) {
      ((TextView) rootView.findViewById(R.id.tv_item1_desc)).setText(R.string.hint_name);
      ((TextView) rootView.findViewById(R.id.tv_item2_desc)).setText(R.string.hint_education);
      ((TextView) rootView.findViewById(R.id.tv_item3_desc)).setText(R.string.hint_awards);
      mViewBinder = new TrainerViewBinder(rootView);
    }
    mFab = rootView.findViewById(R.id.fab);
    mFab.setOnClickListener(view -> {
      Intent intent = new Intent(getActivity(), EditProfileActivity.class);
      intent.putExtra(BundleConstants.BUNDLE_USER_TYPE, mUserType);
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
    if (mUserType == UserType.USER) {
      readUser();
    } else if (mUserType == UserType.TRAINER) {
      readTrainer();
    }
  }

  private void readTrainer() {
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    Query query = FirebaseDatabase.getInstance().getReference()
        .child(DatabaseConstants.CHILD_TRAINERS)
        .child(uid);

    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Trainer trainer = dataSnapshot.getValue(Trainer.class);
        ((TrainerViewBinder) mViewBinder).bind(trainer);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: " + databaseError.toString());
      }
    });
  }

  private void readUser() {
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    Query query = FirebaseDatabase.getInstance().getReference()
        .child(DatabaseConstants.CHILD_USERS)
        .child(uid);

    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        ((UserViewBinder) mViewBinder).bind(user);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: " + databaseError.toString());
      }
    });
  }

  protected class UserViewBinder extends ViewBinder<User> {

    private TextView mNameView;
    private TextView mAgeView;
    private TextView mGenderView;

    public UserViewBinder(View rootView) {
      super(rootView);
      mNameView = rootView.findViewById(R.id.tv_item1);
      mAgeView = rootView.findViewById(R.id.tv_item2);
      mGenderView = rootView.findViewById(R.id.tv_item3);
    }

    @Override
    public void bind(User user) {
      mNameView.setText(user.name);
      mAgeView.setText(String.valueOf(user.age));
      mGenderView.setText(user.gender);
    }
  }

  protected class TrainerViewBinder extends ViewBinder<Trainer> {

    private TextView mNameView;
    private TextView mEducationView;
    private TextView mAwardsView;

    public TrainerViewBinder(View rootView) {
      super(rootView);
      mNameView = rootView.findViewById(R.id.tv_item1);
      mEducationView = rootView.findViewById(R.id.tv_item2);
      mAwardsView = rootView.findViewById(R.id.tv_item3);
    }

    @Override
    public void bind(Trainer trainer) {
      mNameView.setText(trainer.name);
      mEducationView.setText(trainer.education);
      mAwardsView.setText("");
      for (String award : trainer.awards) {
        mAwardsView.append(award + '\n');
      }
    }
  }
}