package huji.postpc.y2021.tal.yichye.thebubble;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.UUID;

public class UsersDB {


	private FirebaseFirestore db = null;

	public UsersDB(Context context) {
		db = FirebaseFirestore.getInstance();
	}



	private PersonData getUserById(String id){
		final PersonData[] requestedOrder = {null};
		db.collection("orders").document(id).get()
				.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
					@Override
					public void onSuccess(DocumentSnapshot documentSnapshot) {
						PersonData user = documentSnapshot.toObject(PersonData.class);
//						if (order != null) {
//							orderStatus.postValue(order.getStatus());
//						}
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.e(TAG, "onFailure: ", e);
					}
				});
		return requestedOrder[0];
	}

	public void saveUserToFS(PersonData userToSave){
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		db.collection("users").document(userToSave.getId()).set(userToSave);
	}


	private void removeUserFromFS(String id){
		db.collection("users").document(id).delete();
	}


}
