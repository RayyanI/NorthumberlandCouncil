package uk.ac.ncl.northumberlandcouncil;


/* Begin library imports */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/* End library imports */

/**
 * Handles the main activity of the application and creates listeners/formats navigation
 *
 * @author Rayyan Iqbal
 * Created on 20/02/2019
 * Last modified 05/04/2019 (R Iqbal)
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /* Class Variables */

    private DrawerLayout drawer;
    private Result<TwitterSession> twitterSessionResult = null;
    private GoogleSignInResult googleSignInResult = null;
    private Map<String,String> userMap =  new HashMap<String,String>();
    /* End Class Variables */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /* Create twitter context */
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.com_twitter_sdk_android_CONSUMER_KEY), getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);
        /* End context creation */



        /* Add menu button */
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        /* Remove toolbar text */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        /* End removal of toolbar text */

        drawer = findViewById(R.id.drawer_layout);

        /* Handle navigation bar clicks */
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /* End handling */

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        /* End menu button */

        /* Hide logout option if not logged in */
        hideLogoutFromDrawer();


        /* Load default activity on start, check instance status to prevent double load upon orientation change */
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AuthorisationFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);

        }
        /* End activity loading */

    }

    /**
     * Center the logo in the tool bar programmatically
     *
     * @param hasFocus - whether current window has focus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Toolbar toolbar = findViewById(R.id.toolBar);
        ImageView logo = findViewById(R.id.logo);
        int offset = (toolbar.getWidth() / 2) - (logo.getWidth() / 2);
        // set
        logo.setX(offset);

    }

    /**
     * Handle displayed activity from menu select callback
     *
     * @param menuItem - item selected
     * @return boolean - upon selection
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
            case R.id.nav_favourites:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FavouritesFragment()).commit();
                break;
            case R.id.nav_login:
                if (getSupportActionBar() != null)
                    getSupportActionBar().hide(); // hide action bar on login page
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AuthorisationFragment()).commit();
                break;
            case R.id.mapactivity:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit();
                break;
            case R.id.nav_website:
                Toast.makeText(this, "Load website", Toast.LENGTH_SHORT).show();
                goToWebsite(this);
                break;
            case R.id.nav_view_castles:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewCastlesFragment()).commit();
                break;
            case R.id.nav_logout:
                logout();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START); // Close navigation bar
        return true;
    }

    /**
     * Handle the changes to UI upon a login successful login request for Twitter
     *
     * @param twitterSessionResult - twitter login object
     */
    protected void onLoginResult(Result<TwitterSession> twitterSessionResult) {
        /* Update UI */
        this.twitterSessionResult = twitterSessionResult; // Let's save this session
        this.googleSignInResult = null; // In the event that a user signed in with Google then Twitter (defensive)
        String url = "http://18.130.117.241/API.php?getNameAndEmail&tokenId=" + Long.toString(twitterSessionResult.data.getUserId());
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            /* Submit the GET Request to the API and wait for synchronisation */
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response responses = client.newCall(request).execute();
                        String jsonData = responses.body().string();

                        // Parse the response from the server into a JSON Object and then parse that JSON object for data //
                        JSONObject jsonObject = new JSONArray(jsonData).getJSONObject(0);
                        userMap.put("name", jsonObject.getString("firstName") + " " + jsonObject.getString("lastName"));
                        userMap.put("email", jsonObject.getString("email"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            thread.join();

            ((TextView) findViewById(R.id.username)).setText(userMap.get("name"));
            ((TextView) findViewById(R.id.email)).setText(userMap.get("email"));



        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Pull name and email address from our database */


        hideLoginFromDrawer();
        showLogoutFromDrawer();
        showAccSettingsFromDrawer();

    }


    /**
     * Handle the changes to UI upon a successful login request for Google
     *
     * @param result google login object
     */
    protected void onLoginResult(GoogleSignInResult googleSignInResult) {
        /* Update UI */
        GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
        ((TextView) findViewById(R.id.username)).setText(googleSignInAccount.getDisplayName());
        ((TextView) findViewById(R.id.email)).setText(googleSignInAccount.getEmail());


        /* Setup declarations */
        this.googleSignInResult = googleSignInResult;
        this.twitterSessionResult = null; // In the event that a user signed in with Twitter, then Google (defensive)
        hideLoginFromDrawer();
        showLogoutFromDrawer();
        showAccSettingsFromDrawer();
    }

    /**
     * Show account settings from drawer menu
     */
    protected void showAccSettingsFromDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_settings).setVisible(true);
    }


    /**
     * Show account settings from drawer menu
     */
    protected void hideAccSettingsFromDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_settings).setVisible(false);
    }

    /**
     * Hide login fragment from drawer menu
     */
    protected void hideLoginFromDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
    }

    /**
     * Show login fragment from drawer menu
     */
    protected void showLoginFromDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
    }


    /**
     * Hide logout from drawer menu
     */
    protected void hideLogoutFromDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
    }

    /**
     * Show logout from drawer menu
     */
    protected void showLogoutFromDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
    }


    /**
     * Logout a user that is currently logged in from the system
     */
    public void logout() {
        /* Handle twitter logout */
        if (twitterSessionResult != null) {
            TwitterCore.getInstance().getSessionManager().clearActiveSession();
            twitterSessionResult = null;
        }

        /* Handle google logout */
        if (googleSignInResult != null) {
            Auth.GoogleSignInApi.signOut(AuthorisationFragment.mGoogleApiClient);
            googleSignInResult = null;

        }

        /* Handle UI changes */
        hideLogoutFromDrawer();
        showLoginFromDrawer();
        hideAccSettingsFromDrawer();
        ((TextView) findViewById(R.id.username)).setText("Brown Fox");
        ((TextView) findViewById(R.id.email)).setText("BrownFox@gmail.com");


        /* Let's also update the current fragment being displayed */
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

    }


    /**
     * Restores the action bar
     */
    protected void restoreActionBar() {
        onWindowFocusChanged(true);
        if (getSupportActionBar() != null)
            getSupportActionBar().show();


    }

    /**
     * Handle activity results and pass them to appropriate fragments
     *
     * @param requestCode - integer req code
     * @param resultCode  - response from server
     * @param data        - data i.e. login
     */

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof AuthorisationFragment) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /**
     * Override on back pressed method to deal with closing of navigation bar before display
     */
    @Override
    public void onBackPressed() {
        /* Handle closing of navigation bar before next activity is displayed */
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed(); // close activity as usual
        }
        /* End handling */

    }

    public Result<TwitterSession> getTwitterSessionResult() {
        return twitterSessionResult;
    }

    public GoogleSignInResult getGoogleSignInResult() {
        return googleSignInResult;
    }

    public void goToWebsite(MainActivity view) {
        Intent openBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://northumberlandcastles.co.uk"));
        startActivity(openBrowser);
    }

}
