package uk.ac.ncl.northumberlandcouncil;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Defines functionality for the getDirections button
 *
 * @author Jasper Griffin
 * Created 26/04/2019
 */

public class MapDirections extends AsyncTask<String, Void, String> {


    @Override
    protected String doInBackground(String... url) {
        Log.d("Background_start: ", "Fetching directions");

        String data = "";
        try {
            Log.d("downloaded", "data downloaded");
            data = downloadUrl(url[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(String data) {

        String[] listOfPaths;
        MapDirections mp = new MapDirections();
        PopupWindow pop = new PopupWindow();

        //CALL TO DIRECTIONS
        listOfPaths = mp.Directions(data);

        if (listOfPaths != null) {
            pop.displayPolyline(listOfPaths);
        } else {
            return;
        }


    }


    //call method from async
    private String downloadUrl(String url) throws IOException {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        InputStream inputStream = null;
        String data = "";
        String finalData = "";

        PopupWindow p = new PopupWindow();
        p.getUrl(url);
        Log.d("Url status: ", "Url passed");

        URL newUrl = new URL(url);
        urlConnection = (HttpURLConnection) newUrl.openConnection();
        urlConnection.connect();

        inputStream = urlConnection.getInputStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer buffer = new StringBuffer();

        if ((data = br.readLine()) != null) {
            buffer.append(data);
        }
        finalData = br.toString();

        if (finalData != null) {
            return finalData;
        }
        else {
            Log.d("data", "data is null");
            return null;
        }

    }



    public String[] Directions(String finalData) {

        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(finalData);

            if (jsonObject != null) {
                jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
            }

        } catch (JSONException e) {
            Log.d("Json object:", "Json Object is null");
            e.printStackTrace();
        }

        Log.d("jsonArray", "jsonArray is empty");
        return null;

    }
}