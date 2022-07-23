package com.secure.dglocker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
//    private FirebaseAuth firebaseAuth;
//    private TextView tvPhone;
//    private ProgressBar progressBar;
//    private AppCompatButton logOut;
//
//    //    google signin vala
//    private TextView email,tvName;

//    TextView textView;
    RelativeLayout cards,documents;
    private DrawerLayout drawer;

//    AppCompatButton logout;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        firebaseAuth = FirebaseAuth.getInstance();

//        textView = findViewById(R.id.textview);

//        tvPhone = findViewById(R.id.tvPhone);
//        tvName = findViewById(R.id.tvName);
//        logOut = findViewById(R.id.logOut);
//        progressBar = findViewById(R.id.progressBar);
//
//        firebaseAuth = FirebaseAuth.getInstance();
//
//        //        google signin vala
//        email = findViewById(R.id.email);
//        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
//        if (signInAccount != null){
//            email.setText(signInAccount.getEmail());
////            mTvEmail.setText(signInAccount.getEmail());
//        }
//
//
//        logOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                progressBar.setVisibility(View.VISIBLE);
//                logOut.setVisibility(View.INVISIBLE);
//                firebaseAuth.signOut();
//                startActivity(new Intent(MainActivity.this,SendOTPActivity.class));
//                finish();
//            }
//        });



        cards = findViewById(R.id.cards);
        cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ShowCardsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        documents = findViewById(R.id.documents);
        documents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ShowActivity.class);
                startActivity(intent);
                finish();
            }
        });


//        logout = findViewById(R.id.logout);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                firebaseAuth.signOut();
//                startActivity(new Intent(MainActivity.this,SendOTPActivity.class));
//                finish();
//            }
//        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_share:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
                    String shareMessage= "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://drive.google.com/drive/folders/15j5ZVuRf931ULmGhqSmNlwSYNRlAS6Oi?usp=sharing" +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
                break;
            case R.id.nav_disclaimer:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This app is made for documents save like cards, images and other. In this application everything is free of cost and with the help of application no need to carry hard copy of document. It's saved in our database and easily access when user is needed.")
                        .setCancelable(false)
                        .setTitle("Disclaimer")
                        .setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.nav_privacy_policy:
                Intent pp = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cardmanagement.blogspot.com/2022/04/privacy-policy.html"));
                startActivity(pp);
                break;

            case R.id.nav_help:
                Intent intent = new Intent(Intent.ACTION_SENDTO)
                        .setData(Uri.parse("mailto:"+"card.managment.helpdesk@gmail.com"));

//check if the target app is available or not

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;

            case R.id.nav_logOut:

                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this,SendOTPActivity.class));
                finish();
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser!=null){
//            Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();
            Log.d("myTag", "Welcome");
//            textView.setText(firebaseUser.getUid());
//            email.setText(firebaseUser.getEmail());
//            tvName.setText(firebaseUser.getDisplayName());
        }else {
            startActivity(new Intent(this,SendOTPActivity.class));
            finish();
        }

    }
}