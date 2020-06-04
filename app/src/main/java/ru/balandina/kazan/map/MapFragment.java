package ru.balandina.kazan.map;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.balandina.kazan.R;
import ru.balandina.kazan.models.ObjectEntity;

public class MapFragment extends Fragment implements OnMapReadyCallback, Filterable {

    public interface OnMarkerClickListener {
        void onMarkerClick(ObjectEntity objectEntity);
    }

    @BindView(R.id.search)
    SearchView searchView;
    private GoogleMap googleMap;
    private static List<ObjectEntity> objectEntityList;
    private static List<ObjectEntity> objectEntityListFull;
    private OnMarkerClickListener onMarkerClickListener;
    private static MapFragment instance;
    private Unbinder unbinder;
    private static LatLng position = null;

    public static MapFragment newInstance(List<ObjectEntity> objectEntities){
        objectEntityList = objectEntities;
        if (objectEntityList != null){
            objectEntityListFull = new ArrayList<>(objectEntityList);
        }
        if (instance == null){
            instance = new MapFragment();
        }
        return instance;
    }

    public static MapFragment newInstance(List<ObjectEntity> objectEntities, LatLng newPosition){
        objectEntityList = objectEntities;
        position = newPosition;
        if (objectEntityList != null){
            objectEntityListFull = new ArrayList<>(objectEntityList);
        }
        if (instance == null){
            instance = new MapFragment();
        }
        return instance;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnMarkerClickListener){
            onMarkerClickListener = (OnMarkerClickListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (onMarkerClickListener != null){
            onMarkerClickListener = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this, view);

        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if (fragment != null) {
            fragment.getMapAsync(this);
        }

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
        googleMap = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setOnMarkerClickListener(marker -> {
            if (onMarkerClickListener != null) {
                int objectPosition = Integer.parseInt(marker.getTag().toString()) - 1;
                onMarkerClickListener.onMarkerClick(objectEntityListFull.get(objectPosition));
            }
            return false;
        });

        List<MarkerData> markerData  = objectEntityToMarkerData(objectEntityList);
        addMarkers(markerData);

        int width = getResources().getDisplayMetrics().widthPixels;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (position != null){
            builder.include(position);
        } else {
            for (MarkerData marker : markerData) {
                builder.include(marker.getMarkerOptions().getPosition());
            }
        }
        LatLngBounds latLngBounds = builder.build();
        CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, width, width, 100);
        googleMap.animateCamera(track);
    }

    private void addMarkers(List<MarkerData> markerData) {
        if (googleMap != null && markerData != null) {
            googleMap.clear();
            markerData.stream().forEach(markerData1 -> {
                Marker marker = googleMap.addMarker(markerData1.getMarkerOptions());
                marker.setTag(markerData1.getId());
            });
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ObjectEntity> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0){
                    filteredList = objectEntityListFull;
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (ObjectEntity object: objectEntityListFull){
                        if (object.getTitle().toLowerCase().contains(filterPattern)){
                            filteredList.add(object);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (objectEntityList != null){
                    objectEntityList.clear();
                    objectEntityList.addAll((List) results.values);
                    addMarkers(objectEntityToMarkerData(objectEntityList));
                }
            }
        };
    }

    private List<MarkerData> objectEntityToMarkerData(List<ObjectEntity> objectEntities) {
        ArrayList<MarkerData> markerDataArrayList = new ArrayList<>();
        if (!objectEntities.isEmpty()) {
            for (ObjectEntity entity : objectEntities) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .draggable(false)
                        .position(new LatLng(entity.getLocation().get(0), entity.getLocation().get(1)));

                MarkerData markerData = new MarkerData((int) entity.getId(), markerOptions);
                markerDataArrayList.add(markerData);
            }
        }
        return markerDataArrayList;
    }
}
