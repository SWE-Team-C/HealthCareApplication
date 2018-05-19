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
import edu.swe.healthcareapplication.databinding.FragmentChatBinding;

public class ChatFragment extends Fragment {

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    FragmentChatBinding binding = DataBindingUtil
        .inflate(inflater, R.layout.fragment_chat, container, false);
    return binding.getRoot();
  }
}
