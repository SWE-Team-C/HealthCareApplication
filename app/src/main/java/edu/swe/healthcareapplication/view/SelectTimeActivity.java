package edu.swe.healthcareapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.ChatChannel;
import edu.swe.healthcareapplication.model.TimeTable;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.view.adapter.SelectTimeAdapter;
import java.util.ArrayList;
import java.util.List;

public class SelectTimeActivity extends AppCompatActivity {

  private static final String TAG = SelectTimeActivity.class.getSimpleName();

  private DatabaseReference mFirebaseDatabaseReference;
  private FirebaseAuth mFirebaseAuth;
  private CoordinatorLayout mRootLayout;
  private SelectTimeAdapter mAdapter;
  private String mTrainerId;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_select_time);
    mFirebaseAuth = FirebaseAuth.getInstance();
    mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    initView();
  }

  @Override
  protected void onStart() {
    super.onStart();
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      mTrainerId = bundle.getString(BundleConstants.BUNDLE_TRAINER_ID);
    } else {
      Log.e(TAG, "onStart: Unknown trainer id");
    }
    if (mTrainerId != null) {
      readTrainerTimeTable(mTrainerId, 0);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.select_time_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_complete:
        writeChatChannel(mTrainerId);
        navigateMain();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void initView() {
    setSupportActionBar(findViewById(R.id.toolbar));
    mRootLayout = findViewById(R.id.coordinator_layout);
    String[] dateStrings = getResources().getStringArray(R.array.date);
    TabLayout tabLayout = findViewById(R.id.tab_layout);
    for (int index = 0; index < 7; index++) {
      Tab tab = tabLayout.newTab().setText(dateStrings[index]);
      if (index == 0) {
        tab.select();
      }
      tabLayout.addTab(tab);
    }
    tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
      @Override
      public void onTabSelected(Tab tab) {
        readTrainerTimeTable(mTrainerId, tab.getPosition());
      }

      @Override
      public void onTabUnselected(Tab tab) {
        // No-op
      }

      @Override
      public void onTabReselected(Tab tab) {
        // No-op
      }
    });

    mAdapter = new SelectTimeAdapter();
    RecyclerView trainerList = findViewById(R.id.timetable_list);
    trainerList.setLayoutManager(new LinearLayoutManager(this));
    trainerList.setHasFixedSize(true);
    trainerList.setAdapter(mAdapter);

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(v -> {
      int position = tabLayout.getSelectedTabPosition();
      writeSelectedTimeTable(mTrainerId, mAdapter.getSelectedKeyList(), position);
    });
  }

  private void readTrainerTimeTable(@NonNull String trainerId, int dateIndex) {
    DatabaseReference reference = mFirebaseDatabaseReference
        .child(DatabaseConstants.CHILD_TIMETABLE)
        .child(trainerId)
        .child(String.valueOf(dateIndex));
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> timeTablesSnapshot = dataSnapshot.getChildren();
        List<Pair<String, TimeTable>> timeTableList = new ArrayList<>();
        for (DataSnapshot timeTableSnapshot : timeTablesSnapshot) {
          Pair<String, TimeTable> timeTablePair = new Pair<>(timeTableSnapshot.getKey(),
              timeTableSnapshot.getValue(TimeTable.class));
          timeTableList.add(timeTablePair);
        }
        mAdapter.setTimeTableList(timeTableList);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: " + databaseError.toString());
      }
    });
  }

  private void writeSelectedTimeTable(@NonNull String trainerId,
      @NonNull List<String> selectedKey,
      int dateIndex) {
    if (selectedKey.size() == 0) {
      Snackbar.make(mRootLayout, R.string.msg_timetable_not_selected, Snackbar.LENGTH_SHORT).show();
      return;
    }

    DatabaseReference reference = mFirebaseDatabaseReference
        .child(DatabaseConstants.CHILD_TIMETABLE)
        .child(trainerId)
        .child(String.valueOf(dateIndex));

    for (String key : selectedKey) {
      reference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          TimeTable timeTable = dataSnapshot.getValue(TimeTable.class);
          FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
          if (timeTable != null && currentUser != null) {
            timeTable.selectedUserId = currentUser.getUid();
          }
          reference.child(key).setValue(timeTable);

          Snackbar.make(mRootLayout, R.string.msg_timetable_saved, Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
          Log.e(TAG, "onCancelled: " + databaseError.toString());
        }
      });
    }
  }

  private void writeChatChannel(@NonNull String trainerId) {
    FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
    if (currentUser != null) {
      String userUid = currentUser.getUid();
      ChatChannel channel = new ChatChannel(userUid, trainerId);
      DatabaseReference reference = mFirebaseDatabaseReference
          .child(DatabaseConstants.CHILD_CHAT_CHANNEL);
      reference.push().setValue(channel);
    }
  }

  private void navigateMain() {
    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra(BundleConstants.BUNDLE_USER_TYPE, UserType.USER);
    startActivity(intent);
    finish();
  }
}
