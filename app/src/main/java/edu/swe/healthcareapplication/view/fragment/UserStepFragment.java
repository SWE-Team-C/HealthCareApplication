package edu.swe.healthcareapplication.view.fragment;

import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.User;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.util.step.DataManager;
import edu.swe.healthcareapplication.util.step.Navigator;
import java.util.Map;

public class UserStepFragment extends Fragment {

  private int mStepPosition;

  private EditText mEditName;
  private EditText mEditAge;
  private Button mBtnOk;

  private Button mBtnTypeMan;
  private Button mBtnTypeWoman;

  public static UserStepFragment newInstance(int stepPosition) {
    UserStepFragment fragment = new UserStepFragment();
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
      rootView = inflater.inflate(R.layout.step_user_01, container, false);
      mEditName = rootView.findViewById(R.id.edit_name);
      mEditAge = rootView.findViewById(R.id.edit_age);
      mBtnOk = rootView.findViewById(R.id.btn_ok);

      mEditName.setText(getUserDisplayName());
      mBtnOk.setOnClickListener(v -> {
        Context context = getContext();
        if (context instanceof DataManager && context instanceof Navigator) {
          DataManager dataManager = (DataManager) context;
          Navigator navigator = (Navigator) context;
          String name = mEditName.getText().toString();
          String age = mEditAge.getText().toString();
          if (TextUtils.isEmpty(name)) {
            // Error message
            return;
          }
          if (TextUtils.isEmpty(age)) {
            // Error message
            return;
          }
          if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(age)) {
            dataManager.saveData("name", name);
            dataManager.saveData("age", Integer.parseInt(age));
            navigator.nextStep();
          }
        }
      });
    } else if (mStepPosition == 1) {
      rootView = inflater.inflate(R.layout.step_user_02, container, false);
      mBtnTypeMan = rootView.findViewById(R.id.btn_type_man);
      mBtnTypeWoman = rootView.findViewById(R.id.btn_type_woman);

      Context context = getContext();
      Navigator navigator = (Navigator) context;
      DataManager dataManager = (DataManager) context;
      mBtnTypeMan.setOnClickListener(v -> {
        if (dataManager != null && navigator != null) {
          dataManager.saveData("gender", "M");
          writeDatabase(dataManager.getAllData());
          navigator.completeStep();
        }
      });
      mBtnTypeWoman.setOnClickListener(v -> {
        if (dataManager != null && navigator != null) {
          dataManager.saveData("gender", "W");
          writeDatabase(dataManager.getAllData());
          navigator.completeStep();
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

  private void writeDatabase(Map<String, Object> userData) {
    String name = (String) userData.get("name");
    int age = (int) userData.get("age");
    String gender = (String) userData.get("gender");
    User user = new User(name, age, gender);

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    if (currentUser != null) {
      String uid = currentUser.getUid();
      DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
      reference.child(DatabaseConstants.CHILD_USERS).child(uid).setValue(user);
      reference.child(DatabaseConstants.CHILD_USER_TYPES).child(uid)
          .setValue(DatabaseConstants.USER_TYPE_USER);
    }
  }
}
