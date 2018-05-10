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
    Step step01 = new Step(rootView.findViewById(R.id.layout_step_01)) {
      @Override
      public void initialize() {
        EditText editName = stepView.findViewById(R.id.edit_name);
        EditText editAge = stepView.findViewById(R.id.edt_age);
        Button btnOk = stepView.findViewById(R.id.btn_ok);

        btnOk.setOnClickListener(v -> {
          String name = editName.getText().toString();
          int age = Integer.parseInt(editAge.getText().toString());
          valueMap.put("name", name);
          valueMap.put("age", age);
          moveStep(1);
        });
      }
    };
    addStep(step01);
    Step step02 = new Step(rootView.findViewById(R.id.layout_step_02)) {
      @Override
      public void initialize() {
        String name = (String) valueMap.get("name");
        int age = (int) valueMap.get("age");
        Button btnMan = stepView.findViewById(R.id.btn_type_man);
        btnMan.setOnClickListener(v -> {
          User user = new User(name, age, "M");
          writeDatabase(user);
        });
        Button btnWoman = stepView.findViewById(R.id.btn_type_woman);
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
