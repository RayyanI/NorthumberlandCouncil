package uk.ac.ncl.northumberlandcouncil;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View look = inflater.inflate(R.layout.fragment_map, container, false);
        return look;

    }

    public void onActivityCreated( @Nullable Bundle savedInstanceState){
        super.onActivityCreated( savedInstanceState);
        SupportMapFragment sMapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.mapactivity);

        if(sMapFragment!=null){
            sMapFragment.getMapAsync(this);
        }
    }


    @Override
    //DISPLAYS MAP
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap theMap;
        theMap = googleMap;

        LatLng location = new LatLng(54.9783, 1.6178);
        MarkerOptions options = new MarkerOptions();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        options.position(location).title("newcastle");
        theMap.addMarker(options);


    }


}