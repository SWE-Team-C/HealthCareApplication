package edu.swe.healthcareapplication.service;

import android.support.annotation.Nullable;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import edu.swe.healthcareapplication.util.DatabaseConstants;

public class ChatInstanceIdService extends FirebaseInstanceIdService {

  private static final String TAG = ChatInstanceIdService.class.getSimpleName();

  @Override
  public void onTokenRefresh() {
    super.onTokenRefresh();
    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
    Log.d(TAG, "Refreshed Token: " + refreshedToken);
    String uid = fetchUid();
    if (uid != null) {
      saveToken(uid, refreshedToken);
    }
  }

  private void saveToken(String uid, String token) {
    FirebaseDatabase.getInstance().getReference()
        .child(DatabaseConstants.CHILD_FCM_TOKEN)
        .child(uid)
        .setValue(token);
  }

  @Nullable
  private String fetchUid() {
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    if (currentUser != null) {
      return currentUser.getUid();
    }
    return null;
  }
}
