package uk.ac.ncl.northumberlandcouncil;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;

/**
 * Handles code for Authorisation Fragment
 *
 * @author Alvin Ho and Jasper Griffin
 * Created on 01/04/2019
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    /* Declarations */
    protected GoogleMap theMap;
    private MapView mapview;
    protected String destinationAddress;
    private String originAddress;
    protected String distance;
    protected String duration;
    private String castleName;
    private static BufferedReader in;
    private static StringBuffer response;
    protected Location currentLocation;
    private URL url;
    protected boolean isMarkerPressed = false;
    protected LocationManager locationManager;


    private ViewGroup infoWindow;

    /* End Declarations */

    public MapFragment() {
        // Required empty public constructor
    }

    /*
     * Inflate fragment upon selection
     *
     * @param inflater           for instancing xml fragments
     * @param container          container to display content stream
     * @param savedInstanceState current activity datas store
     * @return display xml view on screen
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Select the button and text elements
        View look = inflater.inflate(R.layout.fragment_map, container, false);
        Button searchButton = look.findViewById(R.id.searchbutton);
        EditText editText = look.findViewById(R.id.address);
        editText.setImeActionLabel("Enter", KeyEvent.KEYCODE_ENTER);
        ImageButton infoButton = look.findViewById(R.id.infobutton);

        // TODO: SET THE CURRENT LOCATION TO USERS CURRENT LOCATION HERE OR THIS IF NO LOCATION ACCESS //
        currentLocation = new Location("Northumberland Council");
        currentLocation.setLatitude(55.224470);
        currentLocation.setLongitude(-2.014950);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }

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

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());

                // sets the title
                alertDialogBuilder.setTitle("Castles");

                // set the message of the dialog
                alertDialogBuilder
                        .setMessage("Castles to Enter are as follows:\n" + "Alnwick Castle\n" + "Bamburgh Castle\n" + "Warkworth Castle\n" + "Lindisfarne Castle\n" + "Mitford Castle\n " + "Dunstanburgh Castle\n" + "Chillingham Castle\n" + "Berwick Castle\n" + "Prudhoe Castle\n" + "Edlinghan Castle\n"

                        )
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getContext(), "Info closed", Toast.LENGTH_SHORT).show();

                            }
                        });


                // create the alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.getWindow().setLayout(600, 350);
                // show the alert dialog
                alertDialog.show();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*  Try to search for a castle with this name */
                EditText locationSearch = Objects.requireNonNull(getView()).findViewById(R.id.address);
                String location = locationSearch.getText().toString();
                List<Address> listOfAddress = null;
                System.out.println(location);

                if (location != null) {
                    // Geocoder used to transform a description from location to data and puts the data stored into the array listOfAddress
                    Geocoder geocoder = new Geocoder(getActivity());
                    try {
                        listOfAddress = geocoder.getFromLocationName(location, 1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // statement if the data collected from the geocoder isn't empty
                    if (listOfAddress != null) {
                        // if statement to make sure that if the size of the array is greater than 0 then get the first element in the array and get the address and latitude
                        if (listOfAddress.size() > 0) {
                            Address address = listOfAddress.get(0);


                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());


                            Thread thread = new Thread(new Runnable() {

                                public void run() {

                                    try {
                                        // Statement below is setting a new location and clears map first then create a new location sets the coordinates and removes any markers that are the same
                                        theMap.clear();

                                        getCastleCoordinates();

                                        Location castleSearch = new Location("point B");
                                        castleSearch.setLatitude(latLng.latitude);
                                        castleSearch.setLongitude(latLng.longitude);
                                        removeDuplicateMarkers(castleSearch, listOfCastles());

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


        mapview = look.findViewById(R.id.mapactivity);
        mapview.onCreate(savedInstanceState);
        mapview.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapview.getMapAsync(this);
        data();
        return look;

    }


    //DISPLAYS MAP
    public void onMapReady(GoogleMap googleMap) {

        this.theMap = googleMap;
        theMap = googleMap;

        getCastleCoordinates();

        if (theMap != null) {

            theMap.setInfoWindowAdapter(new InfoWindowAdapter() {


                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    View row = getLayoutInflater().inflate(R.layout.info_window, null);
                    TextView t1_name = row.findViewById(R.id.markerTitle);
                    t1_name.setText(marker.getTitle());
                    castleName = marker.getTitle();
                    Log.i("name", castleName);
                    return row;
                }

            });
        }

        theMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {

                isMarkerPressed = true;

                PopupWindow popUp = new PopupWindow();

                popUp.show(getActivity().getSupportFragmentManager(), "");
                Log.i("Title", castleName);
                popUp.setTitle(castleName);


            }
        });


        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            theMap.setMyLocationEnabled(true);
        }

        //getCastleCoordinates();
        handleNewLocation();
        getBorder();

    }

    /*a method to handle location and current location*/
    public LatLng handleNewLocation() {
        // if statement to handle if the location manager isnt enabled on the phone
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            String error = "Please enable your location in your phone settings";
            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            LatLng defaultPosition = new LatLng(55.224470, -2.014950);
            theMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPosition, 8));
            return null;
        }
        // else statement to handle the access and permission of the application
        else {

            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);

            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //return;
            }
            // gets the users last known location
            currentLocation = locationManager.getLastKnownLocation(provider);
            // if statement to handle if the location currently isn't empty then get the coordinates and move the camera towards that position
            if (currentLocation != null) {

                double latitude = currentLocation.getLatitude();
                double longitude = currentLocation.getLongitude();
                LatLng currentPosition = new LatLng(latitude, longitude);
                theMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 13f));
                return currentPosition;

            }
            // a else statement to place a default position if there is no current location
            else {
                LatLng defaultPosition = new LatLng(55.224470, -2.014950);
                theMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPosition, 8));
                //locationManager = null;
                return null;

            }

            /*else {

                LocationListener locationListener = new LocationListener() {

                    @Override
                    public void onLocationChanged(Location location) {

                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LatLng newCurrentPosition = new LatLng(latitude, longitude);
                        theMap.addMarker(new MarkerOptions().position(newCurrentPosition).title("I'm here!"));
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                return null;
            }*/


        }

    }

    public LatLng passCurrentLocation() {
        return handleNewLocation();
    }

    /* a method that sets a marker for each castle in its location                */
    public void getCastleCoordinates() {

        // Castle coordinates
        LatLng Alnwick = new LatLng(55.41575, -1.70607);
        theMap.addMarker(new MarkerOptions().position(Alnwick).title("Alnwick castle").snippet("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_castle_marker)));
        LatLng Bamburgh = new LatLng(55.608, -1.709);
        theMap.addMarker(new MarkerOptions().position(Bamburgh).title("Bamburgh castle").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_castle_marker)));
        LatLng Warkworth = new LatLng(55.3447, -1.6105);
        theMap.addMarker(new MarkerOptions().position(Warkworth).title("Warkworth castle").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_castle_marker)));
        LatLng Lindisfarne = new LatLng(55.669, -1.785);
        theMap.addMarker(new MarkerOptions().position(Lindisfarne).title("Lindisfarne castle").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_castle_marker)));
        LatLng Mitford = new LatLng(55.164, -1.734);
        theMap.addMarker(new MarkerOptions().position(Mitford).title("Mitford castle").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_castle_marker)));
        LatLng Dunstanburgh = new LatLng(55.4894, -1.5950);
        theMap.addMarker(new MarkerOptions().position(Dunstanburgh).title("Dunstanburgh castle").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_castle_marker)));
        LatLng Chillingham = new LatLng(55.5259, -1.9038);
        theMap.addMarker(new MarkerOptions().position(Chillingham).title("Chillingham castle").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_castle_marker)));
        LatLng Berwick = new LatLng(55.7736, -2.0125);
        theMap.addMarker(new MarkerOptions().position(Berwick).title("Berwick castle").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_castle_marker)));
        LatLng Prudhoe = new LatLng(54.9649, -1.8582);
        theMap.addMarker(new MarkerOptions().position(Prudhoe).title("Prudhoe castle").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_castle_marker)));
        LatLng Edlingham = new LatLng(55.3767, -1.8185);
        theMap.addMarker(new MarkerOptions().position(Edlingham).title("Edlingham castle").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_castle_marker)));
    }

    /* adds each castle into a array list of castles       */
    public List<LatLng> listOfCastles() {

        List<LatLng> points = new ArrayList<LatLng>();
        points.add(new LatLng(55.41575, -1.70607));
        points.add(new LatLng(55.608, -1.709));
        points.add(new LatLng(55.3447, -1.6105));
        points.add(new LatLng(55.669, -1.785));
        points.add(new LatLng(55.164, -1.734));
        points.add(new LatLng(55.4894, -1.5950));
        points.add(new LatLng(55.5259, -1.9038));
        points.add(new LatLng(55.7736, -2.0125));
        points.add(new LatLng(54.9649, -1.8582));
        points.add(new LatLng(55.3767, -1.8185));
        return points;
    }

    /*   adds each castle name into a array list            */
    public List<String> listOfCastleNames() {

        List<String> names = new ArrayList<>();
        names.add("Alnwick castle");
        names.add("Bamburgh castle");
        names.add("Warkworth castle");
        names.add("Lindisfarne castle");
        names.add("Mitford castle");
        names.add("Dunstanburgh castle");
        names.add("Chillingham castle");
        names.add("Berwick castle");
        names.add("Prudhoe castle");
        names.add("Edlingham castle");
        return names;
    }

    public void getBorder() {

        try {
            GeoJsonLayer layer = new GeoJsonLayer(theMap, R.raw.geojson, getActivity().getApplicationContext());

            GeoJsonPolygonStyle map = layer.getDefaultPolygonStyle();
            map.setFillColor(Color.argb(60, 255, 195, 195));
            map.setStrokeWidth(5);
            map.setStrokeColor(Color.rgb(255, 77, 77));

            layer.addLayerToMap();

        } catch (IOException ex) {
            Log.e("IOException", ex.getLocalizedMessage());

        } catch (JSONException ex) {
            Log.e("JSONException", ex.getLocalizedMessage());
        }
    }

    public static void closeKeyboard(android.app.Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void data() {

        // Array of castle's locations
        String[] destinations = {"NE66%201NG", "NE69%207DF", "NE65%200UJ", "TD15%202SH", "NE30%204BZ",
                "NE66%203TT", "NE66%205NJ", "TD15%201NF", "NE42%206NA", "NE66%202BW"};


        // For loop to calculate each castle's data. currently the origin is just Newcastle
        for (String destination : destinations) {
            calcDistAndDur(originAddress, destination); // <-- Change first argument to correct origin
            System.out.println("-------------------");
        }
    }
    // Method to calculate the specified castle data

    private void calcDistAndDur(String origin, String dest) {
        origin = currentLocation.toString();
        String urlString = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origin + "&destinations=" + dest + "&key=AIzaSyA-SYN3vPXJ0Z7Xgw7QhkhTl7fo9xL48yw";
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    in = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    response = new StringBuffer();

                    int currentLine = 0;
                    String readLine = null;
                    try {
                        while ((readLine = in.readLine()) != null) {
                            if (currentLine == 1) {
                                destinationAddress = readLine;
                            } else if (currentLine == 2) {
                                originAddress = readLine;
                            } else if (currentLine == 8) {
                                distance = readLine;
                            } else if (currentLine == 12) {
                                duration = readLine;
                            }
                            currentLine++;
                            response.append(readLine);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        JSONObject js = new JSONObject(response.toString());
                        System.out.println(js.getString("destination_addresses"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            thread.start();
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*A method to handle duplicate markers being removed*/
    public void removeDuplicateMarkers(Location castleSearch, List<LatLng> points) {

        for (int i = 0; i < 10; i++) {

            Location castleCoord = new Location("point A");
            castleCoord.setLatitude(points.get(i).latitude);
            castleCoord.setLongitude(points.get(i).longitude);

            double distance = castleCoord.distanceTo(castleSearch);

            if (distance < 1000) {

                theMap.addMarker(new MarkerOptions().position(points.get(i)).title(points.get(i).toString()).snippet("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_castle_marker)));
                theMap.animateCamera(CameraUpdateFactory.newLatLngZoom(points.get(i), 13f));
                theMap.clear();
                getCastleCoordinates();
                //getBorder();
            }
        }
    }

    @Override
    /*called when activity was hidden comes back to view */
    public void onResume() {
        super.onResume();
        mapview.onResume();
    }

    @Override
    /*called when activity is hidden from view */
    public void onPause() {
        super.onPause();
        mapview.onPause();
    }

    @Override
    /*called when activity finishes lifestyle*/
    public void onDestroy() {
        super.onDestroy();
        closeKeyboard(getActivity());
        mapview.onDestroy();
    }

    @Override
    /*when system is low on memory*/
    public void onLowMemory() {
        super.onLowMemory();
        mapview.onLowMemory();
    }


}