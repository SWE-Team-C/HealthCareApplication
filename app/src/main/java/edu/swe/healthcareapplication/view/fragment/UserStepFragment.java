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
import edu.swe.healthcareapplication.model.User;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import java.util.Map;

public class UserStepFragment extends StepFragment {

  public static UserStepFragment newInstance(UserType userType) {
    UserStepFragment fragment = new UserStepFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(BundleConstants.BUNDLE_USER_TYPE, userType);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_step_user, container, false);
    Step step01 = new Step(rootView.findViewById(R.id.stub_step_01)) {
      @Override
      public void onInitialize(View inflatedView) {
        EditText editName = inflatedView.findViewById(R.id.edit_name);
        EditText editAge = inflatedView.findViewById(R.id.edt_age);
        Button btnOk = inflatedView.findViewById(R.id.btn_ok);

        btnOk.setOnClickListener(v -> {
          String name = editName.getText().toString();
          int age = Integer.parseInt(editAge.getText().toString());
          Map<String, Object> stepValueMap = getValueMap();
          stepValueMap.put("name", name);
          stepValueMap.put("age", age);
          setValueMap(stepValueMap);
          moveStep(1);
        });
      }
    };
    addStep(step01);
    Step step02 = new Step(rootView.findViewById(R.id.stub_step_02)) {
      @Override
      public void onInitialize(View inflatedView) {
        Map<String, Object> stepValueMap = getValueMap();
        String name = (String) stepValueMap.get("name");
        int age = (int) stepValueMap.get("age");
        Button btnMan = inflatedView.findViewById(R.id.btn_type_man);
        btnMan.setOnClickListener(v -> {
          User user = new User(name, age, "M");
          writeDatabase(user);
        });
        Button btnWoman = inflatedView.findViewById(R.id.btn_type_woman);
        btnWoman.setOnClickListener(v -> {
          User user = new User(name, age, "W");
          writeDatabase(user);
        });
      }
    };
    addStep(step02);
    moveStep(0);
    return rootView;
  }

  private void writeDatabase(User user) {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    if (currentUser != null) {
      String uid = currentUser.getUid();
      DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
      reference.child("users").child(uid).setValue(user);
      reference.child("user_types").child(uid).setValue("user");
    }
  }
}
