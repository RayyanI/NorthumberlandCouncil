package uk.ac.ncl.northumberlandcouncil;


/* Begin library imports */
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
/* End library imports */


/**
 * Handles code for Favourites Fragment
 * @author Rayyan Iqbal
 * Created on 11/04/2019
 * Last modified 11/04/2019 (R Iqbal)
 */
public class SettingsFragment extends Fragment {
    // Declarations //
    private GoogleSignInClient mGoogleSignInClient;


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
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }


}
