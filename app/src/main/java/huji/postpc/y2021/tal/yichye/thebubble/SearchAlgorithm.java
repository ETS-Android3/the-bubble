package huji.postpc.y2021.tal.yichye.thebubble;

import static android.content.ContentValues.TAG;
import static huji.postpc.y2021.tal.yichye.thebubble.LocationHelper.createLocationRequest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;

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
import org.osmdroid.util.GeoPoint;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import huji.postpc.y2021.tal.yichye.thebubble.Connections.Request;

public class SearchAlgorithm {

	public static final double DEFAULT_SEARCH_RADIUS = 200;

	public MutableLiveData<ArrayList<PersonData>> possibleMatchesLiveData = new MutableLiveData<>();
	public MutableLiveData<ArrayList<PersonData>> possibleMatchesAgentLiveData = new MutableLiveData<>();

	public MutableLiveData<ArrayList<Pair<PersonData, HashMap<String, Double>>>>
			possibleMatchesInRadiusLiveData = new MutableLiveData<>();
	public MutableLiveData<Location> myCurrentLocation = new MutableLiveData<>();
	public MutableLiveData<Boolean> radiusSearchFinished = new MutableLiveData<>();
	public MutableLiveData<Boolean> agentSearchFinished = new MutableLiveData<>();
	public AtomicInteger numOfUsersCheckedRadiusSearch = new AtomicInteger(0);
	public AtomicInteger numOfUsersCheckedAgentSearch = new AtomicInteger(0);
	public Context activity;
	private final UserViewModel userViewModel;
	private final Gson gson;

	public MutableLiveData<GeoPoint> myLocation = new MutableLiveData<>();

	public SearchAlgorithm(Activity activity) {
		this.activity = activity;
		this.userViewModel =  new ViewModelProvider((ViewModelStoreOwner) activity).get(UserViewModel.class);
		this.gson = new Gson();
	}

	public void SearchForPossibleMatches() {
		ArrayList<PersonData> possibleMatches = new ArrayList<>();
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
			@Override
			public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
				for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
					PersonData otherUser = document.toObject(PersonData.class);
					if (otherUser != null) {
						String userName = otherUser.getId();
						if (!userName.equals(userViewModel.getUserNameLiveData().getValue())){
							if (checkMatch(userViewModel, otherUser)){
								possibleMatches.add(otherUser);
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
		int otherUserAge = PersonData.calcAge(otherUser.dateOfBirth);
		return otherUserAge >= myMinAge && otherUserAge <= myMaxAge &&
				myAge >= otherUser.minAgePreference && myAge <= otherUser.maxAgePreference;
	}

	private void getMyLocation(){
		// TODO - CHECK WHICH PERMISSION IS NEEDED HERE
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
				public CancellationToken onCanceledRequested(
						@NonNull OnTokenCanceledListener onTokenCanceledListener) { return null; }
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
		ArrayList<PersonData> possibleMatches = this.getPossibleMatchesLiveData().getValue();
		getRadiusSearchFinished().setValue(false);
		getPossibleMatchesInRadiusLiveData().setValue(new ArrayList<>());
		if (possibleMatches != null) {
			//TODO get my current location
			getMyLocation();
			myCurrentLocation.observe((LifecycleOwner) activity, new Observer<Location>() {
				@Override
				public void onChanged(Location location) {
					if (location != null) {
						searchInGivenRadiusHelper(location.getLatitude(), location.getLongitude(),
								possibleMatches, radius);
					}
					myCurrentLocation.removeObservers((LifecycleOwner) activity);
				}
			});
		}
	}

	private void searchInGivenRadiusHelper(double myLatitude, double myLongitude,
										   ArrayList<PersonData> possibleMatches, double radius){
		numOfUsersCheckedRadiusSearch.set(0);
		for (PersonData user : possibleMatches) {
			String userName = user.getUserName();
			StorageReference ref = LocationHelper.createLocationReference(userName,
					LocationHelper.LAST_LOCATION_FILE_NAME);
			ref.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
				@Override
				public void onSuccess(byte[] bytes) {
					InputStream inputStream = new ByteArrayInputStream(bytes);
					try {
						JSONObject jsonObject = new JSONObject(
								new JsonParser().parse(new InputStreamReader(inputStream,
										"UTF-8")).getAsJsonObject().toString());
						String timeStr = jsonObject.keys().next();
						HashMap<String, Double> locationMap = gson.fromJson(
								jsonObject.get(timeStr).toString(), HashMap.class);
						Double otherLatitude = locationMap.get("latitude");
						Double otherLongitude = locationMap.get("longitude");
						if (otherLatitude != null && otherLongitude != null) {
							if (distance(myLatitude, otherLatitude, myLongitude,
									otherLongitude, 0 ,0) <= radius) {
								updatePossibleMatchesInRadius(user, locationMap);
							}
						}
					} catch (UnsupportedEncodingException | JSONException e) {
						e.printStackTrace();
					}
					SearchAlgorithm.this.numOfUsersCheckedRadiusSearch.addAndGet(1);
					if (SearchAlgorithm.this.numOfUsersCheckedRadiusSearch.get() == possibleMatches.size()) {
						myLocation.setValue(new GeoPoint(myLatitude, myLongitude));
						getRadiusSearchFinished().setValue(true);
					}
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception e) {
					SearchAlgorithm.this.numOfUsersCheckedRadiusSearch.addAndGet(1);
					if (SearchAlgorithm.this.numOfUsersCheckedRadiusSearch.get() == possibleMatches.size()) {
						getRadiusSearchFinished().setValue(true);
					}
				}
			});
		}
		if (possibleMatches.size() == 0)
		{
			myLocation.setValue(new GeoPoint(myLatitude, myLongitude));
			getRadiusSearchFinished().setValue(true);
		}
	}

	private void updatePossibleMatchesInRadius(PersonData userToAdd, HashMap<String, Double> locationMap){
		ArrayList<Pair<PersonData, HashMap<String, Double>>> possibleMatchesInRadius = getPossibleMatchesInRadiusLiveData().getValue();
		if (possibleMatchesInRadius != null) {
			possibleMatchesInRadius.add(new Pair<>(userToAdd, locationMap));
			getPossibleMatchesInRadiusLiveData().setValue(possibleMatchesInRadius);
		}
	}

	private void updatePossibleMatchesAgent(PersonData userToAdd){
		ArrayList<PersonData> possibleMatchesAgent = getPossibleMatchesAgentLiveData().getValue();
		if (possibleMatchesAgent != null) {
			possibleMatchesAgent.add(userToAdd);
			getPossibleMatchesAgentLiveData().setValue(possibleMatchesAgent);
		}
	}

	public MutableLiveData<ArrayList<PersonData>> getPossibleMatchesLiveData() {
		return possibleMatchesLiveData;
	}

	public MutableLiveData<ArrayList<PersonData>> getPossibleMatchesAgentLiveData() {
		return possibleMatchesAgentLiveData;
	}

	public MutableLiveData<Boolean> getRadiusSearchFinished() {
		return radiusSearchFinished;
	}

	public MutableLiveData<Boolean> getAgentSearchFinished() {
		return agentSearchFinished;
	}

	public MutableLiveData<ArrayList<Pair<PersonData, HashMap<String, Double>>>> getPossibleMatchesInRadiusLiveData() {
		return possibleMatchesInRadiusLiveData;
	}


	public void activateAgentSearch() {
		numOfUsersCheckedAgentSearch.set(0);
		getPossibleMatchesAgentLiveData().setValue(new ArrayList<>());
		double similarityDist = 100;
		String myUserName = userViewModel.getUserNameLiveData().getValue();
		StorageReference myUserRef = LocationHelper.createLocationReference(myUserName, LocationHelper.LOCATIONS_FILE_NAME);
		myUserRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
			@Override
			public void onSuccess(byte[] MyBytes) {
				InputStream myInputStream = new ByteArrayInputStream(MyBytes);
				try {
					JSONObject myJsonObject = new JSONObject(new JsonParser().parse(
							new InputStreamReader(myInputStream, "UTF-8")).getAsJsonObject().toString());

					ArrayList<PersonData> possibleMatches = getPossibleMatchesLiveData().getValue();
					ArrayList<String> ignoreList = userViewModel.getIgnoreListLiveData().getValue();
					ArrayList<Request> requests = userViewModel.getRequestsLiveData().getValue();

					if (possibleMatches != null) {
						for (PersonData possibleMatch : possibleMatches) {
							String otherUserName = possibleMatch.getUserName();
							boolean inRequests = false;
							if (requests != null) {
								for (Request request : requests) {
									if (request.getReqUserId().equals(otherUserName)) {
										inRequests = true;
										break;
									}
								}
							}
							boolean inIgnore = false;
							if (ignoreList != null && ignoreList.contains(otherUserName)) {
								inIgnore = true;
							}
							if (!inRequests && !inIgnore) {
								StorageReference otherUserRef =
										LocationHelper.createLocationReference(otherUserName,
												LocationHelper.LOCATIONS_FILE_NAME);
								otherUserRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(otherBytes -> {
									InputStream otherInputStream = new ByteArrayInputStream(otherBytes);
									try {
										JSONObject otherJsonObject = new JSONObject(
												new JsonParser().parse(new InputStreamReader(
														otherInputStream, "UTF-8")).
														getAsJsonObject().toString());

										Iterator<String> otherKeys = otherJsonObject.keys();
										while (otherKeys.hasNext()) {
											String otherKey = otherKeys.next();
											Iterator<String> myKeys = myJsonObject.keys();
											boolean matchFound = false;
											while (myKeys.hasNext()){
												String myKey = myKeys.next();

												HashMap<String, Double> myLocationMap =
														gson.fromJson(myJsonObject.get(myKey).
																toString(), HashMap.class);
												Double myLatitude = myLocationMap.get("latitude");
												Double myLongitude = myLocationMap.get("longitude");
												Double myCount = myLocationMap.get("count");

												HashMap<String, Double> otherLocationMap =
														gson.fromJson(otherJsonObject.get(otherKey).
																toString(), HashMap.class);
												Double otherLatitude = otherLocationMap.get("latitude");
												Double otherLongitude = otherLocationMap.get("longitude");
												Double otherCount = otherLocationMap.get("count");

												if (myLatitude != null && myLongitude != null && myCount != null &&
														otherLatitude != null && otherLongitude != null && otherCount != null) {
													if (myCount > 1 && otherCount > 1 &&
															distance(myLatitude, otherLatitude, myLongitude, otherLongitude, 0 ,0) <= similarityDist) {
														matchFound = true;
														break;
													}
												}
											}
											if (matchFound) {
												updatePossibleMatchesAgent(possibleMatch);
												break;
											}
										}
									} catch (JSONException | UnsupportedEncodingException e) {
										e.printStackTrace();
									}
									numOfUsersCheckedAgentSearch.addAndGet(1);
									if (numOfUsersCheckedAgentSearch.get() == possibleMatches.size()) {
										agentSearchFinished.setValue(true);
									}
								}).addOnFailureListener(new OnFailureListener() {
									@Override
									public void onFailure(@NonNull Exception e) {
										numOfUsersCheckedAgentSearch.addAndGet(1);
										if (numOfUsersCheckedAgentSearch.get() == possibleMatches.size()) {
											agentSearchFinished.setValue(true);
										}
									}
								});
							} else {
								numOfUsersCheckedAgentSearch.addAndGet(1);
								if (numOfUsersCheckedAgentSearch.get() == possibleMatches.size()) {
									agentSearchFinished.setValue(true);
								}
							}


						}
					}
				} catch (UnsupportedEncodingException | JSONException e) {
					e.printStackTrace();
				}
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Log.i(TAG, "SearchAlgorithm: activateAgentSearch. Can't download myUser locations: " + e.getMessage());
			}
		});
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
