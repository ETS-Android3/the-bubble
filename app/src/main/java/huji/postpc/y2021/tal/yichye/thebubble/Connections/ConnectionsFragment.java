package huji.postpc.y2021.tal.yichye.thebubble.Connections;


import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import huji.postpc.y2021.tal.yichye.thebubble.Connections.ViewPagerAdapter;
import huji.postpc.y2021.tal.yichye.thebubble.R;

public class ConnectionsFragment extends Fragment {

    TabLayout tabLayout;
    ViewPagerAdapter viewPagerAdapter;
    ViewPager2 viewPager;

    public ConnectionsFragment(){
        super(R.layout.fragment_connections);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = view.findViewById(R.id.connectionsTabLayout);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager = view.findViewById(R.id.tabsViewPager);
        viewPager.setAdapter(viewPagerAdapter);


        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0 ){
                        tab.setText("Conversations");
                    }
                    else tab.setText("BTB Requests");

                }).attach();

    }


}
