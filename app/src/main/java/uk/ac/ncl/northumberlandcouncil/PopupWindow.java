package uk.ac.ncl.northumberlandcouncil;

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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PopupWindow extends DialogFragment {

    private static TextView castleName;
    private String chosenCastle;
    private static Fragment popupFragment;

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
        vcf.setChosenImage(chosenCastle.split(" ")[0].toLowerCase()+"test");
        getDirectionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        getMoreInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new InformationFragment()).commit();
                dismiss();
            }


        });


        return popupwindow;
    }

    public void setTitle(String cName){
        chosenCastle = cName;
    }

}