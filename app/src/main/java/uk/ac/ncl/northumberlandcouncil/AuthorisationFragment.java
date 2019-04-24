package uk.ac.ncl.northumberlandcouncil;

/* Begin library imports */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * Last modified 17/04/2019 (R Iqbal)
 */
public class AuthorisationFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
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
    private static final String API_URL = "http://18.130.117.241/";
    public static GoogleApiClient mGoogleApiClient;
    private TwitterLoginButton twitterLoginButton;
    private String firstName;
    public static Boolean existingTwitterUser; // existing twitter user
    private String lastName;
    private String email;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);



    /* End Declarations */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        existingTwitterUser = false;
        /* Handle google login */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
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
                System.out.println(result.data.getUserId());
                /* Successful twitter login has occurred
                    (1) Has this user already registered with twitter before? --> fetch data and redirect
                    (2) Create a popup to request additional information from the user
                 */

                /* Check if this user is already registered */

                OkHttpClient client = new OkHttpClient();                       // WEB REQUEST //
                String idToken = Long.toString(result.data.getUserId());        //    SETUP   //

                try {
                    // Setup the body of the request to include name-value pair of idToken //
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("idToken", idToken)
                            .build();


                    // Setup API URL //
                    Request request = new Request.Builder()
                            .url(API_URL + "validUser.php")
                            .post(requestBody)
                            .build();


                    // Execute network activity off of the main thread //
                    Thread t1 = new Thread(new Runnable() {
                        public void run() {
                            try {
                                Response response = client.newCall(request).execute();
                                String res = response.body().string();

                                /* If the user is an existing user do not request more information! */
                                if (res.contains("true")) {
                                    existingTwitterUser = true;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    /* Start the thread and wait for it to finish before moving on */
                    t1.start();
                    t1.join();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println(" TWITTER STATUS " + existingTwitterUser);
                if (existingTwitterUser) {
                    updateUI();
                    ((MainActivity) getActivity()).onLoginResult(result); // update ui in activity
                }

                if (!existingTwitterUser) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    Context context = getContext();
                    LinearLayout layout = new LinearLayout(context);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    dialog.setTitle("More information required");


                    // Add a TextView here for the "Title" label, as noted in the comments
                    final EditText nameBox = new EditText(context);
                    nameBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    nameBox.setHint("Full Name");
                    layout.addView(nameBox); // Notice this is an add method


                    // Add another TextView here for the "Description" label
                    final EditText emailBox = new EditText(context);
                    emailBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    emailBox.setHint("Email Address");
                    layout.addView(emailBox); // Another add method


                    dialog.setView(layout); // Again this is a set method, not add

                    // Set up the buttons
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            firstName = nameBox.getText().toString();
                            lastName = nameBox.getText().toString();
                            email = emailBox.getText().toString();


                            // Handle invalid arguments that may have got past our checks. //
                            if (firstName.length() == 0 || lastName.length() == 0 || email.length() == 0) {
                                throw new IllegalArgumentException("Name or email not given.");
                            }

                            /* Parse the name now */
                            firstName = firstName.split(" ")[0];
                            lastName = lastName.split(" ")[1];


                            System.out.println(firstName + " " + lastName + " " + email);
                            /* Communicate with the backend API to store user-record on our database */


                            // Pass the user data to the backend server for authentication via HTTPS Post //

                            System.out.println(idToken);
                            try {
                                // Setup the body of the request to include name-value pair of idToken //
                                RequestBody requestBody = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("idToken", idToken)
                                        .addFormDataPart("firstName", firstName)
                                        .addFormDataPart("lastName", lastName)
                                        .addFormDataPart("email", email)
                                        .build();


                                // Setup API URL //
                                Request request = new Request.Builder()
                                        .url(API_URL + "validation.php")
                                        .post(requestBody)
                                        .build();


                                // Execute network activity off of the main thread //
                                Thread thread = new Thread(new Runnable() {
                                    public void run() {
                                        try {
                                            Response response = client.newCall(request).execute();
                                            System.out.println(response.body().string());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                thread.start();
                                thread.join();
                                System.out.println("HERE ALREADY");
                                ((MainActivity) getActivity()).onLoginResult(result); // update ui in activity
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            updateUI();

                        }
                    });

                    // Setup the dialog and only allow the positive button to be pressed if text is given //
                    final AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                    // Enable the button only when the text fields are valid //
                    nameBox.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count,
                                                      int after) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            // Check if either of the two edit texts are not filled //
                            if (nameBox.getText().toString().isEmpty() || emailBox.getText().toString().isEmpty())
                                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false); // Defensive


                            // Double check email address and name //
                            if (!emailBox.getText().toString().isEmpty() && !nameBox.getText().toString().isEmpty())
                                if (!(validateEmail(emailBox.getText().toString()) && validateName(nameBox.getText().toString())))
                                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false); // Defensive
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                            // Check if both of the two edit texts are now filled //
                            if (!nameBox.getText().toString().isEmpty() && !emailBox.getText().toString().isEmpty()) {
                                // Validate email address before enabling the button //
                                if (validateEmail(emailBox.getText().toString()) && validateName(nameBox.getText().toString()))
                                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                            } else {
                                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true); // Defensive
                            }
                        }
                    });


                    // Enable the button only when the text fields are valid //
                    emailBox.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count,
                                                      int after) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            // Check if either of the two edit texts are not filled //
                            if (nameBox.getText().toString().isEmpty() || emailBox.getText().toString().isEmpty())
                                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false); // Defensive


                            // Double check email address and name //
                            if (!emailBox.getText().toString().isEmpty() && !nameBox.getText().toString().isEmpty())
                                if (!(validateEmail(emailBox.getText().toString()) && validateName(nameBox.getText().toString())))
                                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false); // Defensive
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                            // Check if both of the two edit texts are now filled //
                            if (!nameBox.getText().toString().isEmpty() && !emailBox.getText().toString().isEmpty()) {
                                // Validate email address before enabling the button //
                                if (validateEmail(emailBox.getText().toString()) && validateName(nameBox.getText().toString()))
                                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                            } else {
                                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true); // Defensive
                            }
                        }
                    });
                }
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
            System.out.println(result.getSignInAccount().getId());
            handleSignInResult(result);
        }

        // Pass the activity result to the twitter login button
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }


    private void handleSignInResult(GoogleSignInResult result) {
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
                        .url(API_URL + "validation.php")
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
     * Returns true or false when given an email string that is valid (minor counter examples exist for performance i.e. @ 127.0.0.1)
     *
     * @param emailStr - email string to be checked
     * @return boolean - true or false depending on email validity
     */
    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }


    /**
     * Returns true or false when given a name string, expects a first and last name separated by a space
     * consisting of at least 3 characters in each element
     *
     * @param name - name string to be checked
     * @return boolean - true or false depending on email validity
     */
    public static boolean validateName(String name) {
        return name.matches("([a-zA-Z\\-]+){3,}\\s+([a-zA-Z\\-]+){3,}");
    }

    /**
     * Update the user interface upon a successful login by instancing the home fragment, and removing the current from stack
     */
    private void updateUI() {
        ((MainActivity) getActivity()).restoreActionBar();
        getFragmentManager().beginTransaction()
                .replace(((ViewGroup) getView().getParent()).getId(), new HomeFragment())
                .addToBackStack(null)
                .commit();
    }


}
