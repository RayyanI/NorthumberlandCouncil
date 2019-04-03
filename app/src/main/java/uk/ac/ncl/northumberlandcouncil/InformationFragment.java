package uk.ac.ncl.northumberlandcouncil;


/* Begin library imports */
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("CHANGED");
        View view = inflater.inflate(R.layout.fragment_information, container, false);

        TextView castleNameTV = view.findViewById(R.id.castle_name);
        TextView castleLocationTV = view.findViewById(R.id.castle_address);
        TextView castleRatingTV = view.findViewById(R.id.rating);
        ImageView castlePhotoImg = view.findViewById(R.id.castle_img);
        TextView castleWebsiteTV = view.findViewById(R.id.castle_name);

        return inflater.inflate(R.layout.fragment_information, container, false);



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
        String chosenCastle = "Alnwick%20Castle";

    }

    /* Method gathers castle details */
    private void getCastleDetails(String castle) throws IOException {
        /* URL details for API to use and getter setup */
        url = new URL(urlString+castle+"&inputtype=textquery&fields=photos,formatted_address," +
                "name,rating&key="+key);
        HttpURLConnection connection;
        connection = (HttpURLConnection) url.openConnection();
        InputStream is = connection.getInputStream();
        /* Gather information retrieved with an input stream and buffer reader */
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String readLine;

        /* Split up all the information, this may not be the technique used in the final app but it
        will do for now */
        int currLine = 0;
        while ((readLine = in .readLine()) != null) {
            if(currLine == 3) {
                address = readLine.substring(32, readLine.length()-2);
            }else if(currLine == 4) {
                name = readLine.substring(19, readLine.length()-2);
            }else if(currLine == 11) {
                photoReference = readLine.substring(36, readLine.length()-2);
            }else if(currLine == 15) {
                if(readLine.substring(20).length() > 1) {
                    rating = readLine.substring(20, readLine.length());
                }else {
                    rating = readLine.substring(20);
                }

            }
            currLine++;
        }

        /* Test to display information */

        System.out.println("Name: " + name);
        System.out.println("Address: " + address);
        System.out.println("Photo Reference: " + photoReference);
        System.out.println("Rating: " + rating);
        System.out.println("================================");
    }

    // When called it updates the page information

    private void setCastleDetails(String castleN, String castleL, String castleR, String castleImgRef){
        castleNameTV.setText(castleN);
        castleLocationTV.setText(castleL);
        castleRatingTV.setText(castleR);
//        castlePhotoImg.setImage(castleImgRef); Will hold the image
    }

}
