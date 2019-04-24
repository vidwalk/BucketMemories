package com.example.bucketnotes.bucketmemories;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;
    private static final String TAG = LoginActivity.class.getSimpleName();

    // UI references.
    private AutoCompleteTextView tvEmailView;
    private EditText etPasswordView;
    private View viewProgressView;
    private View viewLoginFormView;


    //Authentication references
    private FirebaseAuth mFirebaseAuthentication;
    private boolean boolSuccessLogin = false;
    GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SignInButton googleButton;

        //Initialise Firebase Authentication
        FirebaseApp.initializeApp(this);
        mFirebaseAuthentication = FirebaseAuth.getInstance();


        //google button
        googleButton = findViewById(R.id.button_google_sign_in);
        googleButton.setSize(SignInButton.SIZE_STANDARD);

        //calles google emails select pop up
        findViewById(R.id.button_google_sign_in).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_google_sign_in:
                        signIn();
                        break;
                    // ...
                }
            }
        });

        // Set up the login form.
        //Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.id_token))
                .build();
        //client
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        tvEmailView = findViewById(R.id.text_email_login);


        etPasswordView = findViewById(R.id.edit_password_login);
        etPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        Button mEmailSignInButton = findViewById(R.id.button_email_sign_in);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        viewLoginFormView = findViewById(R.id.scrollview_login_form);
        viewProgressView = findViewById(R.id.progressbar_login_progress);
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.

        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
         FirebaseUser currentUser = mFirebaseAuthentication.getCurrentUser();
        //Firebase check is user is signed in and update of UI
        if (currentUser != null ) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void signIn() {
        //gets The Google Sign In intent to select email
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, getString(R.string.error_sign_in_google)+ e.getStatusCode());
                updateUI(null);
                // ...
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        tvEmailView.setError(null);
        etPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = tvEmailView.getText().toString();
        String password = etPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            etPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = etPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            tvEmailView.setError(getString(R.string.error_field_required));
            focusView = tvEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            tvEmailView.setError(getString(R.string.error_invalid_email));
            focusView = tvEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {

        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {

        return password.length() > 4;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // DONE attempt authentication against a network service.

            mFirebaseAuthentication.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Sign in is is successful,Update Main UI Thread
                        Log.d(TAG, getString(R.string.success_email));
                        FirebaseUser userFb = mFirebaseAuthentication.getCurrentUser();
                        updateUI(userFb);


                    } else {
                        //Login Failure
                        Log.w(TAG, getString(R.string.error_email), task.getException());
                        Toast.makeText(LoginActivity.this, getString(R.string.error_authentication), Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
            });


            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;


            if (success && boolSuccessLogin) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                etPasswordView.setError(getString(R.string.error_incorrect_password));
                etPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

        }
    }

    //update UI methods
    //Firebase Instance
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            boolSuccessLogin = true;
        }
    }


private void firebaseAuthGoogle(GoogleSignInAccount user){
    Log.d(TAG, getString(R.string.success_firebase)+user.getId());

    AuthCredential googleCredentials = GoogleAuthProvider.getCredential(user.getIdToken(),null);
    mFirebaseAuthentication.signInWithCredential(googleCredentials)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                    Log.d(TAG, getString(R.string.success_google_sign_in));
                    FirebaseUser userFb = mFirebaseAuthentication.getCurrentUser();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    updateUI(userFb);

                    }
                    else {
                        Log.w(TAG, getString(R.string.error_google_sign_in), task.getException());

                        updateUI(null);
                    }
                }
            });
}


    public void goToSignUp(View view) {
        //If User Has No Account Redirect them to Register
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

}

