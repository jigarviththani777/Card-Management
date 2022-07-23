package com.secure.dglocker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

public class AddCardsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    //widgets

    private AppCompatButton uploadBtn, showAllBtn,view,datepicker,schedule;
    private ImageView imageView;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    //vars
    private DatabaseReference root,root2;
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private Uri imageUri;
    EditText personname,cardname;

    TextView person_tv,card_tv,date;
//    FirebaseDatabase firebaseDatabase;
//    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cards);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        uploadBtn = findViewById(R.id.upload_btn);
        showAllBtn = findViewById(R.id.showall_btn);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);

        progressBar.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        personname = findViewById(R.id.person);
        cardname = findViewById(R.id.cardname);

//        schedule.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                scheduleNotification();
//            }
//        });


        String person = personname.getText().toString().trim();
//        String card = cardname.getText().toString();


//        person_tv = findViewById(R.id.person_tv);
//        card_tv = findViewById(R.id.card_tv);



        datepicker = findViewById(R.id.datepicker);
        date = findViewById(R.id.date);

        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new com.secure.dglocker.DatePicker();
                datePicker.show(getSupportFragmentManager(),"date picker");
            }
        });
        

//        firebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReference = firebaseDatabase.getReference(firebaseUser.getUid()+"/Cards/person");

        root = FirebaseDatabase.getInstance().getReference(firebaseUser.getUid()+"/Cards");

        showAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(AddCardsActivity.this , ShowCardsActivity.class));
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , 2);
            }
        });


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null){
                    uploadToFirebase(imageUri);

                }else{
                    Toast.makeText(AddCardsActivity.this, "Please Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        view = findViewById(R.id.view);
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(AddCardsActivity.this, person.trim(), Toast.LENGTH_SHORT).show();
//            }
//        });



    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        String curentdate = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        date.setText(curentdate);
//        scheduleNotification(2,"Alert!!!","Your card is expired!");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==2 && resultCode == RESULT_OK && data != null){

            imageUri = data.getData();
            imageView.setImageURI(imageUri);

        }
    }

    private void uploadToFirebase(Uri uri){

        final StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Model model = new Model(uri.toString());
                        String modelId = root.push().getKey();
                        root.child(modelId).setValue(model);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(AddCardsActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        imageView.setImageResource(R.drawable.addimage);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(AddCardsActivity.this, "Uploading...\nPlease wait!!!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(AddCardsActivity.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri mUri){

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddCardsActivity.this,ShowCardsActivity.class);
        startActivity(intent);
        finish();
    }

//    public void scheduleNotification(long time, String title, String text) {
//
//        Intent intent = new Intent(this, NotificationReceiver.class);
//        intent.putExtra("title", title);
//        intent.putExtra("text", text);
//        PendingIntent pending = PendingIntent.getBroadcast(this, 42, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        // Schdedule notification
//        AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending);
//    }
//
//    public static void cancelNotification(Context context, String title, String text) {
//        Intent intent = new Intent(context, NotificationReceiver.class);
//        intent.putExtra("title", title);
//        intent.putExtra("text", text);
//        PendingIntent pending = PendingIntent.getBroadcast(context, 42, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        // Cancel notification
//        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        manager.cancel(pending);
//    }
}