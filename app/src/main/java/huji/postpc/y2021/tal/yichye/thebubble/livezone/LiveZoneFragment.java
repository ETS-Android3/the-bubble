package huji.postpc.y2021.tal.yichye.thebubble.livezone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import huji.postpc.y2021.tal.yichye.thebubble.R;
import huji.postpc.y2021.tal.yichye.thebubble.SearchAlgorithm;
import huji.postpc.y2021.tal.yichye.thebubble.TheBubbleApplication;
import huji.postpc.y2021.tal.yichye.thebubble.UserViewModel;

public class LiveZoneFragment extends Fragment {

	private MapHandler mapHandler;
	private UserViewModel userViewModel;
	private LayoutInflater layoutInflater;
	private ViewGroup containerGroup;
	private SearchAlgorithm algorithm;
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		layoutInflater = inflater;
		containerGroup = container;
		algorithm = new SearchAlgorithm(requireActivity());
		algorithm.SearchForPossibleMatches(requireActivity());
		algorithm.getPossibleMatchesLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
			@Override
			public void onChanged(ArrayList<String> userNames) {
				algorithm.searchInGivenRadius(SearchAlgorithm.DEFAULT_SEARCH_RADIUS);
			}
		});

		return inflater.inflate(R.layout.live_zone_fragment, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mapHandler = new MapHandler(view.findViewById(R.id.map));
		userViewModel =  new ViewModelProvider(requireActivity()).get(UserViewModel.class);
		String userId = userViewModel.getUserNameLiveData().getValue();

		TheBubbleApplication.getInstance().getUsersDB().getUserByID(userId).observe(getViewLifecycleOwner(), personData -> {
			Drawable personDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.person);
			mapHandler.showMarkerOnMap(personDrawable, personData, true);
//					mapHandler.setMyLocationOnMap(bitmap);
//					mapHandler.setCenter(personData);
		});
		showMatchesOnMap();

		algorithm.getRadiusSearchFinished().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
			@Override
			public void onChanged(Boolean aBoolean) {
				if (aBoolean){
					System.out.println(algorithm.possibleMatchesInRadiusLiveData.getValue());
					System.out.println(aBoolean);
					algorithm.getRadiusSearchFinished().setValue(false);
//					showMatchesOnMap();

				}
			}
		});
	}

	public void showMatchesOnMap()
	{
		// TODO GET ARRAY OF ALL MATCHES (ACCORDING TO LOCATION AND TENDENCY)
		//  AND UPDATE MAP WITH MARKER FOR EACH MATCH
		StorageReference reference = TheBubbleApplication.getInstance()
				.getImageStorageDB().createReference("testf", "profileImage");

		reference.getBytes(5000000).addOnSuccessListener(bytes -> {
			Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			RoundedBitmapDrawable circularDrawable = getCircularDrawable(bitmap);
			TheBubbleApplication.getInstance().getUsersDB().getUserByID("testf").
					observe(getViewLifecycleOwner(), personData ->
							mapHandler.showMarkerOnMap(circularDrawable, personData, false));
		});
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

}