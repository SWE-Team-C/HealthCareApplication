package edu.swe.healthcareapplication.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.Trainer;
import edu.swe.healthcareapplication.model.User;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.view.EditProfileActivity;
import java.util.ArrayList;

public class ProfileFragment extends Fragment {

  FirebaseAuth auth = FirebaseAuth.getInstance();
  FirebaseUser currentUser = auth.getCurrentUser();
  ArrayAdapter<String> Adapter;
  ArrayList<String> arraylist = new ArrayList<>();
  String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
  User user = new User();
  Trainer trainer = new Trainer();
  UserType mUserType;
  private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
  private DatabaseReference databaseReference = firebaseDatabase.getReference();

  private ListView mProfileListView;
  private FloatingActionButton mFab;

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
    View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
    mProfileListView = rootView.findViewById(R.id.profile_info);
    mFab = rootView.findViewById(R.id.fab);
    mFab.setOnClickListener(view -> {
      Intent intent = new Intent(getActivity(), EditProfileActivity.class);
      startActivity(intent);
    });
    return rootView;
  }

  @Override
  public void onStart() {
    super.onStart();
    Bundle bundle = getArguments();
    mUserType = (UserType) bundle.getSerializable(BundleConstants.BUNDLE_USER_TYPE);
    if (mUserType == UserType.USER) {
      type_user();
    } else if (mUserType == UserType.TRAINER) {
      type_trainer();
    }
  }

  private void type_trainer() {
    Adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
        arraylist);
    mProfileListView.setAdapter(Adapter);

    Query query = FirebaseDatabase.getInstance().getReference()
        .child(DatabaseConstants.CHILD_TRAINERS)
        .child(uid);

    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        trainer = dataSnapshot.getValue(Trainer.class);
        arraylist.clear();
        arraylist.add(String.valueOf(trainer.awards));
        arraylist.add(trainer.education);
        arraylist.add(trainer.name);
        Adapter.notifyDataSetChanged();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        //Log.e(TAG, "onCancelled: " + databaseError.toString());
      }
    });
  }

  private void type_user() {
    Adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
        arraylist);
    mProfileListView.setAdapter(Adapter);

    Query query = FirebaseDatabase.getInstance().getReference()
        .child(DatabaseConstants.CHILD_USERS)
        .child(uid);

    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        user = dataSnapshot.getValue(User.class);
        arraylist.clear();
        arraylist.add(String.valueOf(user.age));
        arraylist.add(user.gender);
        arraylist.add(user.name);
        Adapter.notifyDataSetChanged();
      }
      @Override
      public void onCancelled(DatabaseError databaseError) {
        //Log.e(TAG, "onCancelled: " + databaseError.toString());
      }
    });
  }
}