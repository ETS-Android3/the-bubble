package huji.postpc.y2021.tal.yichye.thebubble;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {

	private MainActivity2 instance;
	private ActivityResultLauncher<String> requestPermissionToForegroundLocation;

	private final long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
	private final long FASTEST_INTERVAL = 2000; /* 2 sec */
	FusedLocationProviderClient fusedLocationProviderClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		instance = this;

		Button getLocationButton = findViewById(R.id.getLocationButton);
		TextView latitudeView = findViewById(R.id.latitude);
		TextView longitudeView = findViewById(R.id.longitude);
		TextView countryView = findViewById(R.id.country);
		TextView localityView = findViewById(R.id.locality);

//		LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//				.addLocationRequest(mLocationRequestHighAccuracy)
//				.addLocationRequest(mLocationRequestBalancedPowerAccuracy);

		requestPermissionToForegroundLocation = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
			if (isGranted) {
				// everything is ok, continue
			} else {
				// explain to user
			}
		});


		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


		getLocationButton.setOnClickListener(new View.OnClickListener() {
			@RequiresApi(api = Build.VERSION_CODES.M)
			@Override
			public void onClick(View v) {
				if (ContextCompat.checkSelfPermission(
						MainActivity2.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
						PackageManager.PERMISSION_GRANTED) {
					// TODO: have permission

					// Code for rec
					fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
						@Override
						public void onComplete(@NonNull Task<Location> task) {
							// Init location
							Location location = task.getResult();
							if (location != null) {
								try {
									// Init geoCoder
									Geocoder geocoder = new Geocoder(MainActivity2.this,
											Locale.getDefault());
									// Init address list
									List<Address> addresses = geocoder.getFromLocation(
											location.getLatitude(), location.getLongitude(), 1
									);
									latitudeView.setText("Latitude: " + addresses.get(0).getLatitude());
									longitudeView.setText("Longitude: " + addresses.get(0).getLongitude());
									countryView.setText("County: " + addresses.get(0).getCountryName());
									localityView.setText("Locality: " + addresses.get(0).getLocality());
									// TODO: do something with received location
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					});
				} else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
					// TODO: show UI to explain the user why this permission is needed
				} else {
					requestPermissionToForegroundLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION);
				}
			}
		});


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