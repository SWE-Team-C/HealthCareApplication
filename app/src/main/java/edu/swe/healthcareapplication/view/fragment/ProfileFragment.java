package edu.swe.healthcareapplication.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.databinding.FragmentProfileBinding;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;

public class ProfileFragment extends Fragment {

  public static ProfileFragment newInstance(UserType userType) {
    ProfileFragment instance = new ProfileFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(BundleConstants.BUNDLE_USER_TYPE, userType);
    instance.setArguments(bundle);
    return instance;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    FragmentProfileBinding binding = DataBindingUtil
        .inflate(inflater, R.layout.fragment_profile, container, false);
    return binding.getRoot();
  }
}
