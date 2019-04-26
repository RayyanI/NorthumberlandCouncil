package uk.ac.ncl.northumberlandcouncil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class PopupWindow extends DialogFragment {

    private static TextView castleName;
    private String chosenCastle;
    private static Fragment popupFragment;
    Polyline polyline;

    public PopupWindow() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        popupFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        View popupwindow = inflater.inflate(R.layout.popupwindow, container, false);
        Button getDirectionsBtn = popupwindow.findViewById(R.id.getdirectionsbtn);
        Button getMoreInfoBtn = popupwindow.findViewById(R.id.getmoreinfobtn);
        castleName = popupwindow.findViewById(R.id.cName);
        castleName.setText(chosenCastle);
        ViewCastlesFragment vcf = new ViewCastlesFragment();
        vcf.setChosenCastle(chosenCastle);
        vcf.setChosenImage(chosenCastle.split(" ")[0].toLowerCase() + "test");


        getDirectionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment_container);

                //mapFragment.passCurrentLocation();

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (mapFragment.passCurrentLocation() == null) {
                                Toast.makeText(getContext(), "Please enable your location", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    
                });
                thread.run();

                if (mapFragment.passCurrentLocation() == null) {
                    Toast.makeText(getContext(), "Please enable your location", Toast.LENGTH_SHORT).show();
                    return;
                }

                else {

                    LatLng curloc = new LatLng(mapFragment.passCurrentLocation().latitude, mapFragment.passCurrentLocation().longitude);
                    mapFragment.theMap.addMarker(new MarkerOptions().position(curloc).title("current location"));


                    for (int i = 0; i < 10; i++) {
                        Thread thread1 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    mapFragment.theMap.clear();
                                    mapFragment.getCastleCoordinates();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                        thread1.run();
                        try {
                            if (chosenCastle.equals(mapFragment.listOfCastleNames().get(i))) {


                                LatLng castleloc = new LatLng(mapFragment.listOfCastles().get(i).latitude, mapFragment.listOfCastles().get(i).longitude);

                                mapFragment.theMap.addMarker(new MarkerOptions().position(castleloc).title("castle location"));

                                polyline = mapFragment.theMap.addPolyline(new PolylineOptions()
                                        .add((curloc), (castleloc))
                                        .width(5)
                                        .color(Color.RED)
                                );

                                cameraZoom(polyline);
                                dismiss();
                                break;
                            } else {
                                //continue;
                            }
                        }catch(Exception e ){
                            e.printStackTrace();

                        }
                        thread1.run();
                    }
                }






            }
        });

        getMoreInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment infoFragment = new InformationFragment();
                ((InformationFragment) infoFragment).setPreviousPage("MapFragment");
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, infoFragment).commit();
                dismiss();
            }


        });

        return popupwindow;
    }

    public void setTitle(String cName) {

        chosenCastle = cName;
    }

    private void cameraZoom(Polyline p) {

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment_container);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < p.getPoints().size(); i++) {
            builder.include(p.getPoints().get(i));
        }

        LatLngBounds bounds = builder.build();

        CameraUpdate zoom = CameraUpdateFactory.newLatLngBounds(bounds, 150);
        mapFragment.theMap.animateCamera(zoom);

    }

}