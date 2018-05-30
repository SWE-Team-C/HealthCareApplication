package edu.swe.healthcareapplication.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserExtra {

  public long lastModified;
  public int height;
  public double weight;
  public double bodyFat;
  public double skeletalMuscle;

  public UserExtra() {
    // Default constructor required for calls to DataSnapshot.getValue(UserExtra.class)
  }

  public UserExtra(long lastModified, int height, double weight, double bodyFat,
      double skeletalMuscle) {
    this.lastModified = lastModified;
    this.height = height;
    this.weight = weight;
    this.bodyFat = bodyFat;
    this.skeletalMuscle = skeletalMuscle;
  }
}
