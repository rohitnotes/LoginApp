package com.example.rohit.loginapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import android.support.annotation.NonNull;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient googleApiClient;

    /**
     * Play Service Request Code.
     */
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final int RC_SIGN_IN = 9001;
    private SignInButton googleAccountSignInButton;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInOptions googleSignInOptions;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initObject();
        signIn();
    }

    private void initView()
    {
        googleAccountSignInButton = findViewById(R.id.sign_in_using_google_account);
        googleAccountSignInButton.setSize(SignInButton.SIZE_STANDARD);
        googleAccountSignInButton.setColorScheme(SignInButton.COLOR_LIGHT);
    }

    private void initObject()
    {
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        Log.w(TAG, "Build Client");
        buildGoogleApiClient();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        connectGoogleApiClient();
        OptionalPendingResult<GoogleSignInResult> optionalPendingResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient);

        if (optionalPendingResult.isDone())
        {
            /*
             * If the user's cached credentials are valid, the OptionalPendingResult will be "done"
             * and the GoogleSignInResult will be available instantly. We can try and retrieve an
             * authentication code.
             */
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult googleSignInResult = optionalPendingResult.get();

            if (googleSignInResult.isSuccess())
            {
                GoogleSignInAccount alreadyLoggedAccount = googleSignInResult.getSignInAccount();
                if (alreadyLoggedAccount != null)
                {
                    Toast.makeText(this, "Already Logged In", Toast.LENGTH_SHORT).show();
                    onLoggedIn(alreadyLoggedAccount);
                }
                else
                {
                    Log.d(TAG, "Not logged in");
                }
            }
        }
        else
        {
            /*
             * If the user has not previously signed in on this device or the sign-in has expired,
             * this asynchronous branch will attempt to sign in the user silently.  Cross-device
             * single sign-on will occur in this branch.
             */
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Checking sign in state...");
            progressDialog.show();
            optionalPendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>()
            {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult)
                {
                    progressDialog.dismiss();
                    if (googleSignInResult.isSuccess())
                    {
                        GoogleSignInAccount alreadyLoggedAccount = googleSignInResult.getSignInAccount();
                        if (alreadyLoggedAccount != null)
                        {
                            Log.d(TAG, "Already Logged In");
                            onLoggedIn(alreadyLoggedAccount);
                        }
                        else
                        {
                            Log.d(TAG, "Not logged in");
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.w(TAG, "Inside onResume()");
        Log.i(TAG, ""+googleApiClient.isConnected());
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.w(TAG, "Inside onPause()");
    }

    @Override
    protected void onStop()
    {
        Log.w(TAG, "Inside onStop()");
        if (googleApiClient != null)
        {
            if (googleApiClient.isConnected())
            {
                googleApiClient.disconnect();
            }
        }
        super.onStop();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        if (!googleApiClient.isConnected())
        {
            connectGoogleApiClient();
        }
        Log.w(TAG, "Inside onRestart()");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.w(TAG, "Inside onDestroy()");
    }

    private void onLoggedIn(GoogleSignInAccount googleSignInAccount)
    {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.GOOGLE_ACCOUNT, googleSignInAccount);
        startActivity(intent);
        finish();
    }

    private void signIn()
    {
        googleAccountSignInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //showProgressDialog();
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            /*
            Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
             */
            if (requestCode == RC_SIGN_IN)
            {

                //hideProgressDialog();
                /*
                Resolve the intent into a GoogleSignInResult we can process.
                 */
                GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                if (googleSignInResult.isSuccess())
                {
                    GoogleSignInAccount alreadyLoggedAccount = googleSignInResult.getSignInAccount();
                    onLoggedIn(alreadyLoggedAccount);
                }

            }
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient()
    {
        Log.w(TAG, "Inside buildGoogleApiClient()");
        googleApiClient = new GoogleApiClient.Builder(this /* Context */)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }

    private void connectGoogleApiClient()
    {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (googleApiAvailability.isUserResolvableError(resultCode))
            {
                googleApiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                Log.d("PLAY SERVICES ERROR : ", "Google Play services is unrecoverable.");
            }
            else
            {
                Log.d("PLAY SERVICES ERROR : ", "This device is not supported.");
                finish();
            }
        }
        else
        {
            Log.d("SERVICES AVAILABLE : ", ""+googleApiAvailability.getErrorString(resultCode));
            if (googleApiClient != null)
            {
                googleApiClient.connect();
            }else
            {
                Log.w(TAG, "googleApiClient is null");
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        if (googleApiClient.isConnected())
        {
            Log.w(TAG, "Inside onConnected Method, Connected to GoogleApiClient");
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.i(TAG, "Location services suspended. Please reconnect.");
        connectGoogleApiClient();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.i(TAG, "GoogleApiClient Connection Failed!");
        if (connectionResult.hasResolution())
        {
            try
            {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, 90000);
            }
            catch (IntentSender.SendIntentException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture)
    {

    }

    private void showProgressDialog()
    {
        if (progressDialog == null)
        {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading");
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    private void hideProgressDialog()
    {
        if (progressDialog != null && progressDialog.isShowing())
        {
            progressDialog.hide();
        }
    }
}
