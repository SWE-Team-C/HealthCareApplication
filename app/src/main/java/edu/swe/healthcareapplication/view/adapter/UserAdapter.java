package edu.swe.healthcareapplication.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.User;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.view.UserDetailActivity;
import edu.swe.healthcareapplication.view.adapter.UserAdapter.UserHolder;
import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserHolder> {

  private static final String TAG = UserAdapter.class.getSimpleName();

  private Context mContext;
  private List<String> mUserIds;

  public UserAdapter(Context context) {
    mContext = context;
    mUserIds = new ArrayList<>();
  }

  @NonNull
  @Override
  public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.item_user, parent, false);
    return new UserHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull UserHolder holder, int position) {
    String userId = mUserIds.get(position);
    holder.itemView.setOnClickListener(view -> {
      Intent intent = new Intent(mContext, UserDetailActivity.class);
      intent.putExtra(BundleConstants.BUNDLE_USER_ID, userId);
      mContext.startActivity(intent);
    });
    FirebaseDatabase.getInstance().getReference().child(DatabaseConstants.CHILD_USERS)
        .child(userId)
        .addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            User user = dataSnapshot.getValue(User.class);
            if (user != null) {
              holder.mNameView.setText(user.name);
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e(TAG, databaseError.toString());
          }
        });
  }

  @Override
  public int getItemCount() {
    return mUserIds.size();
  }

  public void addUserId(String userId) {
    mUserIds.add(userId);
    notifyItemInserted(mUserIds.size());
  }

  public void clearUserIds() {
    mUserIds.clear();
    notifyDataSetChanged();
  }

  public class UserHolder extends RecyclerView.ViewHolder {

    public TextView mNameView;

    public UserHolder(@NonNull View itemView) {
      super(itemView);
      mNameView = itemView.findViewById(R.id.tv_user_name);
    }
  }
}
