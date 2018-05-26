package edu.swe.healthcareapplication.util.step;

public interface Navigator {

  boolean nextStep();

  boolean backStep();

  void completeStep();

}
