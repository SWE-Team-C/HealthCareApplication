package edu.swe.healthcareapplication.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
  public String name;
  public int age;
  public String gender; // Representation rule (Man = "M", Woman = "W")

  public User() {
    // Default constructor required for calls to DataSnapshot.getValue(User.class)
  }

  public User(String name, int age, String gender) {
    this.name = name;
    this.age = age;
    this.gender = gender;
  }
}
