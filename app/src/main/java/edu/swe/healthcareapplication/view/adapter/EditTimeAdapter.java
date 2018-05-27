package edu.swe.healthcareapplication.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.TimeTable;
import edu.swe.healthcareapplication.view.adapter.EditTimeAdapter.EditTimeHolder;

public class EditTimeAdapter extends FirebaseRecyclerAdapter<TimeTable, EditTimeHolder> {

  /**
   * Initialize a {@link Adapter} that listens to a Firebase query. See {@link
   * FirebaseRecyclerOptions} for configuration options.
   */
  public EditTimeAdapter(
      @NonNull FirebaseRecyclerOptions<TimeTable> options) {
    super(options);
  }

  @NonNull
  @Override
  public EditTimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.item_edit_time, parent, false);
    return new EditTimeHolder(view);
  }

  @Override
  protected void onBindViewHolder(@NonNull EditTimeHolder holder, int position,
      @NonNull TimeTable model) {
    DatabaseReference parent = getRef(position).getParent();
    String key = getRef(position).getKey();
    holder.timeView.setText(String.valueOf(model.startTime));
    holder.btnRemove.setOnClickListener(v -> {
      parent.child(key).removeValue();
    });
  }

  public class EditTimeHolder extends ViewHolder {

    public TextView timeView;
    public ImageButton btnRemove;

    public EditTimeHolder(@NonNull View itemView) {
      super(itemView);
      timeView = itemView.findViewById(R.id.tv_time);
      btnRemove = itemView.findViewById(R.id.btn_remove);
    }
  }
}
