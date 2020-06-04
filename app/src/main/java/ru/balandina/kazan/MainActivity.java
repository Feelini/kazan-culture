package ru.balandina.kazan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.balandina.kazan.kulture.KultureFragment;
import ru.balandina.kazan.map.MapFragment;
import ru.balandina.kazan.models.ObjectEntity;
import ru.balandina.kazan.top.TopFragment;
import ru.balandina.kazan.viewer.ViewerFragment;

public class MainActivity extends AppCompatActivity implements MapFragment.OnMarkerClickListener, TopFragment.OnViewOnMapClickListener {

    @BindView(R.id.pre_background)
    ImageView preBackground;
    @BindView(R.id.pre_text)
    TextView preText;
    @BindView(R.id.bottomMenu)
    BottomNavigationView bottomNavigationView;
    private Unbinder unbinder;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private List<ObjectEntity> objects = new ArrayList<>();
    private List<ObjectEntity> top = new ArrayList<>();
    private String kulture;
    private int currentMenu = R.id.menu_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);

        database = FirebaseDatabase.getInstance();
        getObjects(database);
        getKulture(database);
        getTop(database);

        bottomNavigationView.setOnNavigationItemSelectedListener(listener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener = item -> {
        if (item.getItemId() != currentMenu) {
            currentMenu = item.getItemId();
            switch (item.getItemId()) {
                case R.id.menu_map:
                    showMapFragment(objects);
                    break;
                case R.id.menu_kulture:
                    showKultureFragment(kulture);
                    break;
                case R.id.menu_top:
                    showTopFragment();
                    break;
                case R.id.menu_exit:
                    finish();
                    break;
            }
        }
        return true;
    };


    private void getObjects(FirebaseDatabase database) {
        databaseReference = database.getReference().child("objects");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                        objects.add(dataSnapshotChild.getValue(ObjectEntity.class));
                    }
                    if (objects != null) {
                        hideHello();
                        showMapFragment(objects);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getTop(FirebaseDatabase database) {
        databaseReference = database.getReference().child("places");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                        top.add(dataSnapshotChild.getValue(ObjectEntity.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getKulture(FirebaseDatabase database) {
        databaseReference = database.getReference().child("kulture");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.exists()) {
                    kulture = (String) dataSnapshot.getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private void hideHello() {
        preBackground.setVisibility(View.GONE);
        preText.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    private void showMapFragment(List<ObjectEntity> objects) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, MapFragment.newInstance(objects), MapFragment.class.getName())
                .commit();
    }

    private void showMapFragment(List<ObjectEntity> objects, LatLng position) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, MapFragment.newInstance(objects, position), MapFragment.class.getName())
                .commit();
    }

    private void showViewerFragment(ObjectEntity object) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, ViewerFragment.newInstance(object), ViewerFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }

    private void showKultureFragment(String kulture) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, KultureFragment.newInstance(kulture), KultureFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }

    private void showTopFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, TopFragment.newInstance(top), TopFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onMarkerClick(ObjectEntity objectEntity) {
        showViewerFragment(objectEntity);
    }

    @Override
    public void onViewOnMapClick(LatLng position) {
        bottomNavigationView.getMenu().findItem(R.id.menu_map).setChecked(true);
        currentMenu = R.id.menu_map;
        showMapFragment(objects, position);
    }
}
