package edu.swe.healthcareapplication.view.adapter;

import android.content.Context;
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
import edu.swe.healthcareapplication.util.Utils;
import edu.swe.healthcareapplication.view.adapter.SelectTimeAdapter.ViewHolder;
import java.util.HashMap;
import java.util.Map;

public class SelectTimeAdapter extends FirebaseRecyclerAdapter<TimeTable, ViewHolder> {

  private Context mContext;
  private Map<String, Boolean> mItemModifiedMap;

  /**
   * Initialize a {@link Adapter} that listens to a Firebase query. See {@link
   * FirebaseRecyclerOptions} for configuration options.
   */
  public SelectTimeAdapter(
      Context context,
      @NonNull FirebaseRecyclerOptions<TimeTable> options) {
    super(options);
    this.mContext = context;
    this.mItemModifiedMap = new HashMap<>();
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
    holder.timeView.setText(Utils.formatHour(mContext, model.startTime));
    if (model.selectedUserId == null) {
      holder.timeCheckBox.setChecked(false);
      holder.timeCheckBox.setEnabled(true);
    } else if (model.selectedUserId.equals(getUserUid())) {
      holder.timeCheckBox.setChecked(true);
      holder.timeCheckBox.setEnabled(true);
      mItemModifiedMap.put(key, true);
    } else {
      holder.timeCheckBox.setChecked(false);
      holder.timeCheckBox.setEnabled(false);
    }
    holder.timeCheckBox.setOnClickListener(v -> {
      if (holder.timeCheckBox.isChecked()) {
        mItemModifiedMap.put(key, true);
      } else {
        mItemModifiedMap.put(key, false);
      }
    });
  }

  private String getUserUid() {
    return FirebaseAuth.getInstance().getCurrentUser().getUid();
  }

  public Map<String, Boolean> getItemModifiedMap() {
    return mItemModifiedMap;
  }

  public void setItemUpdated() {
    mItemModifiedMap.clear();
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
