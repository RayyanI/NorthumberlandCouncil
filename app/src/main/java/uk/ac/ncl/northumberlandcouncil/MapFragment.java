package uk.ac.ncl.northumberlandcouncil;

import android.hardware.input.InputManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import android.widget.Button;

import android.view.inputmethod.InputMethodManager;
import android.content.Context;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap theMap;
    MapView mapview;

    public MapFragment() {
        // Required empty public constructor
    }
    public void onMapSearch(View view) {
        EditText locationSearch = (EditText) getView().findViewById(R.id.address);
        String location = locationSearch.getText().toString();
        List<Address>listOfAddress = null;



        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(getActivity());
            try {
                listOfAddress = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = listOfAddress.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            theMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            theMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View look = inflater.inflate(R.layout.fragment_map, container, false);

        Button searchButton = (Button) look.findViewById(R.id.searchbutton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //put functionality here
                closeKeyboard(getActivity());
            }
        });




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
        theMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,14f));


    }

    public static void closeKeyboard(android.app.Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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