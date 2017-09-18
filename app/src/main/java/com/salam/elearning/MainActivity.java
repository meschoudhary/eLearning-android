package com.salam.elearning;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.salam.elearning.Fragments.CategoryFragment;
import com.salam.elearning.Fragments.HomeFragment;
import com.salam.elearning.Fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.home_fragment, HomeFragment.class)
                .add(R.string.profile_fragment, ProfileFragment.class)
                .add(R.string.category_fragment, CategoryFragment.class)
                .create());

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        final SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        viewPagerTab.setCustomTabView(new SmartTabLayout.TabProvider() {

            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                ImageView icon = (ImageView) LayoutInflater.from(viewPagerTab.getContext()).inflate(R.layout.single_tab,container, false);
                switch (position) {
                    case 0:
                        icon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.home_selected));
                        break;
                    case 1:
                        icon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.history));
                        break;
                    case 2:
                        icon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.category));
                        break;
                    default:
                        throw new IllegalStateException("Invalid position: " + position);
                }
                return icon;
            }
        });

        viewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("scrolled", "scrolled");
                Log.d("position", position + "");
            }
            @Override
            public void onPageSelected(int position) {

                View tab = viewPagerTab.getTabAt(position);
                ImageView icon = tab.findViewById(R.id.tab_icon);

                View homeTab;
                ImageView homeTabIcon;

                View categoryTab;
                ImageView categoryTabIcon;

                View historyTab;
                ImageView historyTabIcon;

                switch (position) {
                    case 0:
                        icon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.home_selected));

                        historyTab = viewPagerTab.getTabAt(1);
                        historyTabIcon = historyTab.findViewById(R.id.tab_icon);
                        historyTabIcon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.history));

                        categoryTab = viewPagerTab.getTabAt(2);
                        categoryTabIcon = categoryTab.findViewById(R.id.tab_icon);
                        categoryTabIcon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.category));

                        break;
                    case 1:
                        icon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.history_selected));


                        homeTab = viewPagerTab.getTabAt(0);
                        homeTabIcon = homeTab.findViewById(R.id.tab_icon);
                        homeTabIcon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.home));

                        categoryTab = viewPagerTab.getTabAt(2);
                        categoryTabIcon = categoryTab.findViewById(R.id.tab_icon);
                        categoryTabIcon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.category));

                        break;
                    case 2:
                        icon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.category_selected));

                        homeTab = viewPagerTab.getTabAt(0);
                        homeTabIcon = homeTab.findViewById(R.id.tab_icon);
                        homeTabIcon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.home));

                        historyTab = viewPagerTab.getTabAt(1);
                        historyTabIcon = historyTab.findViewById(R.id.tab_icon);
                        historyTabIcon.setImageDrawable(ContextCompat.getDrawable(viewPagerTab.getContext(), R.drawable.history));

                        break;
                    default:
                        throw new IllegalStateException("Invalid position: " + position);
                }

            }
            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("scrolled_state_changed", state + "");
            }
        });
        viewPagerTab.setViewPager(viewPager);

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
        } else {
            super.onBackPressed(); //replaced
        }
    }
}
