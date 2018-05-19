package edu.swe.healthcareapplication.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.databinding.FragmentTimetableBinding;
import edu.swe.healthcareapplication.model.ChatRoom;
import edu.swe.healthcareapplication.model.TimeTable;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import java.util.Calendar;
import java.util.Date;

public class TimeTableFragment extends Fragment {

  private static final String TAG = TimeTableFragment.class.getSimpleName();

  private RecyclerView mRecyclerView;
  private int mDateIndex = 0;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    FragmentTimetableBinding binding = DataBindingUtil
        .inflate(inflater, R.layout.fragment_timetable, container, false);
    mRecyclerView = binding.timetableList;
    mDateIndex = getTodayIndex();
    binding.timetableList.setHasFixedSize(true);
    binding.timetableList.setLayoutManager(new LinearLayoutManager(getActivity()));

    String[] dateStrings = getResources().getStringArray(R.array.date);
    for (int index = 0; index < 7; index++) {
      Tab tab = binding.tabLayout.newTab().setText(dateStrings[index]);
      binding.tabLayout.addTab(tab);
    }
    binding.tabLayout.getTabAt(mDateIndex).select();
    binding.tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
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
    return binding.getRoot();
  }

  @Override
  public void onStart() {
    super.onStart();
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
      FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
      readUserType(user);
    }
  }

  private void readUserType(FirebaseUser user) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    reference.child(DatabaseConstants.CHILD_USER_TYPES).child(user.getUid())
        .addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                String userType = (String) dataSnapshot.getValue();
                if (userType != null) {
                  if (userType.equals(DatabaseConstants.USER_TYPE_TRAINER)) {
                    readTrainerTimeTable();
                  } else {
                    readTrainerId();
                  }
                }
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.toString());
              }
            });
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
        holder.timeView.setText(String.valueOf(model.startTime));
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
              readUserTimeTable(chatRoom.trainerId);
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
