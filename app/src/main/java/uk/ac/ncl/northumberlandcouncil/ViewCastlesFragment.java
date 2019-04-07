package uk.ac.ncl.northumberlandcouncil;


/* Begin library imports */
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.*;
import java.net.*;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
/* End library imports */


/**
 * Handles code for Home Fragment
 * @author Sean Fuller
 * Created on 03/03/2019
 * Updated on 10/03/2019
 * Updated on 14/03/2019
 * Updated on 06/04/2019
 * Updated on 07/04/2019
 */

public class ViewCastlesFragment extends Fragment {
    private Button alnwickButton;
    private static TextView cName;
    private static String chosenCastle;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_viewcastles, container, false);


        alnwickButton = view.findViewById(R.id.alnwick);
        Button warkworthButton = (Button) view.findViewById(R.id.warkworth);
        Button bamburghButton = (Button) view.findViewById(R.id.bamburgh);
        Log.d("CREATION", "created");
        alnwickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new InformationFragment()).commit();
                chosenCastle = alnwickButton.getText().toString();
            }
        });

        warkworthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new InformationFragment()).commit();
                chosenCastle = warkworthButton.getText().toString();
            }
        });

        bamburghButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICKED", "clicked");
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new InformationFragment()).commit();
                chosenCastle = bamburghButton.getText().toString();
            }
        });


        return view;

    }
    public static String getChosenCastle(){
        return chosenCastle.replaceAll("\\s", "%20");
    }

}
