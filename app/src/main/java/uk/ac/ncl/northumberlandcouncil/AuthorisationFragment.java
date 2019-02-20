/**
 * Handles code for Authorisation Fragment
 * @author Rayyan Iqbal
 * @date 20/02/2019
 */
package uk.ac.ncl.northumberlandcouncil;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AuthorisationFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_authorisation, container, false);
    }
}
