package uk.ac.ncl.northumberlandcouncil;


/* Begin library imports */
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Bundle;
import android.os.StrictMode;
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Entity;
import org.w3c.dom.Text;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/* End library imports */


/**
 * Updates castle information page
 * @author Sean Fuller
 * Created on 03/03/2019
 * Updated on 10/03/2019
 * Updated on 14/03/2019
 * Updated on 09/04/2019
 * Updated on 11/04/2019
 * Updated on 13/04/2019
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
    private ArrayList<String> castleInfo;
    private HashMap<String, String> refinedCastleInfo = new HashMap<>();
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private static int castleid;
    private String castleLocation; /* API's castle location */
    private String castleName; /* Targeted castle name */
    private String ageRange; /* Age range for the castle */
    private int childPrice; /* Price for a child to enter */
    private int adultPrice; /* Price for an adult to enter */
    private static String previousPage;

    private String key = "AIzaSyA-SYN3vPXJ0Z7Xgw7QhkhTl7fo9xL48yw"; /* Places API key */
    private String urlString = "https://maps.googleapis.com/maps/api/place/findplacefromtext/" +
            "json?input="; /* Places API URL */
    private URL url;


    private String address;
    private String name;
    private boolean open;
    private boolean fave = false;
    private String photoReference;
    private String rating;
    private HashMap<String, Integer> castleIDs = new HashMap<>();
    String ip;
    String db;
    String DBUserNameStr;
    String DBPasswordStr;

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
        ImageView castleImg = view.findViewById(R.id.castle_img);
        ImageView backbutton = view.findViewById(R.id.backButton);
        TextView shortDescription = view.findViewById(R.id.shortDescription);



        castleIDs.put("Alnwick%20castle", 0);
        castleIDs.put("Bamburgh%20castle", 1);
        castleIDs.put("Warkworth%20castle", 2);
        castleIDs.put("Lindisfarne%20castle", 3);
        castleIDs.put("Mitford%20Castle", 4);
        castleIDs.put("National%20Trust%20-%20Dunstanburgh%20Castle", 5);
        castleIDs.put("Dunstanburgh%20castle", 5);
        castleIDs.put("Chillingham%20castle", 6);
        castleIDs.put("Berwick%20castle", 7);
        castleIDs.put("Prudhoe%20castle", 8);
        castleIDs.put("Edlingham%20castle", 9);

        OkHttpClient client = new OkHttpClient();                       // WEB REQUEST //

        String API_URL = "http://18.130.117.241/";



        try {

            ViewCastlesFragment vcf = new ViewCastlesFragment();
            String chosenCastle = vcf.getChosenCastle();
            Log.i("REFINED", chosenCastle.replaceAll(" ", "%20"));
            String chosenImage = vcf.getChosenCastleImg();

            castleImg.setImageResource(getResources().getIdentifier(chosenImage,"drawable", BuildConfig.APPLICATION_ID));
            castleImg.setAdjustViewBounds(true);
            castleImg.setScaleType(ImageView.ScaleType.CENTER_CROP);

            ImageView starImg = view.findViewById(R.id.imageStar);
            starImg.bringToFront();


            starImg.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(fave == false){
                        fave = true;
                    }else{
                        fave = false;
                    }
                    if(fave){
                        starImg.setImageResource(getResources().getIdentifier("yellowstar","drawable", BuildConfig.APPLICATION_ID));
                        starImg.setAdjustViewBounds(true);
                    }else{
                        starImg.setImageResource(getResources().getIdentifier("staroutline","drawable", BuildConfig.APPLICATION_ID));
                        starImg.setAdjustViewBounds(true);
                    }

                }
            });
            String casid = Integer.toString(vcf.getId());
            Log.i("castle", chosenCastle);
            // Setup the body of the request to include name-value pair of idToken //
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("idToken", Integer.toString(castleIDs.get(chosenCastle.replaceAll(" ", "%20"))))
                    .build();
            Request request = new Request.Builder()
                    .url(API_URL + "InformationAPI.php")
                    .post(requestBody)
                    .build();
            // Execute network activity off of the main thread //


            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    try {
                        Response response = client.newCall(request).execute();
                        Response descResponse = client.newCall(request).execute();
                        String shortDescription = descResponse.body().string().split("shortDescription")[1].replaceAll(
                                "\"", "").replaceAll(":", "").split(",ageRange")[0];

                        String res = response.body().string().replace(":", " ").replace("" +
                                "[{", "").replace("}]", "").replace("\"", "");
                        castleInfo = new ArrayList<String>(Arrays.asList(res.split(",")));
                        Log.d("worked", res);
                        for(String s : castleInfo){
                            String val1 = s.split(" ")[0];
                            if(!val1.equals("shortDescription")){
                                String val2 = s.split(" ")[1];
                                Log.d("value", val2);
                                refinedCastleInfo.put(val1, val2);
                            }else{
                                refinedCastleInfo.put(val1, shortDescription);
                            }


                        }
                    } catch (Exception e) {
                        Log.e("response", "failure");
                        e.printStackTrace();
                    }
                }
            });
            Log.d("threadRun", "b4");
        t1.start();
        t1.join();
        Log.d("threadRun", "after");
        }catch(Exception e){
            Log.e("FTP_ERROR", e.getMessage());
       }

        try {
            GooglePlacesTask gpt = new GooglePlacesTask();
            gpt.execute();
            String results = gpt.get();
            String[] values = results.split("\n", 3);
            castleLocationTV.setText("        " + values[0].replaceAll(",", "\n       "));
            castleNameTV.setText(values[1]);
            castleRatingTV.setText("        " + values[2]);
            childPriceTV.setText("        £" + refinedCastleInfo.get("childCost"));
            adultPriceTV.setText("        £" + refinedCastleInfo.get("adultCost"));
            castleWebsiteTV.setText("        " + refinedCastleInfo.get("website"));
            openingTimeTV.setText("        " + refinedCastleInfo.get("openingClosing"));
            shortDescription.setText(refinedCastleInfo.get ("shortDescription"));
        } catch (Exception e) {
            Log.e("FTPFAIL", "hi");
            e.printStackTrace();
        }

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(previousPage.equals("ViewCastlesFragment")){
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewCastlesFragment()).commit();
                }else{
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit();
                }

            }
        });

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

    public static void setPreviousPage(String prevPage){
        previousPage = prevPage;
    }
    public static void setID(int id){
        castleid = id;
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
