package com.example.database;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_NOTE_REQUEST=1;
    public static final int EDIT_NOTE_REQUEST=2;


    private FirebaseAuth Fauth;
    private String resultemail;

    private String sortchoice="volume";



    int MY_PERMISSIONS_REQUEST_CAMERA=0;


   // private FirebaseStorage mStorageRef = FirebaseStorage.getInstance();

    FirebaseDatabase Fdatabase;
    //DatabaseReference myRef;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef;
    private MainAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fauth= FirebaseAuth.getInstance();
        Fdatabase = FirebaseDatabase.getInstance();

        final FirebaseUser users = Fauth.getCurrentUser();
        String finaluser=users.getEmail();
        resultemail = finaluser.replace(".","");

        notebookRef = db.collection(resultemail);

       // myRef = Fdatabase.getReference("users");


        //Adding the Plus button that looks like it's floating
        FloatingActionButton buttonAddNote = findViewById(R.id.button_add_note);
        FloatingActionButton buttonSearch = findViewById(R.id.button_search_p);
        FloatingActionButton buttonBarcode = findViewById(R.id.button_barcode);

        //Starting the activity after the button is clicked
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }

                    Intent intent = new Intent(MainActivity.this, AddUpdate.class);
                    startActivityForResult(intent,ADD_NOTE_REQUEST);
              }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        buttonBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ScanActivity.class));
            }
        });


        setUpRecyclerView();
    }


    private void Logout()
    {
        Fauth.signOut();
        finish();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.delete_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
/*            case R.id.delete_all:
                Toast.makeText(this, "All items deleted", Toast.LENGTH_SHORT).show();
                setUpRecyclerView();
                return true;*/
            case R.id.logout_menu:
                Logout();
                return true;
            case R.id.SortName:
                sortchoice="name";
                setUpRecyclerView();
                return true;
            case R.id.SortVolume:
                sortchoice="volume";
                setUpRecyclerView();
                return true;
            case R.id.SortStyle:
                sortchoice="style";
                setUpRecyclerView();
                return true;
            case R.id.SortBrewedOn:
                sortchoice="brewed";
                setUpRecyclerView();
                return true;
            case R.id.SortStorage:
                sortchoice="text";
                setUpRecyclerView();
                return true;
            case R.id.SortDate:
                sortchoice="expdate";
                setUpRecyclerView();
                return true;
           case R.id.SortBrewery:
                sortchoice="brewery";
                setUpRecyclerView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void setUpRecyclerView(){

        Query query = notebookRef.orderBy(sortchoice,Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<MainData> options = new FirestoreRecyclerOptions.Builder<MainData>()
                .setQuery(query,MainData.class)
                .setLifecycleOwner(this)
                .build();


        adapter = new MainAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        //Adding Swipe to delete functionality
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                AlertDialog diaBox = AskOption(viewHolder);
                diaBox.show();
            }

        }).attachToRecyclerView(recyclerView);

        //Update when the item is clicked
        adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                MainData mainData = documentSnapshot.toObject(MainData.class);

                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();

                Intent intent = new Intent(MainActivity.this,AddUpdate.class);
                intent.putExtra(AddUpdate.EXTRA_ID,id);
                intent.putExtra(AddUpdate.EXTRA_TEXT,mainData.getText());
                intent.putExtra(AddUpdate.EXTRA_NAME,mainData.getName());
                intent.putExtra(AddUpdate.EXTRA_STYLE,mainData.getStyle());
                intent.putExtra(AddUpdate.EXTRA_VOLUME,mainData.getVolume());
                intent.putExtra(AddUpdate.EXTRA_BREWED,mainData.getBrewed());
                intent.putExtra(AddUpdate.EXTRA_BREWERY,mainData.getBrewery());
                intent.putExtra(AddUpdate.EXTRA_BEST,mainData.getBest());
                intent.putExtra(AddUpdate.EXTRA_EXPDATE,String.valueOf(mainData.getExpdate()));

                startActivityForResult(intent,EDIT_NOTE_REQUEST);
                /*Toast.makeText(MainActivity.this,
                        "Position: " + position + " ID: " + mainData.getBest(), Toast.LENGTH_SHORT).show();*/

            }
        });
    }


    private AlertDialog AskOption(final RecyclerView.ViewHolder viewHolder)
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Are you sure you want to Delete this item?")

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        adapter.deleteItem(viewHolder.getAdapterPosition());

                        Toast.makeText(MainActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //condition = false;
                        Toast.makeText(MainActivity.this, "Item not deleted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        setUpRecyclerView();
                    }
                })
                .create();

        return myQuittingDialogBox;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
            String sText = data.getStringExtra(AddUpdate.EXTRA_TEXT);
            String sName = data.getStringExtra(AddUpdate.EXTRA_NAME);
            String sStyle = data.getStringExtra(AddUpdate.EXTRA_STYLE);
            //String sVolume = data.getStringExtra(AddUpdate.EXTRA_VOLUME);
            int sVolume = data.getIntExtra(AddUpdate.EXTRA_VOLUME,0);
            String sBrewed = data.getStringExtra(AddUpdate.EXTRA_BREWED);
            String sBest = data.getStringExtra(AddUpdate.EXTRA_BEST);
            String sExpdate = data.getStringExtra(AddUpdate.EXTRA_EXPDATE);
            String sBrewery = data.getStringExtra(AddUpdate.EXTRA_BREWERY);
            //Initialize main data
            MainData mainData = new MainData(sBest,sBrewed,sBrewery,sExpdate,sName,sStyle,sText,sVolume);

            CollectionReference notebookRef = FirebaseFirestore.getInstance()
                    .collection(resultemail);
            notebookRef.add(mainData)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(MainActivity.this, "Item Saved", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Item Not Updated", Toast.LENGTH_SHORT).show();
                }
            })
            ;



        }
        else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK)
        {
            String id = data.getStringExtra(AddUpdate.EXTRA_ID);

            if (id==null){
                Toast.makeText(this, "Item Can't Be Updated", Toast.LENGTH_SHORT).show();
                return;
            }

            String sText = data.getStringExtra(AddUpdate.EXTRA_TEXT);
            String sName = data.getStringExtra(AddUpdate.EXTRA_NAME);
            String sStyle = data.getStringExtra(AddUpdate.EXTRA_STYLE);
            //String sVolume = data.getStringExtra(AddUpdate.EXTRA_VOLUME);
            int sVolume = data.getIntExtra(AddUpdate.EXTRA_VOLUME,0);
            String sBrewed = data.getStringExtra(AddUpdate.EXTRA_BREWED);
            String sBest = data.getStringExtra(AddUpdate.EXTRA_BEST);
            String sExpdate = data.getStringExtra(AddUpdate.EXTRA_EXPDATE);
            String sBrewery = data.getStringExtra(AddUpdate.EXTRA_BREWERY);


            //Initialize main data
            MainData mainData = new MainData(sBest,sBrewed,sBrewery,sExpdate,sName,sStyle,sText,sVolume);
            // mainData.setID(id);

            DocumentReference notebookRef = FirebaseFirestore.getInstance()
                    .collection(resultemail).document(id);

            notebookRef.update("text",sText,
                    "name",sName,
                    "style",sStyle,
                    "volume",sVolume,
                    "brewed",sBrewed,
                    "brewery",sBrewery,
                    "best",sBest,
                    "expdate",sExpdate);

            Toast.makeText(this, "Item Updated", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Item Not Updated", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}