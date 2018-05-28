package edu.swe.healthcareapplication.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.ChatRoom;
import edu.swe.healthcareapplication.model.Trainer;
import edu.swe.healthcareapplication.model.User;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.view.ChatRoomActivity;

public class ChatFragment extends Fragment {

  private static final String TAG = ChatFragment.class.getSimpleName();

  private RecyclerView mChatRoomList;
  private UserType mUserType;

  public static ChatFragment newInstance(UserType userType) {
    ChatFragment instance = new ChatFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(BundleConstants.BUNDLE_USER_TYPE, userType);
    instance.setArguments(bundle);
    return instance;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle != null) {
      mUserType = (UserType) bundle.getSerializable(BundleConstants.BUNDLE_USER_TYPE);
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
    mChatRoomList = rootView.findViewById(R.id.chatroom_list);
    mChatRoomList.setLayoutManager(new LinearLayoutManager(getActivity()));
    mChatRoomList.setHasFixedSize(true);
    return rootView;
  }

  @Override
  public void onStart() {
    super.onStart();
    readChatRoom();
  }

  private void readChatRoom() {
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String filterChildName = "userId";
    if (mUserType == UserType.TRAINER) {
      filterChildName = "trainerId";
    }

    Query query = FirebaseDatabase.getInstance().getReference()
        .child(DatabaseConstants.CHILD_CHAT_ROOM)
        .orderByChild(filterChildName)
        .equalTo(uid);

    FirebaseRecyclerOptions<ChatRoom> options = new FirebaseRecyclerOptions.Builder<ChatRoom>()
        .setQuery(query, ChatRoom.class)
        .setLifecycleOwner(this)
        .build();

    FirebaseRecyclerAdapter<ChatRoom, ChatRoomHolder> adapter = new FirebaseRecyclerAdapter<ChatRoom, ChatRoomHolder>(
        options) {
      @NonNull
      @Override
      public ChatRoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_chat_room, parent, false);
        return new ChatRoomHolder(view);
      }

      @Override
      protected void onBindViewHolder(@NonNull ChatRoomHolder holder, int position,
          @NonNull ChatRoom model) {
        readUserName(holder.nameView, mUserType, model);
        holder.latestMessageView.setText(model.latestMessage != null ? model.latestMessage : "");
        holder.itemView.setOnClickListener(v -> {
          String key = getRef(position).getKey();
          String chatName = holder.nameView.getText().toString();
          showChatRoom(key, chatName);
        });
      }
    };
    mChatRoomList.setAdapter(adapter);
  }

  private void readUserName(final TextView textView, UserType userType, ChatRoom chatRoom) {
    String uid = chatRoom.trainerId;
    String targetChild = DatabaseConstants.CHILD_TRAINERS;
    if (userType == UserType.TRAINER) {
      uid = chatRoom.userId;
      targetChild = DatabaseConstants.CHILD_USERS;
    }

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
        .child(targetChild)
        .child(uid);

    reference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        if (userType == UserType.TRAINER) {
          User user = dataSnapshot.getValue(User.class);
          textView.setText(user != null ? user.name : null);
        } else {
          Trainer trainer = dataSnapshot.getValue(Trainer.class);
          textView.setText(trainer != null ? trainer.name : null);
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: " + databaseError.toString());
      }
    });
  }

  private void showChatRoom(String key, String chatName) {
    Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
    intent.putExtra(BundleConstants.BUNDLE_CHAT_NAME, chatName);
    intent.putExtra(BundleConstants.BUNDLE_CHAT_ROOM_KEY, key);
    startActivity(intent);
  }

  public class ChatRoomHolder extends RecyclerView.ViewHolder {

    public ImageView profileView;
    public TextView nameView;
    public TextView latestMessageView;

    public ChatRoomHolder(@NonNull View itemView) {
      super(itemView);
      profileView = itemView.findViewById(R.id.iv_profile);
      nameView = itemView.findViewById(R.id.tv_user_name);
      latestMessageView = itemView.findViewById(R.id.tv_latest_message);
    }
  }
}
