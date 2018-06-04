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
import edu.swe.healthcareapplication.view.adapter.CertificateAdapter.CertificateHolder;
import java.util.ArrayList;
import java.util.List;

public class CertificateAdapter extends RecyclerView.Adapter<CertificateHolder> {

  private List<String> mCertificateList;

  public CertificateAdapter() {
    mCertificateList = new ArrayList<>();
  }

  @NonNull
  @Override
  public CertificateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.item_certificate, parent, false);
    return new CertificateHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull CertificateHolder holder, int position) {
    holder.certificateView.setText(mCertificateList.get(position));
    holder.btnRemove.setOnClickListener(v -> {
      mCertificateList.remove(position);
      notifyItemRemoved(position);
      notifyDataSetChanged();
    });
  }

  @Override
  public int getItemCount() {
    return mCertificateList.size();
  }

  public List<String> getCertificateList() {
    return mCertificateList;
  }

  public void addCertificate(String certificate) {
    mCertificateList.add(certificate);
    notifyItemInserted(mCertificateList.size());
  }

  public void clearCertificate() {
    mCertificateList.clear();
    notifyDataSetChanged();
  }

  public class CertificateHolder extends ViewHolder {

    public TextView certificateView;
    public ImageButton btnRemove;

    public CertificateHolder(@NonNull View itemView) {
      super(itemView);
      certificateView = itemView.findViewById(R.id.tv_certificate);
      btnRemove = itemView.findViewById(R.id.btn_remove);
    }
  }
}
