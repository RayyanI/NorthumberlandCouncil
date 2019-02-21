/*
 * Handles the main activity of the application and creates listeners for the navigation bar
 * @author Rayyan Iqbal
 * @date 20/02/2019
 */
package uk.ac.ncl.northumberlandcouncil;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
        /* End activity loading */

    }

    /**
     * Center the logo in the tool bar programmatically
     * @param hasFocus - whether current window has focus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        Toolbar toolbar = findViewById(R.id.toolBar);
        ImageView logo = findViewById(R.id.logo);
        int offset = (toolbar.getWidth() / 2) - (logo.getWidth() / 2);
        // set
        logo.setX(offset);

    }
    /**
     * Handle displayed activity from menu select callback
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AuthorisationFragment()).commit();
                break;
            case R.id.nav_website:
                Toast.makeText(this, "Load website", Toast.LENGTH_SHORT).show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START); // Close navigation bar
        return true;
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
