package huji.postpc.y2021.tal.yichye.thebubble;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

public class MainActivity2 extends AppCompatActivity {

	private ActivityResultLauncher<String> requestPermissionToForegroundLocation;

	private final long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
	private final long FASTEST_INTERVAL = 2000; /* 2 sec */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);


		Button liveZoneButton = findViewById(R.id.liveZoneButton);
		final MainActivity2 mainActivity = this;


		LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
				.addLocationRequest(mLocationRequestHighAccuracy)
				.addLocationRequest(mLocationRequestBalancedPowerAccuracy);

		requestPermissionToForegroundLocation = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
			if (isGranted) {
				// everything is ok, continue
			} else {
				// explain to user
			}
		});


		liveZoneButton.setOnClickListener(new View.OnClickListener() {
			@RequiresApi(api = Build.VERSION_CODES.M)
			@Override
			public void onClick(View v) {
				if (ContextCompat.checkSelfPermission(
						mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) ==
						PackageManager.PERMISSION_GRANTED) {
					// TODO: have permission
				} else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
					// TODO: show UI to explain the user why this permission is needed
				} else {
					requestPermissionToForegroundLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION);
				}
			}
		});
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
	getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
				@Override
				public void onLocationResult(LocationResult locationResult) {
					// do work here
					onLocationChanged(locationResult.getLastLocation());
				}
			},
			Looper.myLooper());
}
}