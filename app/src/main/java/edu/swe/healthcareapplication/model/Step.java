package edu.swe.healthcareapplication.model;

import android.view.View;
import java.util.HashMap;
import java.util.Map;

public abstract class Step {

  protected View stepView;
  protected Map<String, Object> valueMap;

  public Step(View stepView) {
    this.stepView = stepView;
    this.valueMap = new HashMap<>();
  }

  public abstract void initialize();

  public View getStepView() {
    return stepView;
  }

  public void setValueMap(Map<String, Object> valueMap) {
    if (valueMap != null) {
      this.valueMap = valueMap;
    } else {
      this.valueMap = new HashMap<>();
    }
  }

  public Map<String, Object> getValueMap() {
    return valueMap;
  }
}
