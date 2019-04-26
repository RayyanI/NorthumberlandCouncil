package uk.ac.ncl.northumberlandcouncil;


/* Begin library imports */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterSession;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static uk.ac.ncl.northumberlandcouncil.AuthorisationFragment.validateEmail;
import static uk.ac.ncl.northumberlandcouncil.AuthorisationFragment.validateName;
/* End library imports */


/**
 * Handles code for Favourites Fragment
 * @author Rayyan Iqbal
 * Created on 11/04/2019
 * Last modified 11/04/2019 (R Iqbal)
 */
public class SettingsFragment extends Fragment {
    // Declarations //
    private GoogleApiClient mGoogleApiClient;
    private final String API_URL = "http://18.130.117.241/";
    private Boolean changedField;
    private String tokenId;
    private String firstName;
    private String lastName;
    private String email;
    /* End Declarations */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if this user is part of a Google login & instance appropriate singleton //
        if (AuthorisationFragment.mGoogleApiClient != null) {
            mGoogleApiClient = AuthorisationFragment.mGoogleApiClient;
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        /* Setup the buttons from our layout */
        Button revokeGoogleButton = (Button) view.findViewById(R.id.revokeGoogle);
        Button revokeTwitterButton = (Button) view.findViewById(R.id.revokeTwitter);
        Button changeName = (Button) view.findViewById(R.id.changeName);
        Button changeEmail = (Button) view.findViewById(R.id.changeEmailAddress);


        /* Detect if the user is signed in with Google or Twitter, change UI Thread appropriately & find token id */
        if (((MainActivity) (getActivity())).getTwitterSessionResult() == null) {
            revokeTwitterButton.setVisibility(View.INVISIBLE);
            tokenId = ((MainActivity) getActivity()).getGoogleSignInResult().getSignInAccount().getId();

        } else {
            revokeGoogleButton.setVisibility(View.INVISIBLE);
            tokenId = Long.toString(((MainActivity) (getActivity())).getTwitterSessionResult().data.getUserId());
        }



        /* Setup confirmation dialogue */
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Are you sure you want to revoke your account?");
        builder.setMessage("This will remove our access to your account and delete all stored data from our system");
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        /* End setup of confirmation dialogue */
        revokeGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleApiClient.isConnected()) {
                    builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(@NonNull Status status) {
                                            ((MainActivity) getActivity()).logout();
                                        }
                                    });
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        revokeTwitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Get this users Twitter object from the Main Activity and delete records */
                Result<TwitterSession> twitterSessionResult = ((MainActivity) getActivity()).getTwitterSessionResult();
                if (twitterSessionResult != null) {
                    builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((MainActivity) getActivity()).logout(); // Revoke and logout //
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Setup the popup dialog */
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                Context context = getContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                dialog.setTitle("Change email address");

                // Add a TextView here for the "Title" label, as noted in the comments
                final EditText emailBox = new EditText(context);
                emailBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                emailBox.setHint("Email Address");
                layout.addView(emailBox); // Notice this is an add method
                dialog.setView(layout);

                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        email = emailBox.getText().toString();

                        // Handle invalid arguments that may have got past our checks. //
                        if (email.length() == 0) {
                            throw new IllegalArgumentException("Email not given.");
                        }

                        /* Work with the output now */
                        try {

                            OkHttpClient client = new OkHttpClient();


                            // Setup the body of the request to include name-value pair of idToken //
                            HttpUrl.Builder urlBuilder = HttpUrl.parse(API_URL + "API.php" + "?changeEmailAddress").newBuilder();
                            urlBuilder.addQueryParameter("tokenId", tokenId);
                            urlBuilder.addQueryParameter("email", email);


                            // Build the URL for the request //
                            String url = urlBuilder.build().toString();

                            // Setup API URL //
                            Request request = new Request.Builder()
                                    .url(url)
                                    .build();

                            // Execute network activity off of the main thread //
                            Thread thread = new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        Response response = client.newCall(request).execute();
                                        String res = response.body().string();
                                        changedField = false;
                                        if (res.equals("Email successfully changed")) {
                                            changedField = true;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            /* Wait for thread to finish & inform the user if the change was successful */
                            thread.start();
                            thread.join();
                            if (changedField) {
                                Toast.makeText(getActivity().getApplicationContext(), "Your email address has been changed. Relog to see changes.", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // Call thew appropriate API function to change this users email address //


                    }
                });

                // Setup the dialog and only allow the positive button to be pressed if text is given //
                final AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                // Enable the button only when the text fields are valid //
                emailBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // Check if either of the two edit texts are not filled //
                        if (emailBox.getText().toString().isEmpty())
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false); // Defensive


                        // Double check email address and name //
                        if (!emailBox.getText().toString().isEmpty())
                            if (!(validateEmail(emailBox.getText().toString())))
                                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false); // Defensive
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                        // Check if both of the two edit texts are now filled //
                        if (!emailBox.getText().toString().isEmpty()) {
                            // Validate email address before enabling the button //
                            if (validateEmail(emailBox.getText().toString()))
                                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                        } else {
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true); // Defensive
                        }
                    }
                });
            }
        });

        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                Context context = getContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                dialog.setTitle("Change first and last name");


                // Add a TextView here for the "Title" label, as noted in the comments
                final EditText nameBox = new EditText(context);
                nameBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                nameBox.setHint("Full Name");
                layout.addView(nameBox); // Notice this is an add method
                dialog.setView(layout);
                // Set up the buttons
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        firstName = nameBox.getText().toString();
                        lastName = nameBox.getText().toString();

                        // Handle invalid arguments that may have got past our checks. //
                        if (firstName.length() == 0 || lastName.length() == 0) {
                            throw new IllegalArgumentException("Name not given.");
                        }

                        /* Parse the name now */
                        firstName = firstName.split(" ")[0];
                        lastName = lastName.split(" ")[1];

                        /* Work with the output now */
                        try {

                            OkHttpClient client = new OkHttpClient();


                            // Setup the body of the request to include name-value pair of idToken //
                            HttpUrl.Builder urlBuilder = HttpUrl.parse(API_URL + "API.php" + "?changeUsername").newBuilder();
                            urlBuilder.addQueryParameter("tokenId", tokenId);
                            urlBuilder.addQueryParameter("firstName", firstName);
                            urlBuilder.addQueryParameter("lastName", lastName);


                            // Build the URL for the request //
                            String url = urlBuilder.build().toString();

                            // Setup API URL //
                            Request request = new Request.Builder()
                                    .url(url)
                                    .build();

                            // Execute network activity off of the main thread //
                            Thread thread = new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        Response response = client.newCall(request).execute();
                                        String res = response.body().string();
                                        changedField = false;
                                        if (res.equals("Username sucessfully changed")) {
                                            changedField = true;
                                        } else {
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            /* Wait for thread to finish & inform the user if the change was successful */
                            thread.start();
                            thread.join();
                            if (changedField) {
                                Toast.makeText(getActivity().getApplicationContext(), "Your name has been changed. Relog to see changes.", Toast.LENGTH_LONG).show();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                });

                // Setup the dialog and only allow the positive button to be pressed if text is given //
                final AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);


                nameBox.addTextChangedListener(new TextWatcher() {


                    @Override
                    public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                        // Check if the name box is now filled //
                        if (!nameBox.getText().toString().isEmpty()) {
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                        } else {
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true); // Defensive
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // Check if the name box is not filled //
                        if (nameBox.getText().toString().isEmpty())
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false); // Defensive


                        // Double check the name //
                        if (!nameBox.getText().toString().isEmpty())
                            if (!validateName(nameBox.getText().toString()))
                                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false); // Defensive
                    }
                });
            }
        });
        return view;
    }
}