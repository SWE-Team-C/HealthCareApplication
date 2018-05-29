package edu.swe.healthcareapplication.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.TimeTable;
import edu.swe.healthcareapplication.model.User;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.util.Utils;
import edu.swe.healthcareapplication.view.adapter.TrainerTimeTableAdapter.TrainerTimeTableHolder;

public class TrainerTimeTableAdapter extends
    FirebaseRecyclerAdapter<TimeTable, TrainerTimeTableHolder> {

  private static final String TAG = TrainerTimeTableAdapter.class.getSimpleName();

  private Context mContext;

  /**
   * Initialize a {@link Adapter} that listens to a Firebase query. See {@link
   * FirebaseRecyclerOptions} for configuration options.
   */
  public TrainerTimeTableAdapter(
      Context context,
      @NonNull FirebaseRecyclerOptions<TimeTable> options) {
    super(options);
    this.mContext = context;
  }

  @NonNull
  @Override
  public TrainerTimeTableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.item_trainer_timetable, parent, false);
    return new TrainerTimeTableHolder(view);
  }

  @Override
  protected void onBindViewHolder(@NonNull TrainerTimeTableHolder holder, int position,
      @NonNull TimeTable model) {

    if (model.selectedUserId != null) {
      DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
          .child(DatabaseConstants.CHILD_USERS)
          .child(model.selectedUserId);

      reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          User user = dataSnapshot.getValue(User.class);
          if (user != null) {
            holder.timeView
                .setText(Utils.formatHour(mContext, model.startTime) + " (" + user.name + ")");
          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
          Log.e(TAG, databaseError.toString());
        }
      });
    } else {
      holder.timeView.setText(Utils.formatHour(mContext, model.startTime));
    }
  }

  public class TrainerTimeTableHolder extends RecyclerView.ViewHolder {

    public TextView timeView;

    public TrainerTimeTableHolder(@NonNull View itemView) {
      super(itemView);
      timeView = itemView.findViewById(R.id.tv_time);
    }
  }
}
