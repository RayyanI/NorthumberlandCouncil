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
 * Updates castle information page
 * @author Sean Fuller
 * Created on 03/03/2019
 * Updated on 10/03/2019
 * Updated on 14/03/2019
 * Updated on 09/04/2019
 * Updated on 11/04/2019
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

    private String key = "AIzaSyA-SYN3vPXJ0Z7Xgw7QhkhTl7fo9xL48yw"; /* Places API key */
    private String urlString = "https://maps.googleapis.com/maps/api/place/findplacefromtext/" +
            "json?input="; /* Places API URL */
    private URL url;


    private String address;
    private String name;
    private boolean open;
    private String photoReference;
    private String rating;

    TextView castleNameTV; /* Displayed castle name to change */
    TextView castleLocationTV;
    TextView castleRatingTV;
    ImageView castlePhotoImg;
    TextView castleWebsiteTV;
    ViewCastlesFragment vcf;

    HttpURLConnection connection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_information, container, false);

        TextView castleNameTV = view.findViewById(R.id.castle_name);
        TextView castleLocationTV = view.findViewById(R.id.castle_address);
        TextView castleRatingTV = view.findViewById(R.id.rating);
        TextView childPriceTV = view.findViewById(R.id.child_price);
        TextView adultPriceTV = view.findViewById(R.id.adult_price);
        ImageView castlePhotoImg = view.findViewById(R.id.castle_img);
        TextView openingTimeTV = view.findViewById(R.id.castle_times);
        TextView castleWebsiteTV = view.findViewById(R.id.website);
//
//        vcf = new ViewCastlesFragment();
//        castleNameTV.setText(vcf.getChosenCastle().replaceAll("%20", " "));
//        castleLocationTV.setText(vcf.getInfo().get("Address"));
//        castleRatingTV.setText(vcf.getInfo().get("Rating"));
//        adultPriceTV.setText(vcf.getInfo().get("AdultPrice"));
//        childPriceTV.setText(vcf.getInfo().get("ChildPrice"));
//        castleWebsiteTV.setText(vcf.getInfo().get("Website"));
//        openingTimeTV.setText(vcf.getInfo().get("Time"));


        try{
            GooglePlacesTask gpt = new GooglePlacesTask();
            gpt.execute();
            String results = gpt.get();
            String[] values = results.split("\n", 3);
            castleLocationTV.setText("        " + values[0].replaceAll(",", "\n       "));
            castleNameTV.setText(values[1]);
            castleRatingTV.setText("        " + values[2]);
        }catch(Exception e){
            e.printStackTrace();
        }

        return view;
    }

    /* Eventually this method will be called when a castle is clicked, it will run the places API */
    public void displayCastle(String name, String location) throws java.io.IOException{
        getCastleDetails(name);

    }


    public static void main(String args[]) {
        /* List of all the castles */



    }

    /* Method gathers castle details */
    private void getCastleDetails(String castle) throws IOException {
        Log.d("CASTLE", castle);
        Log.d("CASTLE", vcf.getChosenCastle());


//        setCastleDetails(name, address, rating, "temp");
    }
    // When called it updates the page information
//
//    private void setCastleDetails(){
//
//
//        String price = vcf.getInfo().get("Price");


//        castlePhotoImg.setImage(castleImgRef); Will hold the image


//    }



}
