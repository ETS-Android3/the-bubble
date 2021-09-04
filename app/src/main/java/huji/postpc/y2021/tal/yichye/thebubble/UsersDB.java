package huji.postpc.y2021.tal.yichye.thebubble;


import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.firestore.FirebaseFirestore;

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


}
