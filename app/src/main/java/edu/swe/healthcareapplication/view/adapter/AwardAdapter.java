package edu.swe.healthcareapplication.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.view.adapter.AwardAdapter.AwardHolder;
import java.util.ArrayList;
import java.util.List;

public class AwardAdapter extends RecyclerView.Adapter<AwardHolder> {

  private List<String> mAwardList;

  public AwardAdapter() {
    mAwardList = new ArrayList<>();
  }

  @NonNull
  @Override
  public AwardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.item_award, parent, false);
    return new AwardHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull AwardHolder holder, int position) {
    holder.awardView.setText(mAwardList.get(position));
    holder.btnRemove.setOnClickListener(v -> {
      mAwardList.remove(position);
      notifyItemRemoved(position);
      notifyDataSetChanged();
    });
  }

  @Override
  public int getItemCount() {
    return mAwardList.size();
  }

  public List<String> getAwardList() {
    return mAwardList;
  }

  public void addAward(String award) {
    mAwardList.add(award);
    notifyItemInserted(mAwardList.size());
  }

  public void clearAward() {
    mAwardList.clear();
    notifyDataSetChanged();
  }

  public class AwardHolder extends ViewHolder {

    public TextView awardView;
    public ImageButton btnRemove;

    public AwardHolder(@NonNull View itemView) {
      super(itemView);
      awardView = itemView.findViewById(R.id.tv_award);
      btnRemove = itemView.findViewById(R.id.btn_remove);
    }
  }
}
