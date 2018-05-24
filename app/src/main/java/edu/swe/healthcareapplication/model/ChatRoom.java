package edu.swe.healthcareapplication.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ChatRoom {

  public String userId;
  public String trainerId;
  public String latestMessage;

  public ChatRoom() {
    // Default constructor required for calls to DataSnapshot.getValue(ChatRoom.class)
  }

  public ChatRoom(String userId, String trainerId, String latestMessage) {
    this.userId = userId;
    this.trainerId = trainerId;
    this.latestMessage = latestMessage;
  }
}
