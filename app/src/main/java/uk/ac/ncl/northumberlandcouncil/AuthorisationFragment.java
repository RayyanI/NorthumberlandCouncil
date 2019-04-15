package uk.ac.ncl.northumberlandcouncil;

/* Begin library imports */

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;


import com.google.android.gms.common.ConnectionResult;
import com.twitter.sdk.android.core.Callback;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/* End library imports */

/**
 * Handles code for Authorisation Fragment
 *
 * @author Rayyan Iqbal
 * Created on 21/02/2019
 * Last modified 05/04/2019 (R Iqbal)
 */
public class AuthorisationFragment extends Fragment implements  GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.googleLoginButton:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_LOG_IN);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /* Declarations */
    private static final int RC_LOG_IN = 3820;
    public static GoogleApiClient mGoogleApiClient;
    private TwitterLoginButton twitterLoginButton;



    /* End Declarations */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Handle google login */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        if(mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity() /* Context */)
                    .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }

    }

    /**
     * Inflate fragment upon selection
     *
     * @param inflater           for instancing xml fragments
     * @param container          container to display content stream
     * @param savedInstanceState current activity datas store
     * @return display xml view on screen
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authorisation, container, false);

        /* Configure UI */

        // Select the buttons and text elements //
        twitterLoginButton = view.findViewById(R.id.twitterLoginButton);
        SignInButton signInButton = view.findViewById(R.id.googleLoginButton);
        TextView guestText = view.findViewById(R.id.guestText);


        // Style buttons to produce similar widths and heights //
        signInButton.setStyle(SignInButton.SIZE_WIDE, SignInButton.COLOR_LIGHT); // GoogleSignIn width & colours

        /* Guest */
        guestText.setOnClickListener((View v) -> updateUI());
        /* Twitter */
        twitterLoginButton.setTextSize(14);
        twitterLoginButton.setTypeface(Typeface.DEFAULT_BOLD);


        view.findViewById(R.id.googleLoginButton).setOnClickListener(this);

        /* Handle twitter login */
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            /**
             * Actions to do when a login attempt is successful
             * @param result - call back
             */
            @Override
            public void success(Result<TwitterSession> result) {
                System.out.println(result.data.getUserName());
                ((MainActivity) getActivity()).onLoginResult(result); // update ui in activity
                updateUI();


            }

            /**
             * Actions to do when a login attempt is unsuccessfull
             * @param exception - exception thrown
             */
            @Override
            public void failure(TwitterException exception) {
                exception.printStackTrace();
                // Do something on failure
            }
        });


        return view;

    }

    /**
     * Handle authorisation responses, passed from this fragment's activity container
     *
     * @param requestCode request code (google / twitter / facebook)
     * @param resultCode  response ( success / fail)
     * @param data        object containing response data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* If the login is for Google */
        if (requestCode == RC_LOG_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            System.out.println(result.getStatus());
            handleSignInResult(result);
        }

        // Pass the activity result to the twitter login button
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }


    private void handleSignInResult(GoogleSignInResult result ) {
        if (result.isSuccess()) {

            // Pass the user data to the backend server for authentication via HTTPS Post //

            OkHttpClient client = new OkHttpClient();
            String idToken = result.getSignInAccount().getIdToken();
            System.out.println(idToken);
            try {
                // Setup the body of the request to include name-value pair of idToken //
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("idToken", idToken)
                        .build();


                // Setup API URL //
                Request request = new Request.Builder()
                        .url("http://18.130.117.241/validation.php")
                        .post(requestBody)
                        .build();


                // Execute network activity off of the main thread //
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Response response = client.newCall(request).execute();
                            System.out.println(response.body().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                ((MainActivity) getActivity()).onLoginResult(result); // update ui in activity
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateUI();



        }


    }
    /**
     * Update the user interface upon a successful login by instancing the home fragment, and removing the current from stack
     *
     */
    private void updateUI() {
        ((MainActivity) getActivity()).restoreActionBar();
        getFragmentManager().beginTransaction()
                .replace(((ViewGroup) getView().getParent()).getId(), new HomeFragment())
                .addToBackStack(null)
                .commit();
    }


}
