package edu.swe.healthcareapplication.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.TimeTable;
import edu.swe.healthcareapplication.view.adapter.SelectTimeAdapter.ViewHolder;
import java.util.ArrayList;
import java.util.List;

public class SelectTimeAdapter extends FirebaseRecyclerAdapter<TimeTable, ViewHolder> {

  private List<String> mSelectedKeyList;

  /**
   * Initialize a {@link Adapter} that listens to a Firebase query. See {@link
   * FirebaseRecyclerOptions} for configuration options.
   */
  public SelectTimeAdapter(
      @NonNull FirebaseRecyclerOptions<TimeTable> options) {
    super(options);
    mSelectedKeyList = new ArrayList<>();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.item_select_time, parent, false);
    return new ViewHolder(view);
  }

  @Override
  protected void onBindViewHolder(@NonNull ViewHolder holder, int position,
      @NonNull TimeTable model) {
    String key = getRef(position).getKey();
    holder.timeView.setText(String.valueOf(model.startTime));
    if (model.selectedUserId.equals(getUserUid())) {
      holder.timeCheckBox.setChecked(true);
      holder.timeCheckBox.setEnabled(true);
      mSelectedKeyList.add(key);
    } else if (!model.selectedUserId.isEmpty()) {
      holder.timeCheckBox.setChecked(false);
      holder.timeCheckBox.setEnabled(false);
    } else {
      holder.timeCheckBox.setChecked(false);
      holder.timeCheckBox.setEnabled(true);
    }
    holder.timeCheckBox.setOnClickListener(v -> {
      if (holder.timeCheckBox.isChecked()) {
        mSelectedKeyList.add(key);
      } else {
        mSelectedKeyList.remove(mSelectedKeyList.indexOf(key));
      }
    });
  }

  public String getUserUid() {
    return FirebaseAuth.getInstance().getCurrentUser().getUid();
  }

  public List<String> getSelectedKeyList() {
    return mSelectedKeyList;
  }

  protected class ViewHolder extends RecyclerView.ViewHolder {

    public TextView timeView;
    public AppCompatCheckBox timeCheckBox;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      timeView = itemView.findViewById(R.id.tv_time);
      timeCheckBox = itemView.findViewById(R.id.chk_time);
    }
  }
}
