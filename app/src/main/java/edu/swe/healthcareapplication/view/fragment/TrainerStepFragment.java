package edu.swe.healthcareapplication.view.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.Trainer;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.util.step.DataManager;
import edu.swe.healthcareapplication.util.step.Navigator;
import java.util.Collections;
import java.util.Map;

public class TrainerStepFragment extends Fragment {

  public static final int RC_LOAD_IMAGE = 101;

  private int mStepPosition;

  private EditText mEditName;
  private Button mBtnOk;

  private EditText mEditEducation;
  private EditText mEditAwards;

  public static TrainerStepFragment newInstance(int stepPosition) {
    TrainerStepFragment fragment = new TrainerStepFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(BundleConstants.BUNDLE_STEP_POSITION, stepPosition);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle != null) {
      mStepPosition = bundle.getInt(BundleConstants.BUNDLE_STEP_POSITION);
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = null;
    if (mStepPosition == 0) {
      rootView = inflater.inflate(R.layout.step_trainer_01, container, false);
      mEditName = rootView.findViewById(R.id.edit_name);
      mBtnOk = rootView.findViewById(R.id.btn_ok);

      mEditName.setText(getUserDisplayName());
      mBtnOk.setOnClickListener(v -> {
        Context context = getContext();
        if (context instanceof DataManager && context instanceof Navigator) {
          DataManager dataManager = (DataManager) context;
          Navigator navigator = (Navigator) context;
          String name = mEditName.getText().toString();
          if (TextUtils.isEmpty(name)) {
            // Error message
          } else {
            dataManager.saveData("name", name);
            navigator.nextStep();
          }
        }
      });
    } else if (mStepPosition == 1) {
      rootView = inflater.inflate(R.layout.step_trainer_02, container, false);
      mEditEducation = rootView.findViewById(R.id.edit_education);
      mEditAwards = rootView.findViewById(R.id.edit_awards);
      mBtnOk = rootView.findViewById(R.id.btn_ok);

      mBtnOk.setOnClickListener(v -> {
        Context context = getContext();
        if (context instanceof DataManager && context instanceof Navigator) {
          DataManager dataManager = (DataManager) context;
          Navigator navigator = (Navigator) context;
          String education = mEditEducation.getText().toString();
          String awards = mEditAwards.getText().toString();
          if (TextUtils.isEmpty(education)) {
            // Error message
          }
          if (TextUtils.isEmpty(awards)) {

          }
          if (!TextUtils.isEmpty(education) && !TextUtils.isEmpty(awards)) {
            dataManager.saveData("education", education);
            dataManager.saveData("awards", Integer.parseInt(awards));
            writeDatabase(dataManager.getAllData());
            navigator.completeStep();
          }
        }
      });
    }
    return rootView;
  }

  @Nullable
  private String getUserDisplayName() {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    if (currentUser != null) {
      return currentUser.getDisplayName();
    }
    return null;
  }

  private void writeDatabase(Map<String, Object> trainerData) {
    String name = (String) trainerData.get("name");
    String education = (String) trainerData.get("education");
    String awards = (String) trainerData.get("awards");
    Trainer trainer = new Trainer(name, education, Collections.singletonList(awards));

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    if (currentUser != null) {
      String uid = currentUser.getUid();
      FirebaseDatabase database = FirebaseDatabase.getInstance();

      DatabaseReference reference = database.getReference();
      reference.child(DatabaseConstants.CHILD_TRAINERS).child(uid).setValue(trainer);
      reference.child(DatabaseConstants.CHILD_USER_TYPES).child(uid)
          .setValue(DatabaseConstants.USER_TYPE_TRAINER);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_LOAD_IMAGE && resultCode == RESULT_OK) {
      Toast.makeText(getActivity(), "Selected Image " + data.getData(), Toast.LENGTH_LONG).show();
    }
  }
}
