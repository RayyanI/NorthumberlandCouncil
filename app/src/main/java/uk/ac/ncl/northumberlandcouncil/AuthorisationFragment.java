package uk.ac.ncl.northumberlandcouncil;

/* Begin library imports */

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.core.Callback;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.SignInButton;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
/* End library imports */

/**
 * Handles code for Authorisation Fragment
 *
 * @author Rayyan Iqbal
 * Created on 21/02/2019
 * Last modified 21/02/2019 (R Iqbal)
 */
public class AuthorisationFragment extends Fragment {
    /* Declarations */
    TwitterLoginButton twitterLoginButton;
    private GoogleApiClient mGoogleApiClient;
    /* End Declarations */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // Select the buttons //
        twitterLoginButton = view.findViewById(R.id.twitterLoginButton);
        SignInButton signInButton = view.findViewById(R.id.googleLoginButton);

        /* Google */
        // Style buttons to produce similar widths and heights //
        signInButton.setStyle(SignInButton.SIZE_WIDE, SignInButton.COLOR_LIGHT); // GoogleSignIn width & colours
        /* Twitter */
        twitterLoginButton.setTextSize(14);
        twitterLoginButton.setTypeface(Typeface.DEFAULT_BOLD);

        /* Handle twitter login */
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            /**
             * Actions to do when a login attempt is successful
             * @param result - call back
             */
            @Override
            public void success(Result<TwitterSession> result) {
                System.out.println(result.data.getUserName());
                updateUI(result.data);

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
        // Pass the activity result to the twitter login button
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Update the user interface upon a successful login
     *
     * @param result object containing user information
     */
    private void updateUI(TwitterSession result) {
        twitterLoginButton.setText(result.getUserName());
        System.out.println(result.getUserName());
    }


}
