package edu.swe.healthcareapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.Trainer;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.view.adapter.SelectTrainerAdapter;
import edu.swe.healthcareapplication.view.listener.OnItemSelectListener;

public class SelectTrainerActivity extends AppCompatActivity implements
    OnItemSelectListener<String> {

  private static final String TAG = SelectTrainerActivity.class.getSimpleName();

  private DatabaseReference mFirebaseDatabaseReference;
  private RecyclerView mTrainerList;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_select_trainer);
    mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    initView();
    readTrainers();
  }

  private void initView() {
    mTrainerList = findViewById(R.id.trainer_list);

    mTrainerList.setHasFixedSize(true);
    mTrainerList.setLayoutManager(new LinearLayoutManager(this));
  }

  private void readTrainers() {
    DatabaseReference reference = mFirebaseDatabaseReference
        .child(DatabaseConstants.CHILD_TRAINERS);

    FirebaseRecyclerOptions<Trainer> options = new FirebaseRecyclerOptions.Builder<Trainer>()
        .setQuery(reference, Trainer.class)
        .setLifecycleOwner(this)
        .build();

    SelectTrainerAdapter adapter = new SelectTrainerAdapter(options);
    adapter.setOnItemSelectListener(this);
    mTrainerList.setAdapter(adapter);
  }

  @Override
  public void onItemSelected(String trainerId) {
    navigateSelectTime(trainerId);
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
