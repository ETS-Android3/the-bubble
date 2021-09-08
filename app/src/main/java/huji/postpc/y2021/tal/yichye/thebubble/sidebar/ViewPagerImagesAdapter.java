package huji.postpc.y2021.tal.yichye.thebubble.sidebar;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

import huji.postpc.y2021.tal.yichye.thebubble.GlideApp;


public class ViewPagerImagesAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<StorageReference> imageRefs;


    ViewPagerImagesAdapter(Context context, ArrayList<StorageReference> imageRefs) {
        this.context = context;
        this.imageRefs = imageRefs;
    }

    @Override
    public int getCount() {
        return imageRefs.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        GlideApp.with(this.context)
                .load(imageRefs.get(position))
                .centerCrop()
                .into(imageView);

        container.addView(imageView);

        // TODO: add click listener for changing images
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}