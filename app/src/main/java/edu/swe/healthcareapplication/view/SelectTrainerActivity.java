package edu.swe.healthcareapplication.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.databinding.ActivitySelectTrainerBinding;
import edu.swe.healthcareapplication.model.Trainer;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.view.adapter.SelectTrainerAdapter;
import edu.swe.healthcareapplication.view.listener.OnItemSelectListener;
import java.util.ArrayList;
import java.util.List;

public class SelectTrainerActivity extends AppCompatActivity implements
    OnItemSelectListener<Pair<String, Trainer>> {

  private static final String TAG = SelectTrainerActivity.class.getSimpleName();

  private SelectTrainerAdapter mAdapter;
  private DatabaseReference mFirebaseDatabaseReference;

  private ActivitySelectTrainerBinding mBinding;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_trainer);
    mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    initView();
    readTrainers();
  }

  private void initView() {
    mAdapter = new SelectTrainerAdapter();
    mAdapter.setOnItemSelectListener(this);
    mBinding.trainerList.setHasFixedSize(true);
    mBinding.trainerList.setLayoutManager(new LinearLayoutManager(this));
    mBinding.trainerList.setAdapter(mAdapter);
  }

  private void readTrainers() {
    DatabaseReference reference = mFirebaseDatabaseReference
        .child(DatabaseConstants.CHILD_TRAINERS);
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> trainersSnapshot = dataSnapshot.getChildren();
        List<Pair<String, Trainer>> trainerList = new ArrayList<>();
        for (DataSnapshot trainerSnapshot : trainersSnapshot) {
          String trainerId = trainerSnapshot.getKey();
          Trainer trainer = trainerSnapshot.getValue(Trainer.class);
          trainerList.add(new Pair<>(trainerId, trainer));
        }
        mAdapter.setTrainerList(trainerList);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: " + databaseError.toString());
      }
    });
  }

  @Override
  public void onItemSelected(Pair<String, Trainer> trainerPair) {
    Trainer trainer = trainerPair.second;
    Toast.makeText(this, trainer.name + " selected", Toast.LENGTH_SHORT).show();
    navigateSelectTime(trainerPair.first);
  }

  private void navigateSelectTime(String trainerId) {
    Intent intent = new Intent(this, SelectTimeActivity.class);
    Bundle bundle = new Bundle();
    bundle.putCharSequence(BundleConstants.BUNDLE_TRAINER_ID, trainerId);
    intent.putExtras(bundle);
    startActivity(intent);
    finish();
  }
}
