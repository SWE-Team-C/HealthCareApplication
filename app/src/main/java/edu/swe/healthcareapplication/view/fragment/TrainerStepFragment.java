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
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import java.util.Arrays;

public class TrainerStepFragment extends StepFragment {

  public static TrainerStepFragment newInstance(UserType userType) {
    TrainerStepFragment fragment = new TrainerStepFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(BundleConstants.BUNDLE_USER_TYPE, userType);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_step_trainer, container, false);
    Step step01 = new Step(rootView.findViewById(R.id.layout_step_01)) {
      @Override
      public void initialize() {
        EditText editName = stepView.findViewById(R.id.edit_name);
        Button btnOk = stepView.findViewById(R.id.btn_ok);

        btnOk.setOnClickListener(v -> {
          String name = editName.getText().toString();
          valueMap.put("name", name);
          moveStep(1);
        });
      }
    };
    addStep(step01);
    Step step02 = new Step(rootView.findViewById(R.id.layout_step_02)) {
      @Override
      public void initialize() {
        Button btnUploadProfile = stepView.findViewById(R.id.btn_upload_profile);
        EditText editEducation = stepView.findViewById(R.id.edit_education);
        EditText editAwards = stepView.findViewById(R.id.edit_awards);
        Button btnOk = stepView.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(v -> {
          String education = editEducation.getText().toString();
          String awards = editAwards.getText().toString();
          String name = (String) valueMap.get("name");
          Trainer trainer = new Trainer(name, education, Arrays.asList(awards));
          writeDatabase(trainer);
        });
      }
    };
    addStep(step02);
    moveStep(0);
    return rootView;
  }

  private void writeDatabase(Trainer trainer) {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    if (currentUser != null) {
      String uid = currentUser.getUid();
      FirebaseDatabase database = FirebaseDatabase.getInstance();

      DatabaseReference reference = database.getReference();
      reference.child("trainers").child(uid).setValue(trainer);
      reference.child("user_types").child(uid).setValue("trainer");
    }
  }
}
