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

import android.view.animation.AlphaAnimation;
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
    private static TextView cName;
    private static String chosenCastle;
    private static int castleID;
    private static String chosenImage;


    private static List<String> castles = new ArrayList<>(Arrays.asList("Alnwick%20Castle", "Bamburgh%20Castle", "Warkworth%20Castle",
            "Lindisfarne%20Castle","Mitford%20Castle","Dunstanburgh%20castle",
            "Chillingham%20Castle", "Berwick%20castle", "Prudhoe%20castle", "Edlingham%20castle"));

    private static HashMap<String, String> castleIDs = new HashMap<>();
    private static HashMap<String, HashMap<String, String>> castleInfo = new HashMap<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Fragment InformationFragment = new InformationFragment();
        View view = inflater.inflate(R.layout.fragment_viewcastles, container, false);

        ((InformationFragment) InformationFragment).setPreviousPage("ViewCastlesFragment");

        Button alnwickButton = (Button) view.findViewById(R.id.alnwick);
        Button warkworthButton = (Button) view.findViewById(R.id.warkworth);
        Button bamburghButton = (Button) view.findViewById(R.id.bamburgh);
        Button lindisfarneButton = (Button) view.findViewById(R.id.lindisfarne);
        Button mitfordButton = (Button) view.findViewById(R.id.mitford);
        Button dunstanburghButton = (Button) view.findViewById(R.id.dunstanburgh);
        Button chillinghamButton = (Button) view.findViewById(R.id.chillingham);
        Button berwickButton = (Button) view.findViewById(R.id.berwick);
        Button prudhoeButton = (Button) view.findViewById(R.id.prudhoe);
        Button edlinghamButton = (Button) view.findViewById(R.id.edlingham);

        //Animation for button onclicks
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.8f);

        alnwickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                alnwickButton.startAnimation(animation);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, InformationFragment).commit();
                chosenCastle = alnwickButton.getText().toString();
                chosenImage = "alnwicktest";
            }
        });

        warkworthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                warkworthButton.startAnimation(animation);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, InformationFragment).commit();
                chosenCastle = warkworthButton.getText().toString();
                chosenImage = "warkworthtest";
            }
        });

        bamburghButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                bamburghButton.startAnimation(animation);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, InformationFragment).commit();
                chosenCastle = bamburghButton.getText().toString();
                chosenImage = "bamburghtest";
            }
        });

        lindisfarneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                lindisfarneButton.startAnimation(animation);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, InformationFragment).commit();
                chosenCastle = lindisfarneButton.getText().toString();
                chosenImage = "lindisfarnetest";
            }
        });

        mitfordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                mitfordButton.startAnimation(animation);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, InformationFragment).commit();
                chosenCastle = mitfordButton.getText().toString();
                chosenImage = "mitfordtest1";
            }
        });
        dunstanburghButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                dunstanburghButton.startAnimation(animation);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, InformationFragment).commit();
                chosenCastle = "National%20Trust%20-%20Dunstanburgh%20Castle";
                chosenImage = "dunstanburghtest";
            }
        });

        chillinghamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                chillinghamButton.startAnimation(animation);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, InformationFragment).commit();
                chosenCastle = "Chillingham%20castle";
                chosenImage = "chillinghamtest";
            }
        });

        berwickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                berwickButton.startAnimation(animation);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, InformationFragment).commit();
                chosenCastle = berwickButton.getText().toString();
                chosenImage = "berwicktest";
            }
        });

        prudhoeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                prudhoeButton.startAnimation(animation);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, InformationFragment).commit();
                chosenCastle = prudhoeButton.getText().toString();
                chosenImage = "prudhoetest";
            }
        });
        edlinghamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                edlinghamButton.startAnimation(animation);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, InformationFragment).commit();
                chosenCastle = edlinghamButton.getText().toString();
                chosenImage = "edlinghamtest";
            }
        });



        int i = 0;

        return view;

    }
    public static String getChosenCastle(){
        return chosenCastle.replaceAll("\\s", "%20");
    }

    public static String getChosenCastleImg(){
        return chosenImage;
    }

    public static void setCastleID(int id){
        castleID = id;
    }
    public static void setChosenCastle(String castle){
        chosenCastle = castle;
    }

    public static void setChosenImage(String img){
        chosenImage = img;
    }
    public static HashMap<String, String> getInfo(){

        Log.d("chosenCastle", getChosenCastle());
        return castleInfo.get("Alnwick Castle");
    }


    public static int getCastleID(){
        return castleID;
    }
}
