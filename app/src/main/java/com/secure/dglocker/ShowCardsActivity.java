package com.secure.dglocker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowCardsActivity extends AppCompatActivity {

    //Add Click
    ImageView add;

    private RecyclerView recyclerView;
    private ArrayList<Model> list;

    private MyAdapter adapter;
    private FirebaseAuth firebaseAuth;

    private DatabaseReference root;

    RelativeLayout notfoundandtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cards);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        notfoundandtxt = findViewById(R.id.notfoundandtxt);

        root = FirebaseDatabase.getInstance().getReference(firebaseUser.getUid()+"/Cards");

        //Add Clicking
        add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowCardsActivity.this,AddCardsActivity.class);
                startActivity(intent);
                finish();
            }
        });
//        ----------------------------------------

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new MyAdapter(this , list);
        recyclerView.setAdapter(adapter);

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Model model = dataSnapshot.getValue(Model.class);
                    list.add(model);
                    notfoundandtxt.setVisibility(View.INVISIBLE);


                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(), "Error occurred!", Toast.LENGTH_SHORT).show();

            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ShowCardsActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}