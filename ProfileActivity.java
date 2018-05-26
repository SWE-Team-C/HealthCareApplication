package edu.swe.healthcareapplication.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.model.User;
import edu.swe.healthcareapplication.util.DatabaseConstants;

public class ProfileActivity extends AppCompatActivity {
    private EditText name;
    private EditText age;
    private EditText gender;




    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = firebaseDatabase.getReference();


    private void writeNewPost() {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child(DatabaseConstants.CHILD_USERS).child(uid).getKey();
        User user = new User(name.getText().toString(), Integer.valueOf(age.getText().toString()), gender.getText().toString());
        Map<String, Object> userValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + uid , userValues);

        mDatabase.updateChildren(childUpdates);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);
        Intent intent = getIntent();
        String value = intent.getStringExtra("key"); //전달받을 데이터

        name = (EditText)findViewById(R.id.editText1);
        age = (EditText)findViewById(R.id.editText2);
        gender = (EditText)findViewById(R.id.editText3);



        Button b = (Button)findViewById(R.id.button);
        b.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        writeNewPost();

                        //SubActivity로 가는 인텐트를 생성
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        //액티비티 시작!
                        startActivity(intent);
                    }
                }
        );


    }
}
