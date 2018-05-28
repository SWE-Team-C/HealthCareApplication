package edu.swe.healthcareapplication.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.Trainer;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.view.adapter.AwardAdapter;
import java.util.List;

public class TrainerEditProfileFragment extends Fragment {

  private static final String TAG = TrainerEditProfileFragment.class.getSimpleName();

  private EditText mEditName;
  private EditText mEditEducation;
  private EditText mEditAward;
  private ImageButton mBtnAddAward;
  private RecyclerView mAwardsList;
  private FloatingActionButton mFab;
  private AwardAdapter mAdapter;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_trainer_edit_profile, container, false);
    mEditName = rootView.findViewById(R.id.edit_name);
    mEditEducation = rootView.findViewById(R.id.edit_education);
    mEditAward = rootView.findViewById(R.id.edit_awards);
    mBtnAddAward = rootView.findViewById(R.id.btn_add_award);
    mAwardsList = rootView.findViewById(R.id.awards_list);
    mFab = rootView.findViewById(R.id.fab);

    mBtnAddAward.setOnClickListener(v -> {
      String award = mEditAward.getText().toString();

      if (TextUtils.isEmpty(award)) {
        return;
      }

      mAdapter.addAward(award);
    });

    mAwardsList.setHasFixedSize(true);
    mAwardsList.setLayoutManager(new LinearLayoutManager(getContext()));
    mAdapter = new AwardAdapter();
    mAwardsList.setAdapter(mAdapter);

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
        .child(DatabaseConstants.CHILD_TRAINERS)
        .child(uid);

    reference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Trainer trainer = dataSnapshot.getValue(Trainer.class);
        if (trainer != null) {
          mEditName.setText(trainer.name);
          mEditEducation.setText(trainer.education);

          if (!trainer.awards.isEmpty()) {
            for (String award : trainer.awards) {
              mAdapter.addAward(award);
            }
          }
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
        .child(DatabaseConstants.CHILD_TRAINERS)
        .child(uid);

    String name = mEditName.getText().toString();
    String education = mEditEducation.getText().toString();
    List<String> awards = mAdapter.getAwardList();

    if (TextUtils.isEmpty(name)) {
      return;
    }

    if (TextUtils.isEmpty(education)) {
      return;
    }

    if (awards.isEmpty()) {
      awards.add(getString(R.string.empty_item));
    }

    Trainer trainer = new Trainer(name, education, awards);
    Task<Void> task = reference.setValue(trainer);
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
