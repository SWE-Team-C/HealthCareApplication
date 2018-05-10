package edu.swe.healthcareapplication.model;


import com.google.firebase.database.IgnoreExtraProperties;
import java.util.List;

@IgnoreExtraProperties
public class Trainer {

  public String name;
  public String education;
  public List<String> awards;

  public Trainer() {
  }

  public Trainer(String name, String education, List<String> awards) {
    this.name = name;
    this.education = education;
    this.awards = awards;
  }
}
