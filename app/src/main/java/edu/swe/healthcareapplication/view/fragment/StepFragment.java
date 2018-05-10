package edu.swe.healthcareapplication.view.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import edu.swe.healthcareapplication.model.Step;
import java.util.ArrayList;
import java.util.List;

public class StepFragment extends Fragment {

  private List<Step> mStepList = new ArrayList<>();
  private int currentPosition = 0;

  protected void addStep(Step step) {
    mStepList.add(step);
  }

  protected void moveStep(int position) {
    Step previousStep = mStepList.get(currentPosition);
    for (int index = 0; index < mStepList.size(); index++) {
      Step step = mStepList.get(index);
      if (index == position) {
        if (position != currentPosition) {
          step.setValueMap(previousStep.getValueMap());
        }
        step.initialize();
      } else {
        step.getStepView().setVisibility(View.GONE);
      }
    }
    currentPosition = position;
  }
}
