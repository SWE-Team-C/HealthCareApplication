package edu.swe.healthcareapplication.view.adapter;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.TimeTable;
import java.util.ArrayList;
import java.util.List;

public class SelectTimeAdapter extends RecyclerView.Adapter<SelectTimeAdapter.ViewHolder> {

  private List<Pair<String, TimeTable>> mTimeTableList;
  private List<String> mSelectedKeyList;

  public SelectTimeAdapter() {
    mTimeTableList = new ArrayList<>();
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
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Pair<String, TimeTable> timeTablePair = mTimeTableList.get(position);
    String key = timeTablePair.first;
    TimeTable timeTable = timeTablePair.second;
    if (timeTable != null) {
      FirebaseAuth auth = FirebaseAuth.getInstance();
      FirebaseUser currentUser = auth.getCurrentUser();
      holder.timeCheckBox.setEnabled(true);
      holder.timeCheckBox.setChecked(false);
      if (currentUser != null && currentUser.getUid().equals(timeTable.selectedUserId)) {
        holder.timeCheckBox.setChecked(true);
        mSelectedKeyList.add(key);
      } else if (!timeTable.selectedUserId.isEmpty()) {
        holder.timeCheckBox.setEnabled(false);
      }
      holder.timeView.setText(String.valueOf(timeTable.startTime));
    }
    holder.timeCheckBox.setOnClickListener(
        v -> {
          if (holder.timeCheckBox.isChecked()) {
            mSelectedKeyList.add(key);
          } else {
            mSelectedKeyList.remove(mSelectedKeyList.indexOf(key));
          }
        });
  }

  @Override
  public int getItemCount() {
    return mTimeTableList.size();
  }

  public void setTimeTableList(List<Pair<String, TimeTable>> timeTableList) {
    this.mTimeTableList = timeTableList;
    this.mSelectedKeyList.clear();
    notifyDataSetChanged();
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
