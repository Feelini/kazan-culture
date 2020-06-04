package ru.balandina.kazan.viewer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.balandina.kazan.R;
import ru.balandina.kazan.map.MapFragment;
import ru.balandina.kazan.map.MarkerData;
import ru.balandina.kazan.models.ObjectEntity;
import ru.balandina.kazan.viewer.fragments.DescriptionFragment;
import ru.balandina.kazan.viewer.fragments.ImagesFragment;
import ru.balandina.kazan.viewer.fragments.TransportFragment;

public class ViewerFragment extends Fragment {

    private static ViewerFragment instance;
    private static ObjectEntity objectEntity;
    private Unbinder unbinder;
    @BindView(R.id.viewerTabs)
    TabLayout viewerTabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    public static ViewerFragment newInstance(ObjectEntity object){
        objectEntity = object;
        if (instance == null){
            instance = new ViewerFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_viewer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), 0);
        viewPagerAdapter.addFragment(DescriptionFragment.getInstance(objectEntity.getTitle(), objectEntity.getDescription()), null);
        viewPagerAdapter.addFragment(ImagesFragment.getInstance(listAssetFiles(Long.toString(objectEntity.getId()))), null);
        viewPagerAdapter.addFragment(TransportFragment.getInstance(objectEntity.getNearbyMetroStation(), objectEntity.getRouteFromTheCityCenter()), null);
        viewPager.setAdapter(viewPagerAdapter);

        viewerTabs.setupWithViewPager(viewPager);
        setCustomTabs(tabIcon, viewerTabs);
    }

    private static final int[] tabIcon = {R.drawable.ic_description, R.drawable.ic_image, R.drawable.ic_directions_bus};

    private void setCustomTabs(int[] icons, TabLayout tabLayout) {
        for (int i = 0; i < icons.length; i++) {
            View view = getLayoutInflater().inflate(R.layout.custom_tab, null);
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            view.findViewById(R.id.icon).setBackgroundResource(icons[i]);
            if (tab != null) {
                tab.setCustomView(view);
            }
        }
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null){
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    private List<String> listAssetFiles(String path) {

        String [] list;
        ArrayList<String> filesList = new ArrayList<>();
        try {
            list = getContext().getAssets().list(path);
            if (list.length > 0) {
                for (String file : list) {
                    if (listAssetFiles(path + "/" + file) == null)
                        return null;
                    else {
                        filesList.add(path + "/" + file);
                    }
                }
            }
        } catch (IOException e) {
            return null;
        }

        return filesList;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentsTitle = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String fragmentTitle){
            fragments.add(fragment);
            fragmentsTitle.add(fragmentTitle);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments != null ? fragments.size() : 0;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitle.get(position);
        }
    }
}
