package edu.swe.healthcareapplication.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.TimeTable;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.view.adapter.EditTimeAdapter;

public class EditTimeActivity extends AppCompatActivity {

  private static final String TAG = EditTimeActivity.class.getSimpleName();

  private DatabaseReference mFirebaseDatabaseReference;
  private FirebaseAuth mFirebaseAuth;

  private Toolbar mToolbar;
  private TabLayout mTabLayout;
  private RecyclerView mTimetableList;
  private FloatingActionButton mFab;
  private CoordinatorLayout mCoordinatorLayout;
  private EditText mEditTime;
  private ImageButton mBtnAddTime;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_time);
    mFirebaseAuth = FirebaseAuth.getInstance();
    mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    initView();
  }

  @Override
  protected void onStart() {
    super.onStart();
    readTimeTable(0);
  }

  private void initView() {
    mToolbar = findViewById(R.id.toolbar);
    mTabLayout = findViewById(R.id.tab_layout);
    mTimetableList = findViewById(R.id.timetable_list);
    mEditTime = findViewById(R.id.edit_time);
    mBtnAddTime = findViewById(R.id.btn_add_time);
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
        readTimeTable(tab.getPosition());
        mEditTime.setText("");
        hideKeyboard();
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

    mBtnAddTime.setOnClickListener(v -> {
      String time = mEditTime.getText().toString();
      if (!TextUtils.isEmpty(time) &&
          Integer.parseInt(time) >= 0 && Integer.parseInt(time) <= 24) {
        int dateIndex = mTabLayout.getSelectedTabPosition();
        if (writeTimeTable(dateIndex, Integer.parseInt(time))) {
          Snackbar
              .make(mCoordinatorLayout, R.string.msg_timetable_exist_time, Snackbar.LENGTH_SHORT)
              .show();
        }
        mEditTime.setText("");
        hideKeyboard();
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.menu_edit_time, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_complete:
        navigateMain();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void readTimeTable(int dateIndex) {
    String uid = mFirebaseAuth.getCurrentUser().getUid();

    Query query = mFirebaseDatabaseReference.child(DatabaseConstants.CHILD_TIMETABLE)
        .child(uid)
        .child(String.valueOf(dateIndex))
        .orderByChild("startTime");

    FirebaseRecyclerOptions<TimeTable> options = new FirebaseRecyclerOptions.Builder<TimeTable>()
        .setQuery(query, TimeTable.class)
        .setLifecycleOwner(this)
        .build();

    EditTimeAdapter adapter = new EditTimeAdapter(this, options);
    mTimetableList.setAdapter(adapter);
  }

  private boolean writeTimeTable(int dateIndex, int startTime) {
    String uid = mFirebaseAuth.getCurrentUser().getUid();
    TimeTable timeTable = new TimeTable(startTime, null);

    boolean isExistsItem = false;
    EditTimeAdapter adapter = (EditTimeAdapter) mTimetableList.getAdapter();
    for (int index = 0; index < adapter.getItemCount(); index++) {
      TimeTable item = adapter.getItem(index);
      if (item.startTime == startTime) {
        isExistsItem = true;
      }
    }
    if (!isExistsItem) {
      mFirebaseDatabaseReference.child(DatabaseConstants.CHILD_TIMETABLE)
          .child(uid)
          .child(String.valueOf(dateIndex))
          .push()
          .setValue(timeTable);
    }
    return isExistsItem;
  }

  private void hideKeyboard() {
    View view = this.getCurrentFocus();
    if (view != null) {
      InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  private void navigateMain() {
    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra(BundleConstants.BUNDLE_USER_TYPE, UserType.TRAINER);
    startActivity(intent);
    finish();
  }
}
