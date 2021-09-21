package huji.postpc.y2021.tal.yichye.thebubble;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import huji.postpc.y2021.tal.yichye.thebubble.Connections.Request;
import huji.postpc.y2021.tal.yichye.thebubble.sidebar.ViewPagerImagesAdapter;

public class AgentAdapter extends RecyclerView.Adapter<AgentAdapter.AgentPagerViewHolder> {

	private ArrayList<PersonData> possibleMatches;
	private UserViewModel userViewModel;
	private UsersDB usersDB;

	static class AgentPagerViewHolder extends RecyclerView.ViewHolder {

		public AgentPagerViewHolder(@NonNull View itemView) {
			super(itemView);
		}
	}

	public AgentAdapter(ArrayList<PersonData> possibleMatches, UserViewModel userViewModel) {
		this.possibleMatches = possibleMatches;
		this.userViewModel = userViewModel;
		this.usersDB = TheBubbleApplication.getInstance().getUsersDB();
	};

	@NonNull
	@Override
	public AgentPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.agent_item, parent, false);
		return new AgentPagerViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull AgentPagerViewHolder holder, int position) {
		PersonData personData = this.possibleMatches.get(position);
//		 TODO set information on view object
		ImageView profileImage = holder.itemView.findViewById(R.id.profileImageViewAgent);
		TextView nameView = holder.itemView.findViewById(R.id.name);
		MaterialButton bubbleInButton = holder.itemView.findViewById(R.id.bubble_in_button);
		MaterialButton bubbleOutButton = holder.itemView.findViewById(R.id.bubble_out_button);

		profileImage.bringToFront();
		nameView.bringToFront();
		nameView.bringToFront();

		nameView.setText(personData.getName() + ", " + Integer.toString(personData.getAge()));

		StorageReference profileImageRef = TheBubbleApplication.getInstance().getImageStorageDB().createReference(personData.getId(), "profileImage");

		GlideApp.with(holder.itemView).load(profileImageRef).into(profileImage);
		if (position == 0){
			holder.itemView.findViewById(R.id.arrow_up).setVisibility(View.GONE);
		}
		if (position == getItemCount() - 1) {
			holder.itemView.findViewById(R.id.arrow_down).setVisibility(View.GONE);
		}
		holder.itemView.findViewById(R.id.circle).setClipToOutline(true);
		profileImage.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public void onClick(View view) {
				LayoutInflater inflater = (LayoutInflater) holder.itemView.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
				View popupView = inflater.inflate(R.layout.full_person_info_window, null);
				int width = holder.itemView.getWidth() - 20;
				int height = holder.itemView.getHeight() - 20;
				boolean focusable = true;
				final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
				popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, -30);
//				popupView.setOnTouchListener((v, event) -> {
//					popupWindow.dismiss();
//					return true;
//				});
				updateFullPopupViews(popupView, personData, profileImageRef);

			}
		});


		bubbleInButton.setOnClickListener(v -> {
			Request requestIn = new Request(userViewModel.getUserNameLiveData().getValue(), true);
			Request requestOut = new Request(personData.getId(), false);
			usersDB.addRequest(userViewModel.getUserNameLiveData().getValue(), requestOut);
			usersDB.addRequest(personData.getId(), requestIn);
			this.possibleMatches.remove(position);
			this.notifyItemRemoved(position);
		});

		bubbleOutButton.setOnClickListener(v -> {
			this.possibleMatches.remove(position);
			usersDB.addToIgnoreList(userViewModel.getUserNameLiveData().getValue(), personData.getId());
			this.notifyItemRemoved(position);
		});

	}

	@Override
	public int getItemCount() {
		return this.possibleMatches.size();
	}

	private void updateFullPopupViews(View popUpView, PersonData personData, StorageReference profileImageRef)
	{
		ImageStorageDB storageDB = TheBubbleApplication.getInstance().getImageStorageDB();
		TextView aboutMeView = popUpView.findViewById(R.id.aboutMeEditProfile);
		TextView fullNameView = popUpView.findViewById(R.id.nameEditProfile);
		TextView ageView = popUpView.findViewById(R.id.ageEditProfile);
		TextView cityView = popUpView.findViewById(R.id.city);
		TextView genderView = popUpView.findViewById(R.id.genderEditProfile);
		ImageView profileImage = popUpView.findViewById(R.id.profileImageView);

		aboutMeView.setMovementMethod(new ScrollingMovementMethod());

		String userName = personData.userName;
		ArrayList<StorageReference> imagesRefs = new ArrayList<>();
		for (String imageName: personData.photos) {
			imagesRefs.add(storageDB.createReference(userName, imageName));
		}

		ViewPager viewPagerImages = popUpView.findViewById(R.id.view_pager);
		ViewPagerImagesAdapter imagesAdapter = new ViewPagerImagesAdapter(popUpView.getContext(), imagesRefs);
		viewPagerImages.setAdapter(imagesAdapter);

		fullNameView.setText(personData.getName());
		ageView.setText(personData.getAge() +"");
		aboutMeView.setText(personData.aboutMe);
		cityView.setText(personData.city);
		genderView.setText(personData.gender.toString().toLowerCase() +"");

		GlideApp.with(popUpView.getContext()).load(profileImageRef).centerCrop().into(profileImage);
	}

}
