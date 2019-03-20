package uk.ac.ncl.northumberlandcouncil;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;
import org.json.JSONException;


/**
 * @author Alvin Ho
 * Created on 12/02/2019
 * Last modified 05/03/2019 (Alvin Ho)
 */


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String MAP_ACTIVITY = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_REQUEST = 1234;
    private static final float ZOOM = 14.0f;

    //widgets
    private EditText mSearchText;
    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;

  //  protected ImageView mGps = findViewById(R.id.ic_gps);
    @Override
    public void onMapReady(GoogleMap googleMap) {
        makeText(this, "Map is Ready", LENGTH_SHORT).show();
        Log.d(MAP_ACTIVITY, "onMapReady: map is ready");
        mMap = googleMap;
        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            initialising();
        }
    }



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mSearchText = findViewById(R.id.input_search);

        getLocationPermission();
        getCastleCoordinates();
        getJsonLayer();
    }
//ffff
    private void initialising(){
        Log.d(MAP_ACTIVITY, "init: initializing");
        mSearchText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                //execute our method for searching
                geoLocate(); }
            return false;
        });
        hideSoftKeyboard();

    }

    private void geoLocate(){

        Log.d(MAP_ACTIVITY, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(MAP_ACTIVITY, "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);
            LatLng loc = new LatLng(address.getLatitude(), address.getLongitude());
            Log.d(MAP_ACTIVITY, "geoLocate: found a location: " + address.toString());
            moveCamera(loc, address.getAddressLine(0));

            makeText(this, address.toString(), LENGTH_SHORT).show();

        }
    }
    /* gets the device current location*/
    private void getDeviceLocation(){
        Log.d(MAP_ACTIVITY, "getDeviceLocation: getting the devices current location");

        FusedLocationProviderClient fusedLocation;
        fusedLocation = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task<Location> location = fusedLocation.getLastLocation();
                final Task<Location> locationTask = location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    Task task;

                    @Override
                    public void onComplete(Task task) {
                        this.task = task;
                        if (task.isSuccessful()) {
                            final int d = Log.d(MAP_ACTIVITY, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), "current location");
                        } else {
                            Log.d(MAP_ACTIVITY, "onComplete: current location is null");
                            makeText(MapsActivity.this, "unable to get current location", LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(MAP_ACTIVITY, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }
    /*Moves camera  */
    private void moveCamera(LatLng latLng, String title){
        Log.d(MAP_ACTIVITY, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MapsActivity.ZOOM));
        if(title.equals("current location")) {
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(options);
        }
        hideSoftKeyboard();

    }
    /* the initialisation of a map */
    private void initialisingMap(){
        Log.d(MAP_ACTIVITY, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(MapsActivity.this);
        }
    }
    /* obtaining the permision for a location*/
    private void getLocationPermission(){
        Log.d(MAP_ACTIVITY, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initialisingMap();
            }else{
                ActivityCompat.requestPermissions(this, permissions, LOCATION_REQUEST);
            }
        }else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_REQUEST);
        }
    }

    /* grants whether the user has access or no access to the location */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(MAP_ACTIVITY, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_REQUEST:{
                if(grantResults.length > 0){
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(MAP_ACTIVITY, "onRequestPermissionsResult: permission failed");

                        }
                    }
                    Log.d(MAP_ACTIVITY, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize the  map
                    initialisingMap();
                }
            }
        }
    }
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /* Displays all 10 castles on the map */
    private void getCastleCoordinates() {

        LatLng Alnwick = new LatLng(55.41575, -1.70607);
        mMap.addMarker(new MarkerOptions().position(Alnwick).title("Alnwick Castle"));
        LatLng Bamburgh = new LatLng(55.608, -1.709);
        mMap.addMarker(new MarkerOptions().position(Bamburgh).title("Bamburgh Castle"));
        LatLng Warkworth = new LatLng(55.3447, -1.6105);
        mMap.addMarker(new MarkerOptions().position(Warkworth).title("Warkworth Castle"));
        LatLng Lindisfarne = new LatLng(55.669, -1.785);
        mMap.addMarker(new MarkerOptions().position(Lindisfarne).title("Lindisfarne Castle"));
        LatLng Tynemouth_Castle_Priory = new LatLng(55.0177, -1.4179);
        mMap.addMarker(new MarkerOptions().position(Tynemouth_Castle_Priory).title("Tynemouth Castle & Priory"));
        LatLng Dunstanburgh = new LatLng(55.4894, -1.5950);
        mMap.addMarker(new MarkerOptions().position(Dunstanburgh).title("Dunstanburgh Castle"));
        LatLng Chillingham = new LatLng(55.5259, -1.9038);
        mMap.addMarker(new MarkerOptions().position(Chillingham).title("Chillingham Castle"));
        LatLng Berwick = new LatLng(55.7736, -2.0125);
        mMap.addMarker(new MarkerOptions().position(Berwick).title("Berwick Castle"));
        LatLng Prudhoe = new LatLng(54.9649, -1.8582);
        mMap.addMarker(new MarkerOptions().position(Prudhoe).title("Prudhoe Castle"));
        LatLng Edlingham = new LatLng(55.3767, -1.8185);
        mMap.addMarker(new MarkerOptions().position(Edlingham).title("Edlingham Castle"));

    }

    /* imports json layer which acts as the boundary for the google maps */
    public void getJsonLayer() {

        try {
            GeoJsonLayer layer = new GeoJsonLayer(mMap, R.raw.geojson, getApplicationContext());

            GeoJsonPolygonStyle map = layer.getDefaultPolygonStyle();
            map.setFillColor(Color.argb(70,255,195,195));
            map.setStrokeWidth(5);
            map.setStrokeColor(Color.rgb(255,77,77));

            layer.addLayerToMap();

        }catch (IOException ex) {
            Log.e("IOException", ex.getLocalizedMessage());
        } catch (JSONException ex) {
            Log.e("JSONException", ex.getLocalizedMessage());
        }
    }
}

