package ru.balandina.kazan.viewer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.balandina.kazan.R;

public class TransportFragment extends Fragment {

    private static TransportFragment instance;
    private static String metro;
    private static String route;
    private Unbinder unbinder;
    @BindView(R.id.nearbyMetroStationText)
    TextView nearbyMetroStationText;
    @BindView(R.id.routeText)
    TextView routeText;

    public static TransportFragment getInstance(String metroSet, String routeSet){
        metro = metroSet;
        route = routeSet;
        if (instance == null){
            instance = new TransportFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transport, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        nearbyMetroStationText.setText(metro);
        routeText.setText(route);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null){
            unbinder.unbind();
        }
    }
}
