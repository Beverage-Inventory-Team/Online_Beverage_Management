package com.example.database;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class AddUpdate extends AppCompatActivity {

    public static final String EXTRA_ID = "com.example.database.EXTRA_ID";
    public static final String EXTRA_TEXT = "com.example.database.EXTRA_TEXT";
    public static final String EXTRA_NAME = "com.example.database.EXTRA_NAME";
    public static final String EXTRA_STYLE = "com.example.database.EXTRA_STYLE";
    public static final String EXTRA_VOLUME = "com.example.database.EXTRA_VOLUME";
    public static final String EXTRA_BREWERY = "com.example.database.EXTRA_BREWERYING";
    public static final String EXTRA_BREWED = "com.example.database.EXTRA_BREWED";
    public static final String EXTRA_BEST = "com.example.database.EXTRA_BEST";
    public static final String EXTRA_EXPDATE = "com.example.database.EXTRA_ARRAY";


    private String currentPhotoPath;
    private FirebaseAuth Fauth;

    String resultemail;



    private EditText editText,editName,eStyle,eVolume,eBrewed,eExpdate,eBrewery;

    private Button btnCapture;
    private ImageView imgCapture;
    Bitmap bp;
    private FirebaseStorage mStorageRef = FirebaseStorage.getInstance();
    private ProgressBar progressBar;
    private static final int Image_Capture_Code = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update);
        progressBar=findViewById(R.id.pbar);

        Fauth= FirebaseAuth.getInstance();


        final FirebaseUser users = Fauth.getCurrentUser();
        String finaluser=users.getEmail();
        resultemail = finaluser.replace(".","");


        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Adding the close button on the menu
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);


        editText = findViewById(R.id.edit_text);
        editName = findViewById(R.id.edit_name);
        eStyle = findViewById(R.id.etStyle);
        eVolume = findViewById(R.id.etVolume);
        eBrewed = findViewById(R.id.etBrewed);
        eExpdate = findViewById(R.id.etExpDate);
        eBrewery = findViewById(R.id.etBrewery);

        btnCapture =(Button)findViewById(R.id.btnTakePicture);
        imgCapture = (ImageView) findViewById(R.id.capturedImage);
        bp=null;

        TextWatcher tw = new TextWatcher() {

            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int j = 2; j <= cl && j < 6; j += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon-1);
                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s-%s-%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    eExpdate.setText(current);
                    eExpdate.setSelection(sel < current.length() ? sel : current.length());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        };

        eExpdate.addTextChangedListener(tw);

        TextWatcher twe = new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {



                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int j = 2; j <= cl && j < 6; j += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon-1);
                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s-%s-%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    eBrewed.setText(current);
                    eBrewed.setSelection(sel < current.length() ? sel : current.length());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        eBrewed.addTextChangedListener(twe);

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fileName = "photo";
                File storageDirectory=getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                try {
                    File imageFile = File.createTempFile(fileName,".jpg",storageDirectory);
                    currentPhotoPath = imageFile.getAbsolutePath();

                    Uri imageUri = FileProvider.getUriForFile(AddUpdate.this,"com.example.database.fileprovider",imageFile);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    startActivityForResult(intent,1);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });




        Intent intent = getIntent();

        //Set Name for the menu and update
        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Edit Item");
            editText.setText(intent.getStringExtra(EXTRA_TEXT));
            editName.setText(intent.getStringExtra(EXTRA_NAME));
            eStyle.setText(intent.getStringExtra(EXTRA_STYLE));
            eVolume.setText(""+intent.getIntExtra(EXTRA_VOLUME,0));
            eBrewed.setText(intent.getStringExtra(EXTRA_BREWED));
            eBrewery.setText(intent.getStringExtra(EXTRA_BREWERY));
            eExpdate.setText(intent.getStringExtra(EXTRA_EXPDATE));
            Picasso.get().load(intent.getStringExtra(EXTRA_BEST)).into(imgCapture);
            currentPhotoPath=intent.getStringExtra(EXTRA_BEST);



        } else {
            setTitle("Add Item"); }

    }

    private void saveNote()
    {
        String text=editText.getText().toString();
        String name=editName.getText().toString();
        String style=eStyle.getText().toString();
        String Tvolume=eVolume.getText().toString();
        int volume=Integer.parseInt(Tvolume);
        String brewed=eBrewed.getText().toString();
        String brewery=eBrewery.getText().toString();
        String expdate=eExpdate.getText().toString();
        //byte [] image = DataConverter.convertImageToByteArray(bp);


        if (brewery.trim().isEmpty()||text.trim().isEmpty()||name.trim().isEmpty()||style.trim().isEmpty()||Tvolume.trim().isEmpty()||brewed.trim().isEmpty()||expdate.trim().isEmpty()){
            Toast.makeText(this,"Please fill out all the information",Toast.LENGTH_SHORT).show();
            return;
        }

        try{

            int day = Integer.parseInt(expdate.substring(0, 2));
            int month = Integer.parseInt(expdate.substring(3, 5));
            int year = Integer.parseInt(expdate.substring(6,10));
        }
        catch (NumberFormatException e){
            eExpdate.setError("Invalid Date");
            eExpdate.requestFocus();
            return;
        }

        try{

            int day = Integer.parseInt(brewed.substring(0, 2));
            int month = Integer.parseInt(brewed.substring(3, 5));
            int year = Integer.parseInt(brewed.substring(6,10));
        }
        catch (NumberFormatException e){
            eBrewed.setError("Invalid Date");
            eBrewed.requestFocus();
            return;
        }

        Toast.makeText(this,brewery,Toast.LENGTH_SHORT).show();



        Intent data = new Intent();
        data.putExtra(EXTRA_TEXT,text);
        data.putExtra(EXTRA_NAME,name);
        data.putExtra(EXTRA_STYLE,style);
        data.putExtra(EXTRA_VOLUME,volume);
        data.putExtra(EXTRA_BREWED,brewed);
        data.putExtra(EXTRA_BREWERY,brewery);
        data.putExtra(EXTRA_EXPDATE,expdate);
        data.putExtra(EXTRA_BEST,currentPhotoPath);
        //data.putExtra(EXTRA_ARRAY,image);


        String id =getIntent().getStringExtra(EXTRA_ID);


        if (id!=null) {
            data.putExtra(EXTRA_ID,id);
        }

        setResult(RESULT_OK,data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_item,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_note:
                saveNote();
                return true;
            case android.R.id.home:
                finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                //bp = (Bitmap) data.getExtras().get("data");
                bp = BitmapFactory.decodeFile(currentPhotoPath);
                imgCapture.setImageBitmap(bp);
                uploadImage();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void uploadImage()
    {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bp.compress(Bitmap.CompressFormat.PNG,100,outputStream);
        byte[] info= outputStream.toByteArray();

        String path = resultemail + "/" + UUID.randomUUID() + ".png";

        final StorageReference fileRef = mStorageRef.getReference(path);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("caption","Bis")
                .build();

        UploadTask uploadTask = fileRef.putBytes(info, metadata);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setEnabled(false);

        uploadTask.addOnCompleteListener(AddUpdate.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Log.i("MA", "Upload Task Completed");
                Toast.makeText(AddUpdate.this, "Upload Task Completed" , Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                progressBar.setEnabled(true);

            }
        });

        Task<Uri> getDownloadUri = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return fileRef.getDownloadUrl();
            }
        });

        getDownloadUri.addOnCompleteListener(AddUpdate.this, new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Toast.makeText(AddUpdate.this, "dasdasdasdasd" + downloadUri, Toast.LENGTH_SHORT).show();
                    currentPhotoPath = downloadUri.toString();
                }
                progressBar.setVisibility(View.GONE);
                progressBar.setEnabled(true);
            };
        });



    }


}