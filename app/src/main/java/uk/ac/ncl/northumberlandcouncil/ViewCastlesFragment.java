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
    private static String chosenCastle;
    private static int castleID;
    private static String chosenImage;

    private Button alnwickButton;
    private Button warkworthButton;
    private Button bamburghButton;
    private Button lindisfarneButton;
    private Button mitfordButton;
    private Button dunstanburghButton;
    private Button chillinghamButton;
    private Button berwickButton;
    private Button prudhoeButton;
    private Button edlinghamButton;
    private Fragment InformationFragment;

    private AlphaAnimation animation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        InformationFragment = new InformationFragment();
        View view = inflater.inflate(R.layout.fragment_viewcastles, container, false);

        uk.ac.ncl.northumberlandcouncil.InformationFragment.setPreviousPage("ViewCastlesFragment");

        alnwickButton = view.findViewById(R.id.alnwick);
        warkworthButton = view.findViewById(R.id.warkworth);
        bamburghButton = view.findViewById(R.id.bamburgh);
        lindisfarneButton = view.findViewById(R.id.lindisfarne);
        mitfordButton = view.findViewById(R.id.mitford);
        dunstanburghButton = view.findViewById(R.id.dunstanburgh);
        chillinghamButton = view.findViewById(R.id.chillingham);
        berwickButton = view.findViewById(R.id.berwick);
        prudhoeButton = view.findViewById(R.id.prudhoe);
        edlinghamButton = view.findViewById(R.id.edlingham);

        //Animation for button onclicks
        animation = new AlphaAnimation(1.0f, 0.8f);

        setupOnClicks();


        return view;

    }

    private void setupOnClicks() {

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
                chosenImage = "mitfordtest";
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



    public static int getCastleID(){
        return castleID;
    }
}
