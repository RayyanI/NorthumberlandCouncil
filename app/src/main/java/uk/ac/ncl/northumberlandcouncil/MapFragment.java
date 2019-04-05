package uk.ac.ncl.northumberlandcouncil;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap theMap;
    MapView mapview;

    public MapFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View look = inflater.inflate(R.layout.fragment_map, container, false);
        mapview = (MapView) look.findViewById(R.id.mapactivity);
        mapview.onCreate(savedInstanceState);
        mapview.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapview.getMapAsync(this);
        return look;


    }



           //DISPLAYS MAP
            public void onMapReady (GoogleMap googleMap){

                theMap = googleMap;

                LatLng location = new LatLng(54.97385, -1.6252);
                MarkerOptions options = new MarkerOptions();

                theMap.setBuildingsEnabled(true);
                
              theMap.addMarker(new MarkerOptions().position(location).title("Newcastle"));
theMap.moveCamera(CameraUpdateFactory.newLatLng(location));


            }


    @Override
    public void onResume() {
        super.onResume();
        mapview.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapview.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapview.onLowMemory();
    }


}