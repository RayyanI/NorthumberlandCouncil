package uk.ac.ncl.northumberlandcouncil;


/* Begin library imports */
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
/* End library imports */


/**
 * Handles code for Home Fragment
 * @author Sean Fuller
 * Created on 03/03/2019
 * Updated on 10/03/2019
 * Updated on 14/03/2019
 * Updated on 06/04/2019
 * Updated on 07/04/2019
 * Updated on 11/04/2019
 */

public class ViewCastlesFragment extends Fragment {
    private Button alnwickButton;
    private static TextView cName;
    private static String chosenCastle;
    private static int castleID;

    private static List<String> castles = new ArrayList<>(Arrays.asList("Alnwick%20Castle", "Bamburgh%20Castle", "Warkworth%20Castle",
            "Lindisfarne%20Castle","Tynemouth%20Castle%20&%20Priory","Dunstanburgh%20castle",
            "Chillingham%20Castle", "Berwick%20castle", "Prudhoe%20Castle", "Edlingham%20Castle"));

    private static HashMap<String, String> castleIDs = new HashMap<>();
    private static HashMap<String, HashMap<String, String>> castleInfo = new HashMap<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_viewcastles, container, false);


        alnwickButton = view.findViewById(R.id.alnwick);
        Button warkworthButton = (Button) view.findViewById(R.id.warkworth);
        Button bamburghButton = (Button) view.findViewById(R.id.bamburgh);
        Button lindisfarneButton = (Button) view.findViewById(R.id.lindisfarne);
        Button tynemouthButton = (Button) view.findViewById(R.id.tynemouth);
        Button dunstanburghButton = (Button) view.findViewById(R.id.dunstanburgh);
        Button chillinghamButton = (Button) view.findViewById(R.id.chillingham);

        alnwickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new InformationFragment()).commit();
                chosenCastle = alnwickButton.getText().toString();
                castleID = 0;
            }
        });

        warkworthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new InformationFragment()).commit();
                chosenCastle = warkworthButton.getText().toString();
                castleID = 0;
            }
        });

        bamburghButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new InformationFragment()).commit();
                chosenCastle = bamburghButton.getText().toString();
                castleID = 0;
            }
        });

        lindisfarneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new InformationFragment()).commit();
                chosenCastle = lindisfarneButton.getText().toString();
            }
        });

        tynemouthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new InformationFragment()).commit();
                chosenCastle = tynemouthButton.getText().toString();
            }
        });
        dunstanburghButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new InformationFragment()).commit();
                chosenCastle = dunstanburghButton.getText().toString();
            }
        });

        chillinghamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new InformationFragment()).commit();
                chosenCastle = chillinghamButton.getText().toString();
            }
        });

        int i = 0;

        return view;

    }
    public static String getChosenCastle(){
        return chosenCastle.replaceAll("\\s", "%20");
    }

    public static HashMap<String, String> getInfo(){

        Log.d("chosenCastle", getChosenCastle());
        return castleInfo.get("Alnwick Castle");
    }


    public static int getCastleID(){
        return castleID;
    }
}
