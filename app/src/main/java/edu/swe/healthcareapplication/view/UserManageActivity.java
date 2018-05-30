package edu.swe.healthcareapplication.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.ChatRoom;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.view.adapter.UserAdapter;
import edu.swe.healthcareapplication.view.widget.RecyclerViewWithEmptyView;

public class UserManageActivity extends AppCompatActivity {

  private static final String TAG = UserManageActivity.class.getSimpleName();

  private RecyclerViewWithEmptyView mUserListView;
  private UserAdapter mAdapter;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_manage);
    initView();
  }

  @Override
  protected void onStart() {
    super.onStart();
    readRelatedUserIds();
  }

  @Override
  public boolean onSupportNavigateUp() {
    onBackPressed();
    return super.onSupportNavigateUp();
  }

  private void initView() {
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    mAdapter = new UserAdapter(this);
    mUserListView = findViewById(R.id.user_list);
    mUserListView.setLayoutManager(new LinearLayoutManager(this));
    mUserListView.setHasFixedSize(true);
    mUserListView.setEmptyView(findViewById(R.id.empty_user));
    mUserListView.setAdapter(mAdapter);
  }

  private void readRelatedUserIds() {
    String uid = fetchUid();

    Query query = FirebaseDatabase.getInstance().getReference()
        .child(DatabaseConstants.CHILD_CHAT_ROOM)
        .orderByChild("trainerId")
        .equalTo(uid);

    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        mAdapter.clearUserIds();
        for (DataSnapshot child : dataSnapshot.getChildren()) {
          ChatRoom chatRoom = child.getValue(ChatRoom.class);
          if (chatRoom != null) {
            mAdapter.addUserId(chatRoom.userId);
          }
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.e(TAG, databaseError.toString());
      }
    });
  }

  @NonNull
  private String fetchUid() {
    return FirebaseAuth.getInstance().getCurrentUser().getUid();
  }
}
