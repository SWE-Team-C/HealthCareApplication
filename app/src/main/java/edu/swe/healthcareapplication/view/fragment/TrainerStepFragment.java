package edu.swe.healthcareapplication.view.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import edu.swe.healthcareapplication.view.adapter.CertificateAdapter;
import java.util.List;
import java.util.Map;

public class TrainerStepFragment extends Fragment {

  public static final int RC_LOAD_IMAGE = 101;

  private int mStepPosition;

  private EditText mEditName;
  private Button mBtnOk;

  private EditText mEditEducation;
  private EditText mEditCertificates;
  private RecyclerView mCertificateList;
  private ImageButton mBtnAddCertificate;
  private CertificateAdapter mCertificateAdapter;

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
      mEditCertificates = rootView.findViewById(R.id.edit_certificate);
      mCertificateList = rootView.findViewById(R.id.certificate_list);
      mBtnAddCertificate = rootView.findViewById(R.id.btn_add_certificate);
      mBtnOk = rootView.findViewById(R.id.btn_ok);

      mCertificateList.setLayoutManager(new LinearLayoutManager(getActivity()));
      mCertificateList.setHasFixedSize(true);

      mCertificateAdapter = new CertificateAdapter();
      mCertificateList.setAdapter(mCertificateAdapter);

      mBtnAddCertificate.setOnClickListener(v -> {
        String award = mEditCertificates.getText().toString();
        if (!TextUtils.isEmpty(award)) {
          mCertificateAdapter.addCertificate(award);
          mEditCertificates.setText("");
        }
      });

      mBtnOk.setOnClickListener(v -> {
        Context context = getContext();
        if (context instanceof DataManager && context instanceof Navigator) {
          DataManager dataManager = (DataManager) context;
          Navigator navigator = (Navigator) context;
          String education = mEditEducation.getText().toString();
          if (TextUtils.isEmpty(education)) {
            // Error message
          }
          if (!TextUtils.isEmpty(education)) {
            List<String> awards = mCertificateAdapter.getCertificateList();
            dataManager.saveData("education", education);
            dataManager.saveData("certificates", awards);
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
    List<String> awards = (List<String>) trainerData.get("certificates");
    Trainer trainer = new Trainer(name, education, awards);

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
