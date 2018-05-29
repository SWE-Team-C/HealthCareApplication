package edu.swe.healthcareapplication.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.TimeTable;
import edu.swe.healthcareapplication.util.Utils;
import edu.swe.healthcareapplication.view.adapter.UserTimeTableAdapter.UserTimeTableHolder;

public class UserTimeTableAdapter extends FirebaseRecyclerAdapter<TimeTable, UserTimeTableHolder> {

  private Context mContext;

  /**
   * Initialize a {@link Adapter} that listens to a Firebase query. See {@link
   * FirebaseRecyclerOptions} for configuration options.
   */
  public UserTimeTableAdapter(
      Context context,
      @NonNull FirebaseRecyclerOptions<TimeTable> options) {
    super(options);
    this.mContext = context;
  }

  @NonNull
  @Override
  public UserTimeTableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.item_user_timetable, parent, false);
    return new UserTimeTableHolder(view);
  }

  @Override
  protected void onBindViewHolder(@NonNull UserTimeTableHolder holder, int position,
      @NonNull TimeTable model) {
    holder.timeView.setText(Utils.formatHour(mContext, model.startTime));
  }

  public class UserTimeTableHolder extends RecyclerView.ViewHolder {

    public TextView timeView;

    public UserTimeTableHolder(@NonNull View itemView) {
      super(itemView);
      timeView = itemView.findViewById(R.id.tv_time);
    }
  }
}
