package edu.swe.healthcareapplication.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.Trainer;
import edu.swe.healthcareapplication.view.adapter.SelectTrainerAdapter;
import java.util.ArrayList;
import java.util.List;

public class SelectTrainerActivity extends AppCompatActivity {

  private static final String TAG = SelectTrainerActivity.class.getSimpleName();

  private RecyclerView mTrainerList;
  private SelectTrainerAdapter mAdapter;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_select_trainer);
    initView();
    readDatabase();
  }

  private void initView() {
    mAdapter = new SelectTrainerAdapter();
    mAdapter.setOnItemSelectListener(trainer -> {
      Toast.makeText(this, trainer.name + " selected", Toast.LENGTH_SHORT).show();
    });
    mTrainerList = findViewById(R.id.trainer_list);
    mTrainerList.setHasFixedSize(true);
    mTrainerList.setLayoutManager(new LinearLayoutManager(this));
    mTrainerList.setAdapter(mAdapter);
  }

  private void readDatabase() {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference().child("trainers");
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> trainersSnapshot = dataSnapshot.getChildren();
        List<Trainer> trainerList = new ArrayList<>();
        for (DataSnapshot trainerSnapshot : trainersSnapshot) {
          Trainer trainer = trainerSnapshot.getValue(Trainer.class);
          trainerList.add(trainer);
        }
        mAdapter.setTrainerList(trainerList);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: " + databaseError.toString());
      }
    });
  }
}
