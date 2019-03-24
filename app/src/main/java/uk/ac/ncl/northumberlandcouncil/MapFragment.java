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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap theMap;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View look= inflater.inflate(R.layout.fragment_map, container, false);

    return look;
    }
    /* */
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.mapactivity);
        mapFragment.getMapAsync(this);
    }

    /* */
    @Override
    public void onMapReady(GoogleMap googleMap) {
    theMap = googleMap;
        LatLng location = new LatLng(54.9783,1.6178);
        MarkerOptions options = new MarkerOptions();
        options.position(location).title("newcastle");
        theMap.addMarker(options);
        theMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
}