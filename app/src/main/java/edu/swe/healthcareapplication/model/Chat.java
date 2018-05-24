package edu.swe.healthcareapplication.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Chat {

  public String type;
  public String senderId;
  public long timestamp;
  public String message;

  public Chat() {
    // Default constructor required for calls to DataSnapshot.getValue(Chat.class)
  }

  public Chat(String type, String senderId, long timestamp, String message) {
    this.type = type;
    this.senderId = senderId;
    this.timestamp = timestamp;
    this.message = message;
  }
}
