package com.artavazd.firebaseapp;

import android.content.Intent;
import android.support.annotation.NonNull;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A login screen that offers login via email/password.
 * and Google Sing-in
 */
public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = SignInActivity.class.getName();

    // UI references.
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mSignUpButton;
    private Button mEmailSignInButton;
    private Button mGuestSignInButton;
    private TextView mForgotPasswordText;


    //Firebase variables.
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        setupLoginUI();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    startActivity(new Intent(SignInActivity.this, MainActivity.class)
                            .putExtra(MainActivity.EXTRA_USER_ID,user.getUid()));
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    mEmailField.setText("");
                    mPasswordField.setText("");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    // Set up the login form.
    private void setupLoginUI() {

        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);

        mForgotPasswordText = (TextView) findViewById(R.id.forgotPassword);
        mForgotPasswordText.setOnClickListener(this);

        mSignUpButton = (Button) findViewById(R.id.button_sign_up);
        mSignUpButton.setOnClickListener(this);

        mEmailSignInButton = (Button) findViewById(R.id.button_sign_in);
        mEmailSignInButton.setOnClickListener(this);

        mGuestSignInButton = (Button) findViewById(R.id.button_sign_in_guest);
        mGuestSignInButton.setOnClickListener(this);


    }

    private void signUp(String email, String password) {
        Log.d(TAG, getLocalClassName());
        if (!validateForm()) {
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this, R.string.sign_up_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                        else
                        {   FirebaseUser user=task.getResult().getUser();
                            Toast.makeText(SignInActivity.this, R.string.sign_up_success+
                                    user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError(getString(R.string.error_email_required));
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError(getString(R.string.error_password_required));
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        return result;
    }

    private void signIn(String email, String password) {
        Log.d(TAG, getLocalClassName());
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccessGuest(task.getResult().getUser().getDisplayName());
                        } else {
                            Toast.makeText(SignInActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignInActivity.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.button_sign_in:
                signIn(mEmailField.getText().toString(),
                        mPasswordField.getText().toString());
                break;
            case R.id.button_sign_up:
                signUp(mEmailField.getText().toString(),
                        mPasswordField.getText().toString());
                break;
            case R.id.button_sign_in_guest:
                signInAnonymously();
                break;
            case R.id.forgotPassword:
                break;

        }
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            onAuthSuccessGuest(task.getResult().getUser().getUid());
                        }
                    }
                });
    }

    private void onAuthSuccessGuest(String username) {


    }
}

