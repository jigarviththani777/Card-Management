package com.secure.dglocker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.secure.dglocker.R;
import com.secure.dglocker.VerifyOTPActivity;

import java.net.PasswordAuthentication;
import java.security.AuthProvider;
import java.util.concurrent.TimeUnit;

public class SendOTPActivity extends AppCompatActivity {

    //        google connection signup

    private RelativeLayout google_Verify;
    private GoogleSignInClient mGoogleSignInClient;
    private  final static int RC_SIGN_IN = 123;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otpactivity);


        final EditText inputMobile = findViewById(R.id.inputMobile);
        final EditText inputPassword = findViewById(R.id.inputPassword);

        Button buttongetOTP = findViewById(R.id.buttonGetOTP);

        final ProgressBar progressBar1 = findViewById(R.id.progressBar1);



        buttongetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                if (inputMobile.getText().toString().trim().isEmpty() && inputPassword.getText().toString().trim().isEmpty()) {
//                    Toast.makeText(SendOTPActivity.this, "Enter Mobile & Password", Toast.LENGTH_SHORT).show();
//                }
//                else
                    if (inputMobile.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SendOTPActivity.this, "Enter Mobile", Toast.LENGTH_SHORT).show();
                }
//                else if (inputPassword.getText().toString().trim().isEmpty()) {
//                    Toast.makeText(SendOTPActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
//                }
                else if (inputMobile.length() < 10) { //inputPassword.length() < 8 &&
                    Toast.makeText(SendOTPActivity.this, "Enter valid Please enter valid mobile number & Enter password minimum 8 digits & characters ", Toast.LENGTH_SHORT).show();
                }
//                else if (inputPassword.length() < 8) {
//                    Toast.makeText(SendOTPActivity.this, "Please enter password minimum 8 digits & characters", Toast.LENGTH_SHORT).show();
//                }

//                else if (inputPassword.length() >= 8){
//                    progressBar.setVisibility(View.VISIBLE);
//                    buttongetOTP.setVisibility(View.INVISIBLE);
//
//                }

                else if (inputMobile.length() == 10) {
                    progressBar1.setVisibility(View.VISIBLE);
                    buttongetOTP.setVisibility(View.INVISIBLE);

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+91" + inputMobile.getText().toString(),
                            60,
                            TimeUnit.SECONDS,
                            SendOTPActivity.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    progressBar1.setVisibility(View.GONE);
                                    buttongetOTP.setVisibility(View.VISIBLE);
                                    startActivity(new Intent(SendOTPActivity.this, MainActivity.class));
                                    finish();

                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    progressBar1.setVisibility(View.GONE);
                                    buttongetOTP.setVisibility(View.VISIBLE);
                                    Toast.makeText(SendOTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    progressBar1.setVisibility(View.GONE);
                                    buttongetOTP.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(getApplicationContext(), VerifyOTPActivity.class);
                                    intent.putExtra("mobile", inputMobile.getText().toString());
                                    intent.putExtra("verificationId", verificationId);
                                    startActivity(intent);

                                }
                            }
                    );
                } else {
                    Toast.makeText(SendOTPActivity.this, "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // google signup
        firebaseAuth = FirebaseAuth.getInstance();
        createRequest();

        findViewById(R.id.google_Sign_Up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    // google sign up

    private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void signIn() {
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
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            startActivity(new Intent(SendOTPActivity.this,MainActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SendOTPActivity.this, "Sorry Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}