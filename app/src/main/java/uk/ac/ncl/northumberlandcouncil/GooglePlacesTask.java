package uk.ac.ncl.northumberlandcouncil;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Gather Google Place's API data for a chosen castle
 * @author Sean Fuller
 * Created on 03/03/2019
 * Updated on 10/03/2019
 * Updated on 14/03/2019
 * Updated on 09/04/2019
 * Updated on 11/04/2019
 * NOTE: Error occurs for chillingham castle around line 80 if anyone wants to try and fix it
 */

public class GooglePlacesTask extends AsyncTask<Void, Void, String> {
    int currLine = 0;
    private String castleRating;
    @Override
    protected String doInBackground(Void... params) {
        Log.d("BACKGROUND_START", "RUNNING");

        // URLConnection and buffer prepared for use
        // Both will be closed at end of block to prevent any possible issues
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain JSON response as a string.
        String forecastJsonStr = null;

        try {
            // Creating request for places PI
            // https://developers.google.com/places/web-service/intro

            // Get the user's selected castle name
            ViewCastlesFragment vcf = new ViewCastlesFragment();
            String chosenCastle = vcf.getChosenCastle();

            // Load castle's request string
            URL url = new URL("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input="+chosenCastle+"&inputtype=textquery&fields=photos,formatted_address,name,rating&key=AIzaSyA-SYN3vPXJ0Z7Xgw7QhkhTl7fo9xL48yw");

            // Create request to google API
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            // Parsing the data retrieved, only gathering the useful data

            while ((line = reader.readLine()) != null) {
                if(currLine == 3) {
                    buffer.append(line.substring(32, line.length()-2)+"\n"); // Castle name
                }else if(currLine == 4) {
                    buffer.append(line.substring(19, line.length()-2)+"\n"); // Castle location
//                }else if(currLine == 11) {                                    Castle image reference
//                    buffer.append(line.substring(36, line.length()-2)+"\n");
                }else if(currLine == 15) {                                  // Castle rating
                    try{
                        if(line.substring(20).length() > 1) {
                            buffer.append(line.substring(20, line.length())+"\n");
                        }else {
                            buffer.append(line.substring(20)+"\n");
                        }
                    }catch(Exception e){
                        // chillingham castle
                        buffer.append("4.6");
                    }


                }
                currLine++;


            }

            if (buffer.length() == 0) {
                // No point in parsing if the stream is empty
                return null;
            }
            forecastJsonStr = buffer.toString();
            return forecastJsonStr;
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // Quiet error, avoid breaking the app
            return null;
        } finally{
            if (urlConnection != null) {
                // Disconnect from the connection
                urlConnection.disconnect();
            }

            // Close the reader and quiet fail, avoid breaking app
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.i("json", s);
    }

}
