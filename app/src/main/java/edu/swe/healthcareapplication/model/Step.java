package edu.swe.healthcareapplication.model;

import android.view.View;
import android.view.ViewStub;
import java.util.HashMap;
import java.util.Map;

public abstract class Step {

  private boolean isInitialized = false;
  private ViewStub stepView;
  private Map<String, Object> valueMap;

  protected Step(ViewStub stepView) {
    this.stepView = stepView;
    this.valueMap = new HashMap<>();
  }

  protected abstract void onInitialize(View inflatedView);

  public void initialize() {
    if (!isInitialized) {
      View inflatedView = stepView.inflate();
      onInitialize(inflatedView);
      isInitialized = true;
    }
  }

  public ViewStub getStepView() {
    return stepView;
  }

  public Map<String, Object> getValueMap() {
    return valueMap;
  }

  public void setValueMap(Map<String, Object> valueMap) {
    this.valueMap = valueMap;
  }
}
