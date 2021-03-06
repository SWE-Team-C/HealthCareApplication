package edu.swe.healthcareapplication.view.adapter;

import android.annotation.SuppressLint;
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
import edu.swe.healthcareapplication.model.Trainer;
import edu.swe.healthcareapplication.view.adapter.SelectTrainerAdapter.ViewHolder;
import edu.swe.healthcareapplication.view.listener.OnItemSelectListener;

public class SelectTrainerAdapter extends FirebaseRecyclerAdapter<Trainer, ViewHolder> {

  private OnItemSelectListener<String> mListener;
  private Context mContext;

  /**
   * Initialize a {@link Adapter} that listens to a Firebase query. See {@link
   * FirebaseRecyclerOptions} for configuration options.
   */
  public SelectTrainerAdapter(Context context,
      @NonNull FirebaseRecyclerOptions<Trainer> options) {
    super(options);
    mContext = context;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.item_select_trainer, parent, false);
    return new ViewHolder(view);
  }

  @SuppressLint("SetTextI18n")
  @Override
  protected void onBindViewHolder(@NonNull ViewHolder holder, int position,
      @NonNull Trainer model) {
    holder.nameView.setText(model.name);
    holder.educationView
        .setText(mContext.getString(R.string.hint_education) + " : " + model.education);
    String awardsPrefix = mContext.getString(R.string.hint_certificate) + " : ";
    if (model.certificates != null) {
      StringBuilder awardsString = new StringBuilder(model.certificates.get(0));
      if (model.certificates.size() > 1) {
        for (int index = 1; index < model.certificates.size(); index++) {
          String award = model.certificates.get(index);
          awardsString.append(", ").append(award);
        }
      }
      holder.certificatesView.setText(awardsPrefix + awardsString.toString());
    } else {
      holder.certificatesView.setText(awardsPrefix + mContext.getString(R.string.empty_item));
    }
    holder.itemView.setOnClickListener(v -> {
      if (mListener != null) {
        mListener.onItemSelected(getRef(position).getKey());
      }
    });
  }


  public void setOnItemSelectListener(
      OnItemSelectListener<String> onItemSelectListener) {
    this.mListener = onItemSelectListener;
  }

  protected class ViewHolder extends RecyclerView.ViewHolder {

    public TextView nameView;
    public TextView educationView;
    public TextView certificatesView;

    public ViewHolder(View itemView) {
      super(itemView);
      nameView = itemView.findViewById(R.id.tv_name);
      educationView = itemView.findViewById(R.id.tv_education);
      certificatesView = itemView.findViewById(R.id.tv_certificates);
    }
  }
}
