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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.ChatRoom;
import edu.swe.healthcareapplication.model.TimeTable;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.view.adapter.SelectTimeAdapter;
import java.util.List;

public class SelectTimeActivity extends AppCompatActivity {

  private static final String TAG = SelectTimeActivity.class.getSimpleName();

  private DatabaseReference mFirebaseDatabaseReference;
  private FirebaseAuth mFirebaseAuth;
  private String mTrainerId;

  private Toolbar mToolbar;
  private TabLayout mTabLayout;
  private RecyclerView mTimetableList;
  private FloatingActionButton mFab;
  private CoordinatorLayout mCoordinatorLayout;

  private int mUpdatedItemCount = 0;

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
    menuInflater.inflate(R.menu.menu_select_time, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_complete:
        writeRelation(mTrainerId);
        navigateMain();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void initView() {
    mToolbar = findViewById(R.id.toolbar);
    mTabLayout = findViewById(R.id.tab_layout);
    mTimetableList = findViewById(R.id.timetable_list);
    mFab = findViewById(R.id.fab);
    mCoordinatorLayout = findViewById(R.id.coordinator_layout);

    setSupportActionBar(mToolbar);
    String[] dateStrings = getResources().getStringArray(R.array.date);
    for (int index = 0; index < 7; index++) {
      Tab tab = mTabLayout.newTab().setText(dateStrings[index]);
      if (index == 0) {
        tab.select();
      }
      mTabLayout.addTab(tab);
    }
    mTabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
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

    mTimetableList.setLayoutManager(new LinearLayoutManager(this));
    mTimetableList.setHasFixedSize(true);

    mFab.setOnClickListener(v -> {
      int position = mTabLayout.getSelectedTabPosition();
      writeSelectedTimeTable(mTrainerId,
          ((SelectTimeAdapter) mTimetableList.getAdapter()).getSelectedKeyList(), position);
    });
  }

  private void readTrainerTimeTable(@NonNull String trainerId, int dateIndex) {
    Query query = mFirebaseDatabaseReference
        .child(DatabaseConstants.CHILD_TIMETABLE)
        .child(trainerId)
        .child(String.valueOf(dateIndex))
        .orderByChild("startTime");

    FirebaseRecyclerOptions<TimeTable> options = new FirebaseRecyclerOptions.Builder<TimeTable>()
        .setQuery(query, TimeTable.class)
        .setLifecycleOwner(this)
        .build();

    SelectTimeAdapter adapter = new SelectTimeAdapter(this, options);
    mTimetableList.setAdapter(adapter);
  }

  private void writeSelectedTimeTable(@NonNull String trainerId,
      @NonNull List<String> selectedKey,
      int dateIndex) {
    if (selectedKey.size() == 0) {
      Snackbar.make(mCoordinatorLayout, R.string.msg_timetable_not_selected,
          Snackbar.LENGTH_SHORT).show();
      return;
    }

    DatabaseReference reference = mFirebaseDatabaseReference
        .child(DatabaseConstants.CHILD_TIMETABLE)
        .child(trainerId)
        .child(String.valueOf(dateIndex));

    mUpdatedItemCount = 0;

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
          mUpdatedItemCount++;
          if (mUpdatedItemCount == selectedKey.size()) {
            Snackbar
                .make(mCoordinatorLayout, R.string.msg_timetable_added, Snackbar.LENGTH_SHORT)
                .show();
          }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
          Log.e(TAG, "onCancelled: " + databaseError.toString());
        }
      });
    }
  }

  private void writeRelation(@NonNull String trainerId) {
    FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
    if (currentUser != null) {
      String userUid = currentUser.getUid();
      ChatRoom chatRoom = new ChatRoom(userUid, trainerId, null);
      DatabaseReference reference = mFirebaseDatabaseReference
          .child(DatabaseConstants.CHILD_CHAT_ROOM);
      reference.push().setValue(chatRoom);
    }
  }

  private void navigateMain() {
    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra(BundleConstants.BUNDLE_USER_TYPE, UserType.USER);
    startActivity(intent);
    finish();
  }
}
