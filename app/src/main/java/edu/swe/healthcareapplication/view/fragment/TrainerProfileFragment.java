package edu.swe.healthcareapplication.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.Trainer;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.view.EditProfileActivity;
import edu.swe.healthcareapplication.view.UserManageActivity;

public class TrainerProfileFragment extends Fragment {

  private static final String TAG = TrainerProfileFragment.class.getSimpleName();

  private TextView mNameView;
  private TextView mEducationView;
  private TextView mCertificatesView;
  private Button mBtnEditProfile;
  private Button mBtnUserManage;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_trainer_profile, container, false);
    mNameView = rootView.findViewById(R.id.tv_name);
    mEducationView = rootView.findViewById(R.id.tv_education);
    mCertificatesView = rootView.findViewById(R.id.tv_certificates);
    mBtnEditProfile = rootView.findViewById(R.id.btn_edit_profile);
    mBtnUserManage = rootView.findViewById(R.id.btn_user_manage);

    mBtnEditProfile.setOnClickListener(view -> {
      Intent intent = new Intent(getActivity(), EditProfileActivity.class);
      intent.putExtra(BundleConstants.BUNDLE_USER_TYPE, UserType.TRAINER);
      startActivity(intent);
    });

    mBtnUserManage.setOnClickListener(view -> {
      Intent intent = new Intent(getActivity(), UserManageActivity.class);
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
        .child(DatabaseConstants.CHILD_TRAINERS)
        .child(uid);

    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Trainer trainer = dataSnapshot.getValue(Trainer.class);
        if (trainer != null) {
          mNameView.setText(trainer.name);
          mEducationView.setText(trainer.education);
          if (trainer.certificates == null) {
            mCertificatesView.setText(R.string.empty_item);
          } else {
            mCertificatesView.setText("");
            for (String award : trainer.certificates) {
              mCertificatesView.append(award + "\n");
            }
          }
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: " + databaseError.toString());
      }
    });
  }
}