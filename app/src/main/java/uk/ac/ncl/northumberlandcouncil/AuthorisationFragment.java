package uk.ac.ncl.northumberlandcouncil;

/* Begin library imports */
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.SignInButton;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
/* End library imports */

/**
 * Handles code for Authorisation Fragment
 * @author Rayyan Iqbal
 * Created on 21/02/2019
 * Last modified 21/02/2019 (R Iqbal)
 */
public class AuthorisationFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Setup activity & possible exceptions */
        FragmentActivity fragmentActivity;
        if (getActivity() != null) {
            fragmentActivity = getActivity();
        } else {
            throw new NullPointerException ("Activity undefined");
        }

        /* Setup twitter config with keys */
        TwitterConfig config = new TwitterConfig.Builder(fragmentActivity)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.com_twitter_sdk_android_CONSUMER_KEY), getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }
    /**
     * Inflate fragment upon selection
     * @param inflater for instancing xml fragments
     * @param container container to display content stream
     * @param savedInstanceState current activity datas store
     * @return display xml view on screen
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_authorisation, container, false);

        /* Configure UI */

        // Select the buttons //
        TwitterLoginButton twitterLoginButton = view.findViewById(R.id.twitterLoginButton);
        SignInButton signInButton = view.findViewById(R.id.googleLoginButton);

        // Style buttons to produce similar widths and heights //
        signInButton.setStyle(SignInButton.SIZE_WIDE, SignInButton.COLOR_LIGHT); // GoogleSignIn width & colours

        /* Twitter */
        twitterLoginButton.setTextSize(14);
        twitterLoginButton.setTypeface(Typeface.DEFAULT_BOLD);




        return view;

    }
}
