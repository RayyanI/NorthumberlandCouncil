package uk.ac.ncl.northumberlandcouncil;


/* Begin library imports */
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Entity;

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

    private String key = "AIzaSyA-SYN3vPXJ0Z7Xgw7QhkhTl7fo9xL48yw"; /* Places API key */
    private String urlString = "https://maps.googleapis.com/maps/api/place/findplacefromtext/" +
            "json?input="; /* Places API URL */
    private URL url;


    private String address;
    private String name;
    private boolean open;
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


        castleIDs.put("Alnwick%20castle", 0);
        castleIDs.put("Bamburgh%20castle", 1);

        OkHttpClient client = new OkHttpClient();                       // WEB REQUEST //

        String API_URL = "http://18.130.117.241/";

        vcf = new ViewCastlesFragment();
        String casteID = Integer.toString(vcf.getCastleID());

        try {

            ViewCastlesFragment vcf = new ViewCastlesFragment();
            String chosenCastle = vcf.getChosenCastle();
            String casid = Integer.toString(vcf.getId());
            // Setup the body of the request to include name-value pair of idToken //
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("idToken", Integer.toString(castleIDs.get(chosenCastle)))
                    .build();
            Request request = new Request.Builder()
                    .url(API_URL + "testPHP.php")
                    .post(requestBody)
                    .build();
            // Execute network activity off of the main thread //


            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    try {
                        Response response = client.newCall(request).execute();
                        String res = response.body().string().replace(":", " ").replace("" +
                                "[{", "").replace("}]", "").replace("\"", "");
                        castleInfo = new ArrayList<String>(Arrays.asList(res.split(",")));
                        Log.d("worked", res);
                        for(String s : castleInfo){
                            String val1 = s.split(" ")[0];
                            Log.d("value", s);
                            String val2 = s.split(" ")[1];
                            refinedCastleInfo.put(val1, val2);
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
            childPriceTV.setText("        " + refinedCastleInfo.get("childCost"));
            adultPriceTV.setText("        " + refinedCastleInfo.get("adultCost"));
            castleWebsiteTV.setText("        " + refinedCastleInfo.get("website"));
            openingTimeTV.setText("        " + refinedCastleInfo.get("openingClosing"));
        } catch (Exception e) {
            Log.e("FTPFAIL", "hi");
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
