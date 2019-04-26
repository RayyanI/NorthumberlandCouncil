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
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import okhttp3.HttpUrl;
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
 * Updated on 25/04/2019
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
    private ArrayList<String> castleInfo;   // Will store the raw castle database data
    private HashMap<String, String> refinedCastleInfo = new HashMap<>();    // Will store the only
                                                                            // necessary data
    private static int castleid;    // Castle's ID which links to the database castle ID
    private static String previousPage;    // Previous fragment's name
    private static Boolean IAS_STORE; // IAS Store for thread result
    private String API_URL = "http://18.130.117.241/";  // URL for our database API linked with PHP
    private boolean fave = false;   // Boolean to declare whether the castle is a favourite or not

    private HashMap<String, Integer> castleIDs = new HashMap<>();   // Castle name to castle ID
    private HashMap<String, Boolean> isDisabled = new HashMap<>();  // Castle name to accessability

    private ViewCastlesFragment vcf;// ViewCastlesFragment necassary to see what castle to display



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
        TextView ageRangeTV = view.findViewById(R.id.ageRange);
        TextView access = view.findViewById(R.id.disabilityDescription);
        ImageView disabledIV = view.findViewById(R.id.disabledIcon);

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

        isDisabled.put("Alnwick%20castle", true);
        isDisabled.put("Bamburgh%20castle", true);
        isDisabled.put("Warkworth%20castle", true);
        isDisabled.put("Lindisfarne%20castle", false);
        isDisabled.put("Mitford%20Castle", false);
        isDisabled.put("National%20Trust%20-%20Dunstanburgh%20Castle", true);
        isDisabled.put("Dunstanburgh%20castle", true);
        isDisabled.put("Chillingham%20castle", true);
        isDisabled.put("Berwick%20castle", true);
        isDisabled.put("Prudhoe%20castle", false);
        isDisabled.put("Edlingham%20castle", false);

        OkHttpClient client = new OkHttpClient();                       // WEB REQUEST //


        try {

            ViewCastlesFragment vcf = new ViewCastlesFragment();
            String chosenCastle = vcf.getChosenCastle();
            if(isDisabled.get(chosenCastle.replaceAll(" ", "%20")) == false){
                disabledIV.setVisibility(View.INVISIBLE);
            }
            String chosenImage = vcf.getChosenCastleImg();

            castleImg.setImageResource(getResources().getIdentifier(chosenImage,"drawable", BuildConfig.APPLICATION_ID));
            castleImg.setAdjustViewBounds(true);
            castleImg.setScaleType(ImageView.ScaleType.CENTER_CROP);

            System.out.println(castleIDs.get(vcf.getChosenCastle()));
            /* Check if the user is currently signed in to Google or Twitter */
            if (((MainActivity) getActivity()).getSignedInStatus()) {
                OkHttpClient web_client = new OkHttpClient();

                // Declarations //
                String tokenId;
                int castleId = castleIDs.get(vcf.getChosenCastle());
                // End Declarations //


                // Find this users unique identification number //
                if (((MainActivity) getActivity()).getTwitterSessionResult() != null) {
                    tokenId = Long.toString(((MainActivity) (getActivity())).getTwitterSessionResult().data.getUserId());
                } else {
                    tokenId = ((MainActivity) getActivity()).getGoogleSignInResult().getSignInAccount().getId();
                }

                // Has the user already favourited this castle? //


                // Setup the body of the request to include name-value pair of idToken //
                HttpUrl.Builder urlBuilder = HttpUrl.parse(API_URL + "API.php" + "?isFavourite").newBuilder();
                urlBuilder.addQueryParameter("tokenId", tokenId);
                urlBuilder.addQueryParameter("castleId", Integer.toString(castleId));


                // Build the URL for the request //
                String url = urlBuilder.build().toString();

                // Setup API URL //
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                // Execute network activity off of the main thread //
                boolean result;
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            Response response = web_client.newCall(request).execute();
                            String res = response.body().string();
                            if (res.equals("true")) {
                                IAS_STORE = true;
                            } else {
                                IAS_STORE = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                thread.join();
                ImageView starImg = view.findViewById(R.id.imageStar);
                if (IAS_STORE) {
                    starImg.setImageResource(getResources().getIdentifier("yellowstar", "drawable", BuildConfig.APPLICATION_ID));
                    starImg.setAdjustViewBounds(true);
                    fave = true;
                } else {
                    starImg.setImageResource(getResources().getIdentifier("staroutline", "drawable", BuildConfig.APPLICATION_ID));
                    starImg.setAdjustViewBounds(true);
                }

                starImg.bringToFront();


                starImg.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // If favourite button is selected, check its current state, if true (the star is already yellow and must go transparent)


                        // Delete this castle from this users favourites //
                        if (fave) {
                            deleteFavourite(castleId, tokenId);
                            if (IAS_STORE) {
                                starImg.setImageResource(getResources().getIdentifier("staroutline", "drawable", BuildConfig.APPLICATION_ID));
                                starImg.setAdjustViewBounds(true);
                                fave = false;
                            }
                        } else {
                            // Favourite this castle //
                            addFavourite(castleId, tokenId);
                            if (IAS_STORE) {
                                starImg.setImageResource(getResources().getIdentifier("yellowstar", "drawable", BuildConfig.APPLICATION_ID));
                                starImg.setAdjustViewBounds(true);
                                fave = true;
                            }
                        }
                    }
                });
            } else {
                view.findViewById(R.id.imageStar).setVisibility(View.INVISIBLE);
            }
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
                        Response disabilityResponse = client.newCall(request).execute();
                        String shortDescription = descResponse.body().string().split("shortDescription")[1].replaceAll(
                                "\"", "").replaceAll(":", "").split(",ageRange")[0];
                        String disabilityDescription = disabilityResponse.body().string().split("disabilityAccess")[1].replaceAll(
                                "\"", "").replaceAll(":", "").replace("}]", "");

                        Log.d("disabilityDescription0", disabilityDescription);

                        String res = response.body().string().replace(":", " ").replace("" +
                                "[{", "").replace("}]", "").replace("\"", "");
                        castleInfo = new ArrayList<String>(Arrays.asList(res.split(",")));
                        Log.d("worked", res);
                        for(String s : castleInfo){
                            String val1 = s.split(" ")[0];
                            if(!val1.equals("shortDescription") && !val1.equals("disabilityAccess")){
                                String val2 = s.split(" ")[1];
                                Log.d("value", val2);
                                refinedCastleInfo.put(val1, val2);
                            }else if(val1.equals("disabilityAccess")){
                                Log.d("DISABILITY", disabilityDescription);
                                refinedCastleInfo.put(val1, disabilityDescription);
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
            castleLocationTV.setText(values[0].replaceAll(",", "\n       "));
            castleNameTV.setText(values[1]);
            castleRatingTV.setText(values[2]);
            childPriceTV.setText("Adult: £" + refinedCastleInfo.get("childCost"));
            adultPriceTV.setText("Child: £" + refinedCastleInfo.get("adultCost"));
            castleWebsiteTV.setText(refinedCastleInfo.get("website"));
            openingTimeTV.setText(refinedCastleInfo.get("openingClosing"));
            ageRangeTV.setText(refinedCastleInfo.get("ageRange"));
            access.setText(refinedCastleInfo.get("disabilityAccess"));
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

    /**
     * Adds a castle to a users favourite list on our database given a valid castle and user identification number
     *
     * @param castleId (0-9)
     * @param tokenId  (Google/Twitter identification tokens)
     */
    private void addFavourite(int castleId, String tokenId) {
        OkHttpClient client = new OkHttpClient();

        // Setup the body of the request to include name-value pair of idToken //
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_URL + "API.php" + "?addFavourite").newBuilder();
        urlBuilder.addQueryParameter("tokenId", tokenId);
        urlBuilder.addQueryParameter("castleId", Integer.toString(castleId));


        // Build the URL for the request //
        String url = urlBuilder.build().toString();

        // Setup API URL //
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Execute network activity off of the main thread //
        boolean result;
        try {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        Response response = client.newCall(request).execute();
                        String res = response.body().string();
                        if (res.equals("Favourite added")) {
                            IAS_STORE = true;
                        } else {
                            IAS_STORE = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a castle from a users favourite list on our database given a valid castle and token identification number
     *
     * @param castleId (0-9) castleId
     * @param tokenId  (Google/Twitter token id)
     */
    private void deleteFavourite(int castleId, String tokenId) {
        OkHttpClient client = new OkHttpClient();

        // Setup the body of the request to include name-value pair of idToken //
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_URL + "API.php" + "?deleteFavourite").newBuilder();
        urlBuilder.addQueryParameter("tokenId", tokenId);
        urlBuilder.addQueryParameter("castleId", Integer.toString(castleId));


        // Build the URL for the request //
        String url = urlBuilder.build().toString();

        // Setup API URL //
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Execute network activity off of the main thread //
        boolean result;
        try {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        Response response = client.newCall(request).execute();
                        String res = response.body().string();
                        if (res.equals("Favourite deleted")) {
                            IAS_STORE = true;
                        } else {
                            IAS_STORE = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
