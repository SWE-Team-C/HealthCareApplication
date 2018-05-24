package edu.swe.healthcareapplication.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import edu.swe.healthcareapplication.model.Chat;
import edu.swe.healthcareapplication.model.ChatRoom;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatRoomActivity extends AppCompatActivity {

  private static final String TAG = ChatRoomActivity.class.getSimpleName();

  private RecyclerView mChatList;
  private TextInputEditText mEditMessage;
  private ImageButton mBtnSend;

  private String mChatRoomKey;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat_room);
    initView();
  }

  private void initView() {
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    mChatList = findViewById(R.id.chat_list);
    mEditMessage = findViewById(R.id.edit_message);
    mBtnSend = findViewById(R.id.btn_send);

    mChatList.setHasFixedSize(true);
    mChatList.setLayoutManager(new LinearLayoutManager(this));
    mChatList.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
      if (bottom < oldBottom) {
        mChatList.postDelayed(() -> {
          mChatList.smoothScrollToPosition(mChatList.getAdapter().getItemCount());
        }, 100);
      }
    });

    mEditMessage.requestFocus();

    mBtnSend.setOnClickListener(v -> {
      String message = mEditMessage.getText().toString();
      if (!message.isEmpty()) {
        sendChat(mChatRoomKey, mEditMessage.getText().toString());
        mEditMessage.setText("");
        mEditMessage.requestFocus();
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      mChatRoomKey = bundle.getString(BundleConstants.BUNDLE_CHAT_ROOM_KEY);
      String chatName = bundle.getString(BundleConstants.BUNDLE_CHAT_NAME);
      setTitle(chatName);
    }
    readChats(mChatRoomKey);
  }

  @Override
  public boolean onSupportNavigateUp() {
    onBackPressed();
    return super.onSupportNavigateUp();
  }

  private void sendChat(String chatRoomKey, String message) {
    String uid = getUserUid();
    long timestamp = System.currentTimeMillis();
    Chat chat = new Chat(DatabaseConstants.CHAT_TYPE_TEXT, uid, timestamp, message);
    FirebaseDatabase.getInstance().getReference().child(DatabaseConstants.CHILD_CHATS)
        .child(chatRoomKey)
        .push()
        .setValue(chat);

    updateChatRoom(chatRoomKey, chat);
  }

  private void updateChatRoom(String chatRoomKey, Chat chat) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
        .child(DatabaseConstants.CHILD_CHAT_ROOM)
        .child(chatRoomKey);

    reference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
        if (chatRoom != null) {
          chatRoom.latestMessage = chat.message;
          dataSnapshot.getRef().setValue(chatRoom);
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: " + databaseError.toString());
      }
    });
  }

  private void readChats(String chatRoomKey) {
    Query query = FirebaseDatabase.getInstance()
        .getReference(DatabaseConstants.CHILD_CHATS)
        .child(chatRoomKey)
        .orderByChild("timestamp");

    FirebaseRecyclerOptions<Chat> options = new FirebaseRecyclerOptions.Builder<Chat>()
        .setQuery(query, Chat.class)
        .setLifecycleOwner(this)
        .build();

    FirebaseRecyclerAdapter<Chat, ChatHolder> adapter = new FirebaseRecyclerAdapter<Chat, ChatHolder>(
        options) {
      private static final int TYPE_SEND = 100;
      private static final int TYPE_RECEIVE = 101;

      @Override
      protected void onBindViewHolder(@NonNull ChatHolder holder, int position,
          @NonNull Chat model) {
        holder.messageView.setText(model.message);
        holder.timeView.setText(getDateString(model.timestamp));
      }

      @NonNull
      @Override
      public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        int layoutId;
        if (viewType == TYPE_SEND) {
          layoutId = R.layout.item_chat_send;
        } else {
          layoutId = R.layout.item_chat_receive;
        }
        View view = inflater.inflate(layoutId, parent, false);
        return new ChatHolder(view);
      }

      @Override
      public int getItemViewType(int position) {
        Chat item = getItem(position);
        if (item.senderId.equals(getUserUid())) {
          return TYPE_SEND;
        }
        return TYPE_RECEIVE;
      }
    };

    adapter.registerAdapterDataObserver(new AdapterDataObserver() {
      @Override
      public void onItemRangeInserted(int positionStart, int itemCount) {
        super.onItemRangeInserted(positionStart, itemCount);
        int count = adapter.getItemCount();
        LinearLayoutManager layoutManager = (LinearLayoutManager) mChatList.getLayoutManager();
        int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
        if (lastVisibleItemPosition == -1 ||
            (positionStart >= (count - 1) &&
                lastVisibleItemPosition == (positionStart - 1))) {
          mChatList.scrollToPosition(positionStart);
        }
      }
    });
    mChatList.setAdapter(adapter);
  }

  private String getDateString(long timestamp) {
    Date date = new Date(timestamp);
    SimpleDateFormat format = new SimpleDateFormat("a hh:mm");
    return format.format(date);
  }

  private String getUserUid() {
    return FirebaseAuth.getInstance().getCurrentUser().getUid();
  }

  public class ChatHolder extends RecyclerView.ViewHolder {

    public TextView messageView;
    public TextView timeView;

    public ChatHolder(@NonNull View itemView) {
      super(itemView);
      messageView = itemView.findViewById(R.id.tv_message_body);
      timeView = itemView.findViewById(R.id.tv_message_time);
    }
  }
}
