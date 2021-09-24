package huji.postpc.y2021.tal.yichye.thebubble.Connections;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull Fragment fragment)
    {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return  new MessagingFragment();
            case 1:
                return  new RequestsFragment();
        }
        return new MessagingFragment();
    }
    @Override
    public int getItemCount() {return 2; }
}