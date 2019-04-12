package uk.ac.ncl.northumberlandcouncil;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.input.InputManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import com.google.android.gms.location.LocationServices;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.google.android.gms.maps.model.Marker;

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
import java.util.Map;
import java.util.Objects;
import android.widget.Button;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import static android.content.Context.*;
import static android.support.v4.content.ContextCompat.getSystemService;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    /* Declarations */
    GoogleMap theMap;
    MapView mapview;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationListener locationListener;

    /* End Declarations */
    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View look = inflater.inflate(R.layout.fragment_map, container, false);
        Button searchButton = (Button) look.findViewById(R.id.searchbutton);
        EditText editText = (EditText) look.findViewById(R.id.address);
        editText.setImeActionLabel("Enter", KeyEvent.KEYCODE_ENTER);

        // Handle searching using the keyboard //
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /* If search is selected on the keyboard  or enter is pressed on the keyboard*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH || event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    searchButton.performClick();
                    return true;
                }
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*  Try to search for a castle with this name */
                EditText locationSearch = (EditText) Objects.requireNonNull(getView()).findViewById(R.id.address);
                String location = locationSearch.getText().toString();
                List<Address> listOfAddress = null;
                System.out.println(location);

                if (location != null) {
                    Geocoder geocoder = new Geocoder(getActivity());
                    try {
                        listOfAddress = geocoder.getFromLocationName(location, 1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (listOfAddress != null) {

                        if (listOfAddress.size() > 0) {
                            Address address = listOfAddress.get(0);
                            String loc = address.getLocality();
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());


                            Thread thread = new Thread(new Runnable() {

                                public void run() {

                                    try {
                                        theMap.clear();
                                        theMap.addMarker(new MarkerOptions().title(loc).position(latLng));
                                        theMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            thread.run();


                        }
                    }

                }
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
    public void onMapReady(GoogleMap googleMap) {

        this.theMap = googleMap;
        theMap = googleMap;
        LatLng myPosition;

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            theMap.setMyLocationEnabled(true);
        }

        /*Method below calls the handlesNewLocation which sets the default zoom to the user's current location*/

        /*Location location = LocationServices.getFusedLocationProviderClient(this.requireContext()).getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.getFusedLocationProviderClient(getContext()).requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
        }
        else {
            handleNewLocation(location);
        }*/

    }


    private void handleNewLocation(Location location) {

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        theMap.addMarker(options);
        theMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));

    }



    public static void closeKeyboard(android.app.Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
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