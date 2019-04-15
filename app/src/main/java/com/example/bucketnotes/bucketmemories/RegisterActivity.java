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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;


    private UserLoginTask mAuthTask = null;
    private static final String TAG = RegisterActivity.class.getSimpleName();

    // UI references.
    private AutoCompleteTextView tvEmailView;
    private EditText etPasswordView;
    private View viewProgressView;
    private View viewLoginFormView;

    //Authentication
    private FirebaseAuth mFirebaseAuthentication;
    private boolean successLogin = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //get FireBase Instance
        mFirebaseAuthentication = FirebaseAuth.getInstance();


        // Set up the login form.
        tvEmailView =  findViewById(R.id.text_email_sign_up);

        etPasswordView =  findViewById(R.id.edit_password_sign_up);
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

        Button mEmailSignInButton =  findViewById(R.id.button_email_sign_in);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        viewLoginFormView = findViewById(R.id.scrollview_login_form);
        viewProgressView = findViewById(R.id.progressbar_login_progress);
    }


    public void SignIn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
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
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

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
            // DONE: attempt authentication against a network service.

            mFirebaseAuthentication.createUserWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        //When User Is Created Successfully
                        Log.d(TAG, getString(R.string.success_user));
                        FirebaseUser user = mFirebaseAuthentication.getCurrentUser();
                        updateUI(user);
                    }else{
                        //if sign in fails,disply a message to user
                        Log.w(TAG, getString(R.string.error_user),task.getException() );
                        Toast.makeText(getApplicationContext(),getString(R.string.error_authentication),Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
            });

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            successLogin = success;
            if (successLogin) {
                //On Success New Intent Is Opened
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
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

    private void updateUI(FirebaseUser currentUser){
        //Gets user instance and updates main thread
        if (currentUser != null){
            successLogin = true;

        }
    }



}

