package edu.swe.healthcareapplication.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ChatChannel {

  public String userId;
  public String trainerId;

  public ChatChannel() {
  }

  public ChatChannel(String userId, String trainerId) {
    this.userId = userId;
    this.trainerId = trainerId;
  }
}
