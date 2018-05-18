package edu.swe.healthcareapplication.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.Step;
import edu.swe.healthcareapplication.model.Trainer;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import java.util.Collections;
import java.util.Map;

public class TrainerStepFragment extends StepFragment {

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_step_trainer, container, false);
    Step step01 = new Step(rootView.findViewById(R.id.stub_step_01)) {
      @Override
      protected void onInitialize(View inflatedView) {
        EditText editName = inflatedView.findViewById(R.id.edit_name);
        Button btnOk = inflatedView.findViewById(R.id.btn_ok);

        String userDisplayName = getUserDisplayName();
        if (userDisplayName != null) {
          editName.setText(userDisplayName);
        }

        btnOk.setOnClickListener(v -> {
          String name = editName.getText().toString();
          Map<String, Object> stepValueMap = getValueMap();
          stepValueMap.put("name", name);
          setValueMap(stepValueMap);
          moveStep(1);
        });
      }
    };
    addStep(step01);
    Step step02 = new Step(rootView.findViewById(R.id.stub_step_02)) {
      @Override
      protected void onInitialize(View inflatedView) {
        Button btnUploadProfile = inflatedView.findViewById(R.id.btn_upload_profile);
        EditText editEducation = inflatedView.findViewById(R.id.edit_education);
        EditText editAwards = inflatedView.findViewById(R.id.edit_awards);
        Button btnOk = inflatedView.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(v -> {
          String education = editEducation.getText().toString();
          String awards = editAwards.getText().toString();
          String name = (String) getValueMap().get("name");

          Trainer trainer = new Trainer(name, education, Collections.singletonList(awards));
          writeDatabase(trainer);
        });
      }
    };
    addStep(step02);
    moveStep(0);
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

  private void writeDatabase(Trainer trainer) {
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
}
