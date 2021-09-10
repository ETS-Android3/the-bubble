package huji.postpc.y2021.tal.yichye.thebubble;

import static android.content.ContentValues.TAG;
import static huji.postpc.y2021.tal.yichye.thebubble.LocationHelper.createLocationRequest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BackgroundLocationWorker extends Worker {

	Context context;

	public BackgroundLocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
		super(context, workerParams);
		this.context = context;
	}

	@NonNull
	@Override
	public Result doWork() {
		final Double[] latitude = new Double[1];
		final Double[] longitude = new Double[1];
		Log.i(TAG, "MyLocation worker starts");

		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			// TODO: have permission
			FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);


			LocationRequest locationRequest = createLocationRequest();
//			LocationCallback locationCallback = new LocationCallback() {
//				@Override
//				public void onLocationResult(@NonNull LocationResult locationResult) {
//					super.onLocationResult(locationResult);
//					Location location1 = locationResult.getLastLocation();
//					latitude[0] = location1.getLatitude();
//					longitude[0] = location1.getLongitude();
//					Log.i(TAG, "MyLocation" + latitude[0] + " " + longitude[0]);
//
//				}
//			};
//			locationRequest.setNumUpdates(3);
////			fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
//			fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

//			fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//				@SuppressLint("MissingPermission")
//				@Override
//				public void onComplete(@NonNull Task<Location> task) {
//					// Init location
//					Location location = task.getResult();
//					if (location != null) {
//						latitude[0] = location.getLatitude();
//						longitude[0] = location.getLongitude();
//						Log.i(TAG, "MyLocation" + latitude[0] + " " + longitude[0]);
//					}}});

			// Code for rec
			fusedLocationProviderClient.getCurrentLocation(locationRequest.getPriority(), new CancellationToken() {
				@Override
				public boolean isCancellationRequested() {
					return false;
				}

				@NonNull
				@Override
				public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
					return null;
				}
			}).addOnCompleteListener(new OnCompleteListener<Location>() {
				@SuppressLint("MissingPermission")
				@Override
				public void onComplete(@NonNull Task<Location> task) {
					// Init location
					Location location = task.getResult();
					if (location != null) {
						latitude[0] = location.getLatitude();
						longitude[0] = location.getLongitude();
						Log.i(TAG, "MyLocation " + latitude[0] + " " + longitude[0] + " first if");
//						saveLocationToFS(location);
					} else {
						// Request location updates
						LocationRequest locationRequest = createLocationRequest();
						LocationCallback locationCallback = new LocationCallback() {
							@Override
							public void onLocationResult(@NonNull LocationResult locationResult) {
								super.onLocationResult(locationResult);
								Location location1 = locationResult.getLastLocation();
								latitude[0] = location1.getLatitude();
								longitude[0] = location1.getLongitude();
								Log.i(TAG, "MyLocation " + latitude[0] + " " + longitude[0] + " callback");
//								saveLocationToFS(location);


//								if (latitude[0] != null) {
//									fusedLocationProviderClient.removeLocationUpdates(new LocationCallback() {
//										@Override
//										public void onLocationResult(@NonNull LocationResult locationResult) {
//											super.onLocationResult(locationResult);
//										}
//									});
//									Log.i(TAG, "MyLocation, removeLocationUpdates");
//								}
							}
						};
						locationRequest.setNumUpdates(1);
						fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
					}


//					if (ActivityCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//						// TODO: Consider calling
//						//    ActivityCompat#requestPermissions
//						// here to request the missing permissions, and then overriding
//						//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//						//                                          int[] grantResults)
//						// to handle the case where the user grants the permission. See the documentation
//						// for ActivityCompat#requestPermissions for more details.
//						return;
//					}

				}
			});

//		} else if (shouldShowRequestPermissionRationale() {
//			// TODO: show UI to explain the user why this permission is needed
		} else {
//			requestPermissionToForegroundLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION);
		}
		Log.i(TAG, "MyLocation worker ends");

//		if (latitude[0] != null && longitude[0] != null){
//			Log.i(TAG, "MyLocation" + latitude[0] + " " + longitude[0]);
//		}
//		if (longitude[0] != null) {
//			Log.i(TAG, "MyLocation" + "success");
//
//			return Result.success();
//		} else {
//			return Result.retry();
//		}
		return Result.success();

	}

	public void updateJsonFile(Location location, String userName) {
		StorageRefeference storageRefeference = storage
		JSONObject
	}


	public void saveLocationToFS(Location location) {
		String userName = "tal01";
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		DocumentReference locationsArray = db.collection("locations").document(userName);
		locationsArray.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				if (task.isSuccessful()) {
					DocumentSnapshot documentSnapshot = task.getResult();
					if (!documentSnapshot.exists()){
						Map<String, Object> dataDocument = new HashMap<>();
						ArrayList<Location> locationArrayList = new ArrayList<>();
						locationArrayList.add(location);
						dataDocument.put("locationArray", locationArrayList);
						locationsArray.set(dataDocument);
					} else {
						locationsArray.update("locationArray", FieldValue.arrayUnion(location)).addOnSuccessListener(new OnSuccessListener<Void>() {
							@Override
							public void onSuccess(Void unused) {
								Log.i(TAG, "MyLocation data added to FS");

							}
						}).addOnFailureListener(new OnFailureListener() {
							@Override
							public void onFailure(@NonNull Exception e) {
								Log.i(TAG, "MyLocation upload data to FS error: " + e.getMessage());
							}
						});
//								addOnCompleteListener(new OnCompleteListener<Void>() {
//							@Override
//							public void onComplete(@NonNull Task<Void> task) {
//								if (task.isSuccessful()) {
//									Log.i(TAG, "MyLocation data added to FS");
//								} else {
//									Log.i(TAG, "MyLocation upload data to FS error: " + task.getException());
//								}
//							}
//						});
					}

				} else {
					Log.i(TAG, "MyLocation FS error " + task.getException());
				}
			}
		});
	}


}
