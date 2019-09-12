package com.example.rohit.loginapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();
    public static final String GOOGLE_ACCOUNT = "google_account_details";

    private ImageView googleAccountProfilePicImageView;
    private TextView googleAccountHolderNameTextView, googleAccountUsernameTextView;
    private Button googleAccountSignOutButton,googleAccountRevokeAccessButton;

    private GoogleSignInClient googleSignInClient;
    private GoogleSignInOptions googleSignInOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initView();
        initObject();
        setGoogleAccountDetail();

        googleAccountSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        googleAccountRevokeAccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revokeAccess();
            }
        });
    }

    private void initView()
    {
        googleAccountProfilePicImageView = findViewById(R.id.google_account_profile_image);
        googleAccountHolderNameTextView = findViewById(R.id.google_account_full_name);
        googleAccountUsernameTextView = findViewById(R.id.google_account_username);
        googleAccountSignOutButton=findViewById(R.id.sign_out_from_google_account);
        googleAccountSignOutButton=findViewById(R.id.sign_out_from_google_account);
        googleAccountRevokeAccessButton=findViewById(R.id.revoke_access_from_google_account);
    }

    private void initObject()
    {
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    private void setGoogleAccountDetail()
    {
        GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

        if (googleSignInAccount != null)
        {
            String personName = googleSignInAccount.getDisplayName();
            System.out.println(personName);

            String personGivenName = googleSignInAccount.getGivenName();
            System.out.println(personGivenName);

            String personFamilyName = googleSignInAccount.getFamilyName();
            System.out.println(personFamilyName);

            String personEmail = googleSignInAccount.getEmail();
            System.out.println(personEmail);

            String personId = googleSignInAccount.getId();
            System.out.println(personId);

            Uri personPhoto = googleSignInAccount.getPhotoUrl();
            System.out.println(personPhoto);

            /*
            Retrieving an ID Token for the user.
             */
            String idToken = googleSignInAccount.getIdToken();
            System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"+idToken);

            if(personPhoto!=null)
            {
                System.out.println("IIIIIIIIIIIIIIIIIIIIII"+personPhoto);
                Picasso.with(this).load(personPhoto).error(R.drawable.placeholder).into(googleAccountProfilePicImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess");
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(getApplicationContext(), "Image Loading Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            googleAccountHolderNameTextView.setText(googleSignInAccount.getDisplayName());
            googleAccountUsernameTextView.setText(googleSignInAccount.getEmail());
        }
        else
        {
            Intent intent=new Intent(ProfileActivity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    /**
     * method to do google sign out
     * This code clears which account is connected to the app. To sign in again, the user must choose their account again.
     */
    private void signOut()
    {
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        /*
                        After Sign-Out Success we navigate the user back to LoginActivity
                         */
                        Intent intent=new Intent(ProfileActivity.this,LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
    }

    /**
     * DISCONNECT ACCOUNTS
     * method to revoke access from this app
     * call this method after successful sign out
     * <p>
     * It is highly recommended that you provide users that signed in with Google the ability to disconnect their Google account from your app. If the user deletes their account, you must delete the information that your app obtained from the Google APIs
     */
    private void revokeAccess()
    {
        googleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        Intent intent=new Intent(ProfileActivity.this,LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
    }
}
