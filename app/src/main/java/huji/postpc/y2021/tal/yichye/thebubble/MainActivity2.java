//package huji.postpc.y2021.tal.yichye.thebubble;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.lifecycle.Observer;
//import androidx.work.ExistingPeriodicWorkPolicy;
//import androidx.work.PeriodicWorkRequest;
//import androidx.work.WorkManager;
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import java.util.ArrayList;
//import java.util.concurrent.TimeUnit;
//
//public class MainActivity2 extends AppCompatActivity {
//
//	private MainActivity2 instance;
//
//	private final long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
//	private final long FASTEST_INTERVAL = 2000; /* 2 sec */
//	private final int PERMISSION_ID = 44;
//
//	FusedLocationProviderClient fusedLocationProviderClient;
//
//	TextView localityView;
//
//	@RequiresApi(api = Build.VERSION_CODES.Q)
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main2);
//		instance = this;
////
////		Button getLocationButton = findViewById(R.id.getLocationButton);
////		TextView latitudeView = findViewById(R.id.latitude);
////		TextView longitudeView = findViewById(R.id.longitude);
////		TextView countryView = findViewById(R.id.country);
////		localityView = findViewById(R.id.locality);
//
//
////		WorkManager.getInstance(this).cancelAllWork();
//
////		if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
////			startBackgroundWorker();
////			SearchAlgorithm algorithm = new SearchAlgorithm(this);
////			algorithm.SearchForPossibleMatches(this);
////			algorithm.getPossibleMatchesLiveData().observe(this, new Observer<ArrayList<String>>() {
////				@Override
////				public void onChanged(ArrayList<String> userNames) {
////					algorithm.searchInGivenRadius(SearchAlgorithm.DEFAULT_SEARCH_RADIUS);
////				}
////			});
////		} else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
////			Toast.makeText(MainActivity2.this, "The feature can't work without BACKGROUDN LOCATION", Toast.LENGTH_LONG).show();
////			ActivityCompat.requestPermissions(this, new String[]{
////					Manifest.permission.ACCESS_COARSE_LOCATION,
////					Manifest.permission.ACCESS_FINE_LOCATION,
////					Manifest.permission.ACCESS_BACKGROUND_LOCATION
////			}, PERMISSION_ID);
////		} else {
////			ActivityCompat.requestPermissions(this, new String[]{
////					Manifest.permission.ACCESS_COARSE_LOCATION,
////					Manifest.permission.ACCESS_FINE_LOCATION,
////					Manifest.permission.ACCESS_BACKGROUND_LOCATION
////			}, PERMISSION_ID);
////		}
////
////	}
//
////	@Override
////	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
////		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
////		if (requestCode == PERMISSION_ID) {
////			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////				startBackgroundWorker();
////				SearchAlgorithm algorithm = new SearchAlgorithm(this);
////				algorithm.SearchForPossibleMatches(this);
////				algorithm.getPossibleMatchesLiveData().observe(this, new Observer<ArrayList<String>>() {
////					@Override
////					public void onChanged(ArrayList<String> userNames) {
////						algorithm.searchInGivenRadius(SearchAlgorithm.DEFAULT_SEARCH_RADIUS);
////					}
////				});
////			}
////		}
////	}
//
////	private void startBackgroundWorker(){
////		WorkManager workManager = WorkManager.getInstance(this);
////		PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(BackgroundLocationWorker.class,
////				15, TimeUnit.MINUTES)
////				.addTag("background")
////				.build();
////		workManager.enqueueUniquePeriodicWork(
////				"background",
////				ExistingPeriodicWorkPolicy.KEEP,
////				periodicWorkRequest); // run worker every 15 min
////
////	}
//	}
//}