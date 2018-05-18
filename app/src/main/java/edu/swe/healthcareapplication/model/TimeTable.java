package edu.swe.healthcareapplication.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class TimeTable {

  public String selectedUserId;
  public int startTime;

  public TimeTable() {
    // Default constructor required for calls to DataSnapshot.getValue(TimeTable.class)
  }

  public TimeTable(String selectedUserId, int startTime) {
    this.selectedUserId = selectedUserId;
    this.startTime = startTime;
  }
}
