package edu.swe.healthcareapplication.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ChatRoom {

  public String userId;
  public String trainerId;

  public ChatRoom() {
    // Default constructor required for calls to DataSnapshot.getValue(ChatRoom.class)
  }

  public ChatRoom(String userId, String trainerId) {
    this.userId = userId;
    this.trainerId = trainerId;
  }
}
