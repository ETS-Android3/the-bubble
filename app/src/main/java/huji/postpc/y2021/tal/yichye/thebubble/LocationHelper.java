package huji.postpc.y2021.tal.yichye.thebubble;

import com.google.android.gms.location.LocationRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class LocationHelper {

	public static final String LOCATIONS_FILE_NAME = "locations.json";
	public static final String LAST_LOCATION_FILE_NAME = "last_location.json";


	public static StorageReference createLocationReference(String userName, String fileName) {
		return FirebaseStorage.getInstance().getReference().child(userName).child("locations_dir").child(fileName);
	}

	public static LocationRequest createLocationRequest() {
		LocationRequest locationRequest = LocationRequest.create();
		locationRequest.setInterval(10000);
		locationRequest.setFastestInterval(0);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		return locationRequest;
	}

}
