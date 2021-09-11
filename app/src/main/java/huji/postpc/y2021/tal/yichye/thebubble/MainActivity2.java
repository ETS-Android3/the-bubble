package huji.postpc.y2021.tal.yichye.thebubble;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity2 extends AppCompatActivity {

	private MainActivity2 instance;
	private ActivityResultLauncher<String> requestPermissionToForegroundLocation;

	private final long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
	private final long FASTEST_INTERVAL = 2000; /* 2 sec */
	private final int PERMISSION_ID = 44;

	FusedLocationProviderClient fusedLocationProviderClient;

	TextView localityView;

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		instance = this;

		Button getLocationButton = findViewById(R.id.getLocationButton);
		TextView latitudeView = findViewById(R.id.latitude);
		TextView longitudeView = findViewById(R.id.longitude);
		TextView countryView = findViewById(R.id.country);
		localityView = findViewById(R.id.locality);

//		LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//				.addLocationRequest(mLocationRequestHighAccuracy)
//				.addLocationRequest(mLocationRequestBalancedPowerAccuracy);


		WorkManager.getInstance(this).cancelAllWork();
		// TODO: To ask also for coarse location
		requestPermissionToForegroundLocation = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
			if (isGranted) {
				// everything is ok, continue
			} else {
				// explain to user
			}
		});
		ActivityResultLauncher<String> requestPermissionLauncher =
				registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
					if (isGranted) {
						// Permission is granted. Continue the action or workflow in your
						// app.
						startBackgroundWorker();
					} else {
						// Explain to the user that the feature is unavailable because the
						// features requires a permission that the user has denied. At the
						// same time, respect the user's decision. Don't link to system
						// settings in an effort to convince the user to change their
						// decision.
						Toast.makeText(MainActivity2.this, "The feature can't work without BACKGROUDN LOCATION", Toast.LENGTH_LONG).show();
					}
				});

		if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
			startBackgroundWorker();
			SearchAlgorithm algorithm = new SearchAlgorithm(this);
			algorithm.SearchForPossibleMatches(this);
			algorithm.getPossibleMatchesLiveData().observe(this, new Observer<ArrayList<String>>() {
				@Override
				public void onChanged(ArrayList<String> userNames) {
					algorithm.searchInGivenRadius(SearchAlgorithm.DEFAULT_SEARCH_RADIUS);
				}
			});
		} else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
			Toast.makeText(MainActivity2.this, "The feature can't work without BACKGROUDN LOCATION", Toast.LENGTH_LONG).show();
//			requestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
			ActivityCompat.requestPermissions(this, new String[]{
					Manifest.permission.ACCESS_COARSE_LOCATION,
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_BACKGROUND_LOCATION
			}, PERMISSION_ID);
		} else {
//			requestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
			ActivityCompat.requestPermissions(this, new String[]{
					Manifest.permission.ACCESS_COARSE_LOCATION,
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_BACKGROUND_LOCATION
			}, PERMISSION_ID);
		}

	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PERMISSION_ID) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				startBackgroundWorker();
				SearchAlgorithm algorithm = new SearchAlgorithm(this);
				algorithm.SearchForPossibleMatches(this);
				algorithm.getPossibleMatchesLiveData().observe(this, new Observer<ArrayList<String>>() {
					@Override
					public void onChanged(ArrayList<String> userNames) {
						algorithm.searchInGivenRadius(SearchAlgorithm.DEFAULT_SEARCH_RADIUS);
					}
				});
			}
		}
	}

	private void startBackgroundWorker(){
		WorkManager workManager = WorkManager.getInstance(this);
		PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(BackgroundLocationWorker.class,
				15, TimeUnit.MINUTES)
				.addTag("background")
				.build();
		workManager.enqueueUniquePeriodicWork(
				"background",
				ExistingPeriodicWorkPolicy.KEEP,
				periodicWorkRequest); // run worker every 15 min

	}

//	private class GeoHandler extends Handler {
//		@Override
//		public void handleMessage(@NonNull Message msg) {
//			super.handleMessage(msg);
//			String address;
//			if (msg.what == 1) {
//				Bundle bundle = msg.getData();
//				address = bundle.getString("address");
//			} else {
//				address = null;
//			}
//			// TODO do sonething with address
//			localityView.setText(address);
//		}
//	}



//		LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//				.addLocationRequest(locationRequest);
//
//
//		SettingsClient client = LocationServices.getSettingsClient(this);
//		Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
//
//		fusedLocationProviderClient.requestLocationUpdates()




	private void requestPermissions() {
		ActivityCompat.requestPermissions(this, new String[]{
				Manifest.permission.ACCESS_COARSE_LOCATION,
				Manifest.permission.ACCESS_FINE_LOCATION}, 44);
	}
	void getLocation() {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}

	}


//	private void startLocationUpdates() {
//		fusedLocationClient.requestLocationUpdates(locationRequest,
//				locationCallback,
//				Looper.getMainLooper());
//	}
// Trigger new location updates at interval
protected void startLocationUpdates() {

	// Create the location request to start receiving updates
	LocationRequest mLocationRequest = new LocationRequest();
	mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	mLocationRequest.setInterval(UPDATE_INTERVAL);
	mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

	// Create LocationSettingsRequest object using location request
	LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
	builder.addLocationRequest(mLocationRequest);
	LocationSettingsRequest locationSettingsRequest = builder.build();

	// Check whether location settings are satisfied
	// https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
	SettingsClient settingsClient = LocationServices.getSettingsClient(this);
	settingsClient.checkLocationSettings(locationSettingsRequest);

	// new Google API SDK v11 uses getFusedLocationProviderClient(this)
//	getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
//				@Override
//				public void onLocationResult(LocationResult locationResult) {
//					// do work here
//					onLocationChanged(locationResult.getLastLocation());
//				}
//			},
//			Looper.myLooper());
}
}