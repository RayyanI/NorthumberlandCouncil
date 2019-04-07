package uk.ac.ncl.northumberlandcouncil;


/* Begin library imports */
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.*;
import java.net.*;

import android.widget.ImageView;
import android.widget.TextView;
/* End library imports */


/**
 * Handles code for Home Fragment
 * @author Sean Fuller
 * Created on 03/03/2019
 * Updated on 10/03/2019
 * Updated on 14/03/2019
 */

/* This still has a lot of work to do, it is nowhere near done */
public class InformationFragment extends Fragment {
    /**
     * Inflate fragment upon selection
     * @param inflater for instancing xml fragments
     * @param container container to display content stream
     * @param savedInstanceState current activity datas store
     * @return display xml view on screen
     */

    private String castleLocation; /* API's castle location */
    private String castleName; /* Targeted castle name */
    private String ageRange; /* Age range for the castle */
    private int childPrice; /* Price for a child to enter */
    private int adultPrice; /* Price for an adult to enter */

    TextView castleNameTV; /* Displayed castle name to change */
    TextView castleLocationTV;
    TextView castleRatingTV;
    ImageView castlePhotoImg;
    TextView castleWebsiteTV;
    ViewCastlesFragment vcf;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);

        TextView castleNameTV = view.findViewById(R.id.castle_name);
        TextView castleLocationTV = view.findViewById(R.id.castle_address);
        TextView castleRatingTV = view.findViewById(R.id.rating);
        ImageView castlePhotoImg = view.findViewById(R.id.castle_img);
        TextView castleWebsiteTV = view.findViewById(R.id.castle_name);

        vcf = new ViewCastlesFragment();
            castleNameTV.setText(vcf.getChosenCastle().replaceAll("%20", " "));

        try{
            getCastleDetails(vcf.getChosenCastle());
        }catch(Exception e){

        }

        return view;



    }

    /* Eventually this method will be called when a castle is clicked, it will run the places API */
    public void displayCastle(String name, String location) throws java.io.IOException{
        getCastleDetails(name);

    }
    private String key = "AIzaSyBt2RpqiKdletwhDxcvPZ-dqTY04xaV2xw"; /* Places API key */
    private String urlString = "https://maps.googleapis.com/maps/api/place/findplacefromtext/" +
            "json?input="; /* Places API URL */
    private URL url;


    private String address;
    private String name;
    private boolean open;
    private String photoReference;
    private String rating;

    public static void main(String args[]) {
        /* List of all the castles */
        String[] castles = {"Alnwick%20Castle", "Bamburgh%20Castle", "Warkworth%20Castle",
                "Lindisfarne%20Castle","Tynemouth%20Castle%20&%20Priory","Dunstanburgh%20castle",
                "Chillingham%20Castle", "Berwick%20castle", "Prudhoe%20Castle", "Edlingham%20Castle"};


    }

    /* Method gathers castle details */
    private void getCastleDetails(String castle) throws IOException {

    }
    // When called it updates the page information

    private void setCastleDetails(String castleN, String castleL, String castleR, String castleImgRef){
        castleNameTV.setText(castleN);
        castleLocationTV.setText(castleL);
        castleRatingTV.setText(castleR);
//        castlePhotoImg.setImage(castleImgRef); Will hold the image


    }



}
