package huji.postpc.y2021.tal.yichye.thebubble;


import android.content.Context;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

import huji.postpc.y2021.tal.yichye.thebubble.Connections.Request;

public class UsersDB {

	private FirebaseFirestore db = null;

	public UsersDB(Context context) {
		db = FirebaseFirestore.getInstance();

	}


	public LiveData<PersonData> getUserByID(String userId)
	{
		MutableLiveData<PersonData> liveData = new MutableLiveData<>();
		db.collection("users").document(userId).get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists()) {
						PersonData user = documentSnapshot.toObject(PersonData.class);
						liveData.setValue(user);
					}
					else {
						liveData.setValue(null);
					}
				});
		return liveData;
	}

	public void addUserToDB(PersonData userToSave){
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		db.collection("users").document(userToSave.getId()).set(userToSave);
	}


	private void removeUserFromDB(String id){
		db.collection("users").document(id).delete();
	}

	public void updateUserField(String userId, String fieldToChange, Object newValue)
	{
		db.collection("users").document(userId).update(fieldToChange, newValue);

	}

	public void addRequest(String userId, Request newRequest)
	{
		db.collection("users").document(userId).get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists()) {
						PersonData user = documentSnapshot.toObject(PersonData.class);
						user.requests.add(newRequest);
						updateUserField(userId, "requests", user.requests);
					}
				});
	}

	public void addToIgnoreList(String userId, String ignoredUserId) {
		db.collection("users").document(userId).get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists()) {
						PersonData user = documentSnapshot.toObject(PersonData.class);
						user.ignoreList.add(ignoredUserId);
						updateUserField(userId, "ignoreList", user.ignoreList);
					}
				});
	}

	public FirebaseFirestore getDb() {
		return db;
	}
}
