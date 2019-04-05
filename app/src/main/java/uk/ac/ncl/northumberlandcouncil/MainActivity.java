package uk.ac.ncl.northumberlandcouncil;


/* Begin library imports */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.List;

/* End library imports */

/**
 * Handles the main activity of the application and creates listeners/formats navigation
 *
 * @author Rayyan Iqbal
 * Created on 20/02/2019
 * Last modified 05/04/2019 (R Iqbal)
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

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
                MapFragment mapFragment = new MapFragment();
                FragmentManager manage = getSupportFragmentManager();
                manage.beginTransaction().replace(R.id.mapactivity, mapFragment).commit();

                 break;
            case R.id.nav_view_castles:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewCastlesFragment()).commit();
                break;
            case R.id.nav_website:
                Toast.makeText(this, "Load website", Toast.LENGTH_SHORT).show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START); // Close navigation bar
        return true;
    }

    /**
     * Handle the changes to UI upon a login successful login request for Twitter
     * @param twitterSessionResult - twitter login object
     */
    protected void onLoginResult (Result<TwitterSession> twitterSessionResult) {
        /* Update UI */
        ((TextView) findViewById(R.id.username)).setText(twitterSessionResult.data.getUserName());

        hideLoginFromDrawer();

    }


    /**
     * Handle the changes to UI upon a successful login request for Google
     * @param result google login object
     */
    protected void onLoginResult (GoogleSignInResult result) {
        /* Update UI */
        GoogleSignInAccount googleSignInAccount = result.getSignInAccount();
        ((TextView) findViewById(R.id.username)).setText(googleSignInAccount.getDisplayName());
        ((TextView) findViewById(R.id.email)).setText(googleSignInAccount.getEmail());

        hideLoginFromDrawer();
    }


    /**
     * Hide login fragment from drawer menu
     */
    protected void hideLoginFromDrawer () {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
    }
    /**
     * Restores the action bar
     */
    protected void restoreActionBar () {
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
}
