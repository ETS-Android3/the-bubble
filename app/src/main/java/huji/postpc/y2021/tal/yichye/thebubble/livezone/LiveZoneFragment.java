package huji.postpc.y2021.tal.yichye.thebubble.livezone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.storage.StorageReference;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import java.util.ArrayList;
import java.util.HashMap;

import huji.postpc.y2021.tal.yichye.thebubble.PersonData;
import huji.postpc.y2021.tal.yichye.thebubble.R;
import huji.postpc.y2021.tal.yichye.thebubble.SearchAlgorithm;
import huji.postpc.y2021.tal.yichye.thebubble.TheBubbleApplication;
import huji.postpc.y2021.tal.yichye.thebubble.UserViewModel;

public class LiveZoneFragment extends Fragment {

	private MapHandler mapHandler;
	private UserViewModel userViewModel;
	private SearchAlgorithm algorithm;
	private CountDownTimer timeCounter = null;
	private MapView mapView;
	private static final long REFRESH_TIME_MILLISECONDS = 300000;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.live_zone_fragment, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		userViewModel =  new ViewModelProvider(requireActivity()).get(UserViewModel.class);
		mapView = view.findViewById(R.id.map);
		mapHandler = new MapHandler(mapView, userViewModel);
		mapView.setVisibility(View.GONE);

		timeCounter = new CountDownTimer(REFRESH_TIME_MILLISECONDS, REFRESH_TIME_MILLISECONDS) {
			public void onTick(long millisUntilFinished) {
			}

			public void onFinish() {
				Toast.makeText(view.getContext(), "Refresh", Toast.LENGTH_LONG).show();
				timeCounter.start();
				runLiveZoneSearch();
			}
		}.start();

		runLiveZoneSearch();
	}

	private void runLiveZoneSearch() {
		algorithm = new SearchAlgorithm(requireActivity());
		algorithm.SearchForPossibleMatches();
		algorithm.getPossibleMatchesLiveData().observe(requireActivity(),
				userNames -> algorithm.searchInGivenRadius(SearchAlgorithm.DEFAULT_SEARCH_RADIUS));

		String userId = userViewModel.getUserNameLiveData().getValue();

		algorithm.getRadiusSearchFinished().observe(getViewLifecycleOwner(), isFinish -> {
			if (isFinish) {
				CircularProgressIndicator loadingView = getView().findViewById(R.id.loading);
				TextView loadingTextView = getView().findViewById(R.id.loadingText);
				loadingView.setVisibility(View.GONE);
				loadingTextView.setVisibility(View.GONE);
				mapView.setVisibility(View.VISIBLE);
				mapHandler.cleanupMap();
				HashMap<String, Double> personLocationMap = getUserLocationHashMap();

				TheBubbleApplication.getInstance().getUsersDB().getUserByID(userId).
						observe(getViewLifecycleOwner(), personData -> {
					Drawable personDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.person);
					mapHandler.showMarkerOnMap(personDrawable, new Pair<>(personData, personLocationMap), true);
					algorithm.getRadiusSearchFinished().setValue(false);
					showMatchesOnMap(algorithm.possibleMatchesInRadiusLiveData.getValue());
				});
			}
		});
	}

	private HashMap<String, Double> getUserLocationHashMap(){
		GeoPoint geoPoint = algorithm.myLocation.getValue();
		HashMap<String, Double> personLocationMap = new HashMap<>();
		personLocationMap.put("longitude", geoPoint.getLongitude());
		personLocationMap.put("latitude", geoPoint.getLatitude());
		return personLocationMap;
	}

	public void showMatchesOnMap(ArrayList<Pair<PersonData, HashMap<String, Double>>> matches)
	{
		for (Pair<PersonData, HashMap<String, Double>> match: matches) {
			StorageReference reference = TheBubbleApplication.getInstance()
					.getImageStorageDB().createReference(match.first.getId(), "profileImage");

			reference.getBytes(700000).addOnSuccessListener(requireActivity(), bytes -> {
				Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				RoundedBitmapDrawable circularDrawable = getCircularDrawable(bitmap);
				mapHandler.showMarkerOnMap(circularDrawable, match, false);
			});
		}
	}

	private RoundedBitmapDrawable getCircularDrawable(Bitmap bitmap) {
		int size = 160;
		Bitmap centerCropBitmap = ThumbnailUtils.extractThumbnail(bitmap, size, size);
		RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), centerCropBitmap);
		roundedBitmapDrawable.setFilterBitmap(true);
		roundedBitmapDrawable.setAntiAlias(true);
		roundedBitmapDrawable.setCircular(true);

		return roundedBitmapDrawable;
	}

	@Override
	public void onDestroyView() {
		System.out.println("on destroy");
		mapHandler.cleanupMap();
		super.onDestroyView();
		if (timeCounter != null) {
			timeCounter.cancel();
		}
	}

	@Override
	public void onPause() {
		mapHandler.cleanupMap();
		System.out.println("on pause");
		super.onPause();
	}

	@Override
	public void onResume() {
		System.out.println("on resume");

		super.onResume();
	}

	@Override
	public void onStop() {
		System.out.println("on stop");

		super.onStop();
	}
}