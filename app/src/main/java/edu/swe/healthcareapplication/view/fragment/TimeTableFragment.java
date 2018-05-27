package edu.swe.healthcareapplication.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.ChatRoom;
import edu.swe.healthcareapplication.model.TimeTable;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.util.Utils;
import java.util.Calendar;
import java.util.Date;

public class TimeTableFragment extends Fragment {

  private static final String TAG = TimeTableFragment.class.getSimpleName();

  private int mDateIndex = 0;

  private RecyclerView mRecyclerView;
  private TabLayout mTabLayout;
  private Toolbar mToolbar;

  private UserType mUserType;
  private String mTrainerId;

  public static TimeTableFragment newInstance(UserType userType) {
    TimeTableFragment instance = new TimeTableFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(BundleConstants.BUNDLE_USER_TYPE, userType);
    instance.setArguments(bundle);
    return instance;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mDateIndex = getTodayIndex();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);
    mRecyclerView = rootView.findViewById(R.id.timetable_list);
    mTabLayout = rootView.findViewById(R.id.tab_layout);
    mToolbar = rootView.findViewById(R.id.toolbar);
//    mToolbar.inflateMenu(R.menu.menu_timetable);
//    mToolbar.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//      @Override
//      public boolean onMenuItemClick(MenuItem item) {
//        return false;
//      }
//    });

    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    String[] dateStrings = getResources().getStringArray(R.array.date);
    for (int index = 0; index < 7; index++) {
      Tab tab = mTabLayout.newTab().setText(dateStrings[index]);
      mTabLayout.addTab(tab);
    }
    mTabLayout.getTabAt(mDateIndex).select();
    mTabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
      @Override
      public void onTabSelected(Tab tab) {
        mDateIndex = tab.getPosition();
        requestTimeTable();
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
    return rootView;
  }

  @Override
  public void onStart() {
    super.onStart();
    Bundle bundle = getArguments();
    if (bundle != null) {
      mUserType = (UserType) bundle.getSerializable(BundleConstants.BUNDLE_USER_TYPE);
    }
    requestTimeTable();
  }

  private int getTodayIndex() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    int index = calendar.get(Calendar.DAY_OF_WEEK);
    if (index == 1) { // SUNDAY
      index = 6;
    } else {
      index = index - 2;
    }
    return index;
  }

  private boolean isSignedIn() {
    return FirebaseAuth.getInstance().getCurrentUser() != null;
  }

  private void requestTimeTable() {
    if (isSignedIn()) {
      if (mUserType == UserType.TRAINER) {
        readTrainerTimeTable();
      } else {
        if (mTrainerId == null) {
          readTrainerId();
        } else {
          readUserTimeTable(mTrainerId);
        }
      }
    }
  }

  private void readTrainerTimeTable() {
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Query query = FirebaseDatabase.getInstance().getReference()
        .child(DatabaseConstants.CHILD_TIMETABLE)
        .child(uid)
        .child(String.valueOf(mDateIndex)) // Date Index
        .orderByChild("startTime");

    FirebaseRecyclerOptions<TimeTable> options = new FirebaseRecyclerOptions.Builder<TimeTable>()
        .setQuery(query, TimeTable.class)
        .setLifecycleOwner(this)
        .build();

    FirebaseRecyclerAdapter<TimeTable, TrainerTimeTableHolder> adapter = new FirebaseRecyclerAdapter<TimeTable, TrainerTimeTableHolder>(
        options) {
      @NonNull
      @Override
      public TrainerTimeTableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_timetable, parent, false);
        return new TrainerTimeTableHolder(view);
      }

      @Override
      protected void onBindViewHolder(@NonNull TrainerTimeTableHolder holder, int position,
          @NonNull TimeTable model) {
        holder.timeView.setText(Utils.formatHour(getContext(), model.startTime));
      }
    };
    mRecyclerView.setAdapter(adapter);
  }

  private void readTrainerId() {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    if (currentUser != null) {
      Query query = FirebaseDatabase.getInstance().getReference()
          .child(DatabaseConstants.CHILD_CHAT_ROOM)
          .orderByChild("userId")
          .equalTo(currentUser.getUid())
          .limitToLast(1);
      query.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          Iterable<DataSnapshot> children = dataSnapshot.getChildren();
          for (DataSnapshot child : children) {
            ChatRoom chatRoom = child.getValue(ChatRoom.class);
            if (chatRoom != null) {
              mTrainerId = chatRoom.trainerId;
              readUserTimeTable(mTrainerId);
            }
          }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
          Log.e(TAG, "onCancelled: " + databaseError.toString());
        }
      });
    }
  }

  private void readUserTimeTable(String trainerId) {
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Query query = FirebaseDatabase.getInstance().getReference()
        .child(DatabaseConstants.CHILD_TIMETABLE)
        .child(trainerId)
        .child(String.valueOf(mDateIndex)) // Date Index
        .orderByChild("selectedUserId")
        .equalTo(uid);

    FirebaseRecyclerOptions<TimeTable> options = new FirebaseRecyclerOptions.Builder<TimeTable>()
        .setQuery(query, TimeTable.class)
        .setLifecycleOwner(this)
        .build();

    FirebaseRecyclerAdapter<TimeTable, UserTimeTableHolder> adapter = new FirebaseRecyclerAdapter<TimeTable, UserTimeTableHolder>(
        options) {
      @NonNull
      @Override
      public UserTimeTableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_timetable, parent, false);
        return new UserTimeTableHolder(view);
      }

      @Override
      protected void onBindViewHolder(@NonNull UserTimeTableHolder holder, int position,
          @NonNull TimeTable model) {
        holder.timeView.setText(String.valueOf(model.startTime));
      }
    };
    mRecyclerView.setAdapter(adapter);
  }

  public class TrainerTimeTableHolder extends RecyclerView.ViewHolder {

    public TextView timeView;

    public TrainerTimeTableHolder(@NonNull View itemView) {
      super(itemView);
      timeView = itemView.findViewById(R.id.tv_time);
    }
  }

  public class UserTimeTableHolder extends RecyclerView.ViewHolder {

    public TextView timeView;

    public UserTimeTableHolder(@NonNull View itemView) {
      super(itemView);
      timeView = itemView.findViewById(R.id.tv_time);
    }
  }
}
