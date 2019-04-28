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
import java.net.ProtocolException;
import java.net.URL;

/**
 * Defines functionality for the getDirections button
 *
 * @author Jasper Griffin
 * Created 26/04/2019
 */

public class MapDirections extends AsyncTask<String, Object, String> {

    @Override
    protected String doInBackground(String... url) {

        Log.d("Background_start: ", "Fetching directions");

        String data = "";

        data = downloadUrl(url[0]);

        MapDirections mp = new MapDirections();
        String[] listOfPaths;

        if (data == null) {
            Log.d("StringData","Data is null");
            return null;
        }
        else {

            //parse data through and return of a list of paths
            listOfPaths = mp.parser(data);

            if (listOfPaths != null) {

                //Display polyline with list of paths
                MapFragment mf = new MapFragment();
                mf.fetchListOfPaths(listOfPaths);
            }
            else {
                Log.d("listOfPathsData","listOfPaths is null");
                return null;
            }

        }

        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    public String downloadUrl(String url) {

        Log.d("downloaded", "data downloaded");

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        InputStream inputStream = null;
        String data = "";

        //PopupWindow p = new PopupWindow();
        //p.getUrl(url);
        Log.d("Url status: ", "Url passed");

        URL newUrl = null;
        try {
            newUrl = new URL(url);
            Log.d("URLEQUALTO",url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //"origin=" + curloc.latitude + "," + casteloc.longitude
        //URL newUrl = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=54.9757,-1.5984&destination=54.964,-1.854&mode=driving&key=AIzaSyA-SYN3vPXJ0Z7Xgw7QhkhTl7fo9xL48yw");

        try {
            urlConnection = (HttpURLConnection) newUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                Log.d("inputStreamCheck","inputStream is null");
                return null;
            }
            StringBuffer buffer = new StringBuffer();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            while ((data = br.readLine()) != null) {
                buffer.append(data);
            }
            data = buffer.toString();

            if (data != null) {
                Log.d("dataPassedIsn'tNull","data is not null!");
                return data;
            }
            else {
                Log.d("dataPassedIsNull", "data is null");
                return null;
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }


    public String[] parser(String data) {

        JSONObject json;
        int count;
        String jsonStr[];

        try {

            json = new JSONObject(data);

            if (json != null) {

                JSONArray routes = json.getJSONArray("routes");
                JSONObject routesObject = routes.getJSONObject(0);
                JSONArray legs = routesObject.getJSONArray("legs");
                JSONObject legsObject = legs.getJSONObject(0);
                JSONArray steps = legsObject.getJSONArray("steps");

                if (steps == null) {
                    Log.d("Json","jsonArray is null");
                    return null;
                }
                else {

                    count = steps.length();
                    jsonStr = new String[count];

                    for (int i = 0; i < count; i++) {
                        JSONObject stepsObject = steps.getJSONObject(i);
                        jsonStr[i] = stepsObject.getJSONObject("polyline").getString("points");
                        Log.i("getFinalJsonString", jsonStr[i]);

                    }

                    if (jsonStr != null) {
                        return jsonStr;
                    }
                    else {
                        Log.d("jsonStr","jsonStr is null");
                        return null;
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }
}