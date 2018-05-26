package edu.swe.healthcareapplication.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.ChatRoom;
import edu.swe.healthcareapplication.model.Trainer;
import edu.swe.healthcareapplication.model.User;
import edu.swe.healthcareapplication.model.UserType;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.DatabaseConstants;
import edu.swe.healthcareapplication.view.ChatRoomActivity;
import edu.swe.healthcareapplication.view.ProfileActivity;

import static edu.swe.healthcareapplication.R.layout.fragment_profile;
import static edu.swe.healthcareapplication.R.layout.fragment_profile_trainer;

public class ProfileFragment extends Fragment {
  FirebaseAuth auth = FirebaseAuth.getInstance();
  FirebaseUser currentUser = auth.getCurrentUser();
  private ListView listView;

  ArrayAdapter<String> Adapter;
  ArrayList<String> arraylist = new ArrayList<String>();

  private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
  private DatabaseReference databaseReference = firebaseDatabase.getReference();
  String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

  User user = new User();
  Trainer trainer = new Trainer();
  UserType mUserType ;


  @Override
  public void onStart() {
    super.onStart();

    Bundle bundle = getArguments();
    mUserType = (UserType) bundle.getSerializable(BundleConstants.BUNDLE_USER_TYPE);

    if(mUserType==UserType.USER){
      type_user();
    }

    else if(mUserType==UserType.TRAINER){
      type_trainer();
    }



  }

  private void type_trainer() {
    Adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arraylist);
    listView.setAdapter(Adapter);

    Query query = FirebaseDatabase.getInstance().getReference()
            .child(DatabaseConstants.CHILD_TRAINERS)
            .child(uid);

    query.addValueEventListener(new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        trainer = dataSnapshot.getValue(Trainer.class);

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
    Adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arraylist);
    listView.setAdapter(Adapter);

    Query query = FirebaseDatabase.getInstance().getReference()
            .child(DatabaseConstants.CHILD_USERS)
            .child(uid);

    query.addValueEventListener(new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        user = dataSnapshot.getValue(User.class);

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


  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(fragment_profile, container, false);
    listView = rootView.findViewById(R.id.profile_info);

    Button b = (Button)rootView.findViewById(R.id.button2);
    b.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(getActivity(), ProfileActivity.class); // 다음 넘어갈 클래스 지정
        startActivity(intent); // 다음 화면으로 넘어간다
      }
    });


    return rootView;
  }

  public static ProfileFragment newInstance(UserType userType) {

    ProfileFragment instance = new ProfileFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(BundleConstants.BUNDLE_USER_TYPE, userType);
    instance.setArguments(bundle);
    return instance;
  }


  public void onCreate(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@NonNull Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
//    Button button = (Button) rootView.findViewById(R.id.button);
//    button.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//
//
//
//    //}
//  }

}}