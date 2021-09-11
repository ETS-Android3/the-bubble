package huji.postpc.y2021.tal.yichye.thebubble;

import static huji.postpc.y2021.tal.yichye.thebubble.LocationHelper.createLocationRequest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchAlgorithm {

	public static final double DEFAULT_SEARCH_RADIUS = 200;

	public MutableLiveData<ArrayList<String>> possibleMatchesLiveData = new MutableLiveData<>();
	public MutableLiveData<ArrayList<String>> possibleMatchesInRadiusLiveData = new MutableLiveData<>();
	MutableLiveData<Location> myCurrentLocation = new MutableLiveData<>();
	public MutableLiveData<Boolean> radiusSearchFinished = new MutableLiveData<>();
	public AtomicInteger numOfUsersChecked = new AtomicInteger(0);
	public Context activity;


	public SearchAlgorithm(Activity activity) {
		this.activity = activity;
	}

	public void SearchForPossibleMatches(ViewModelStoreOwner viewModelStoreOwner) {
		ArrayList<String> possibleMatches = new ArrayList<>();
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
			@Override
			public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
				UserViewModel userViewModel =  new ViewModelProvider(viewModelStoreOwner).get(UserViewModel.class);
				for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
					PersonData otherUser = document.toObject(PersonData.class);
					if (otherUser != null) {
						String userName = otherUser.getId();
						if (!userName.equals(userViewModel.getUserNameLiveData().getValue())){
							if (checkMatch(userViewModel, otherUser)){
								possibleMatches.add(otherUser.getId());
							}
						}
					}
				}
				possibleMatchesLiveData.setValue(possibleMatches);
			}
		});
	}


	public static boolean checkMatch(UserViewModel userViewModel, PersonData otherUser) {

		ArrayList<PersonData.Gender> myGenderTendency = userViewModel.getGenderTendencyLiveData().getValue();
		PersonData.Gender myGender = userViewModel.myGenderLiveData.getValue();
		Integer myMinAge = userViewModel.getMinAgePreferenceLiveData().getValue();
		Integer myMaxAge = userViewModel.getMaxAgePreferenceLiveData().getValue();

		Long myDateOfBirth = userViewModel.dateOfBirthLiveData.getValue();
		if (myGenderTendency == null || myGender == null || myMinAge == null || myMaxAge == null) {
			return false;
		}
		// checks gender preference
		if (!(myGenderTendency.contains(otherUser.gender) &&
				otherUser.genderTendency.contains(myGender))){
			return false;
		}
		// checks age preference
		int myAge = PersonData.calcAge(myDateOfBirth);
		int otherUserAge = PersonData.calcAge(otherUser.dateOfBirth); //todo to use getter
		if (otherUserAge < myMinAge || otherUserAge > myMaxAge ||
				myAge < otherUser.minAgePreference || myAge > otherUser.maxAgePreference) {
			return false;
		}
		return true;
	}

	private void getMyLocation(){
		if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
			LocationRequest locationRequest = createLocationRequest();
			fusedLocationProviderClient.getCurrentLocation(locationRequest.getPriority(), new CancellationToken() {
				@Override
				public boolean isCancellationRequested() {
					return false;
				}
				@NonNull
				@Override
				public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) { return null; }
			}).addOnCompleteListener(new OnCompleteListener<Location>() {
				@SuppressLint("MissingPermission")
				@Override
				public void onComplete(@NonNull Task<Location> task) {
					// Init location
					Location location = task.getResult();
					if (location != null) {
						myCurrentLocation.setValue(location);
					} else {
						// Request location updates
						LocationRequest locationRequest = createLocationRequest();
						LocationCallback locationCallback = new LocationCallback() {
							@Override
							public void onLocationResult(@NonNull LocationResult locationResult) {
								super.onLocationResult(locationResult);
								Location location1 = locationResult.getLastLocation();
								myCurrentLocation.setValue(location1);
							}
						};
						locationRequest.setNumUpdates(1);
						fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
					}
				}
			});
		}
	}

	public void searchInGivenRadius(double radius) {
		ArrayList<String> possibleMatches = this.getPossibleMatchesLiveData().getValue();
		getRadiusSearchFinished().setValue(false);
		getPossibleMatchesInRadiusLiveData().setValue(new ArrayList<>());
		if (possibleMatches != null && possibleMatches.size() != 0) {
			//TODO get my current location
			getMyLocation();
			myCurrentLocation.observe((LifecycleOwner) activity, new Observer<Location>() {
				@Override
				public void onChanged(Location location) {
					if (location != null) {
						searchInGivenRadiusHelper(location.getLatitude(), location.getLongitude(),
								possibleMatches, radius);
					}
				}
			});
			myCurrentLocation.removeObservers((LifecycleOwner) activity);
		}
	}

	private void searchInGivenRadiusHelper(double myLatitude, double myLongitude,
										   ArrayList<String> possibleMatches, double radius){
		Gson gson = new Gson();
		for (String userName : possibleMatches) {
			StorageReference ref = LocationHelper.createLocationReference(userName, LocationHelper.LOCATIONS_FILE_NAME);
			ref.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
				@Override
				public void onSuccess(byte[] bytes) {
					InputStream inputStream = new ByteArrayInputStream(bytes);
					try {
						JSONObject jsonObject = new JSONObject(new JsonParser().parse(new InputStreamReader(inputStream, "UTF-8")).getAsJsonObject().toString());
						String timeStr = jsonObject.keys().next(); // assumes that the file has only one location record (since we work with the lastLocationFile)
						HashMap<String, Double> locationMap = gson.fromJson(jsonObject.get(timeStr).toString(), HashMap.class);
						Double otherLatitude = locationMap.get("latitude");
						Double otherLongitude = locationMap.get("longitude");
						if (otherLatitude != null && otherLongitude != null) {
							if (distance(myLatitude, otherLatitude, myLongitude, otherLongitude, 0 ,0) <= radius) {
								updatePossibleMatchesInRadius(userName);
							}
						}
					} catch (UnsupportedEncodingException | JSONException e) {
						e.printStackTrace();
					}
					SearchAlgorithm.this.numOfUsersChecked.addAndGet(1);
					if (SearchAlgorithm.this.numOfUsersChecked.get() == possibleMatches.size()) {
						getRadiusSearchFinished().setValue(true);
					}
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception e) {
					SearchAlgorithm.this.numOfUsersChecked.addAndGet(1);
					if (SearchAlgorithm.this.numOfUsersChecked.get() == possibleMatches.size()) {
						getRadiusSearchFinished().setValue(true);
					}
				}
			});
		}

	}

	private void updatePossibleMatchesInRadius(String userNameToAdd){
		ArrayList<String> possibleMatchesInRadius = getPossibleMatchesInRadiusLiveData().getValue();
		if (possibleMatchesInRadius != null) {
			possibleMatchesInRadius.add(userNameToAdd);
			getPossibleMatchesInRadiusLiveData().setValue(possibleMatchesInRadius);
		}
	}

	public MutableLiveData<ArrayList<String>> getPossibleMatchesLiveData() {
		return possibleMatchesLiveData;
	}

	public MutableLiveData<Boolean> getRadiusSearchFinished() {
		return radiusSearchFinished;
	}

	public MutableLiveData<ArrayList<String>> getPossibleMatchesInRadiusLiveData() {
		return possibleMatchesInRadiusLiveData;
	}


	/**
	 * Calculate distance between two points in latitude and longitude taking
	 * into account height difference. If you are not interested in height
	 * difference pass 0.0. Uses Haversine method as its base.
	 *
	 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
	 * el2 End altitude in meters
	 * returns Distance in Meters
	 */
	public static double distance(double lat1, double lat2, double lon1,
								  double lon2, double el1, double el2) {

		final int R = 6371; // Radius of the earth

		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lon2 - lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
				* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // convert to meters

		double height = el1 - el2;

		distance = Math.pow(distance, 2) + Math.pow(height, 2);

		return Math.sqrt(distance);
	}

}
