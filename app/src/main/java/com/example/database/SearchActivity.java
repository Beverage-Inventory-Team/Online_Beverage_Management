package com.example.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    public static final int EDIT_NOTE_REQUEST=2;

    MainAdapter adapter;
    private Button btnsearch;
    private EditText etSearch;
    private Spinner spinner;
    private String choice;

    private String resultemail;

    private FirebaseAuth Fauth;

/*    final FirebaseUser users = Fauth.getCurrentUser();
    String finaluser=users.getEmail();
    final String resultemail = finaluser.replace(".","");*/

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        Fauth= FirebaseAuth.getInstance();


        final FirebaseUser users = Fauth.getCurrentUser();
        String finaluser=users.getEmail();
        resultemail = finaluser.replace(".","");

        notebookRef = db.collection(resultemail);

        etSearch = findViewById(R.id.search);
        btnsearch = findViewById(R.id.btnSearch);

        spinner = (Spinner) findViewById(R.id.spinner);
    // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.choice_array, android.R.layout.simple_spinner_item);
    // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the spinner
        spinner.setAdapter(adapter1);


        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchword = etSearch.getText().toString();

                if (searchword.isEmpty()) {
                    etSearch.requestFocus();
                    etSearch.setError("Please enter the keyword!");
                    return;
                }

                setUpRecyclerView();
            }

        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                choice = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinner.requestFocus();
            }
        });

    }

    public void setUpRecyclerView(){

        String searchword = etSearch.getText().toString();
        int vol=0;

        if (choice == "storage")
        {
            choice="text";
        }

        if (choice == "volume")
        {
            try {
                vol= Integer.parseInt(searchword);
            }
            catch (Exception e)
            {
                etSearch.requestFocus();
                etSearch.setError("Please enter valid volume");
                return;
            }
        }

        Query query;

        if (choice == "volume")
        {
            query= notebookRef.whereEqualTo(choice, vol);
        }
        else
        {
            query= notebookRef.whereEqualTo(choice, searchword);
        }

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
                Intent intent = new Intent(SearchActivity.this,AddUpdate.class);
                intent.putExtra(AddUpdate.EXTRA_ID,id);
                intent.putExtra(AddUpdate.EXTRA_TEXT,mainData.getText());
                intent.putExtra(AddUpdate.EXTRA_NAME,mainData.getName());
                intent.putExtra(AddUpdate.EXTRA_STYLE,mainData.getStyle());
                intent.putExtra(AddUpdate.EXTRA_VOLUME,mainData.getVolume());
                intent.putExtra(AddUpdate.EXTRA_BREWED,mainData.getBrewed());
                intent.putExtra(AddUpdate.EXTRA_BREWERY,mainData.getBrewery());
                intent.putExtra(AddUpdate.EXTRA_BEST,mainData.getBest());
                intent.putExtra(AddUpdate.EXTRA_EXPDATE,String.valueOf(mainData.getExpdate()));
                //intent.putExtra(AddUpdate.EXTRA_ARRAY,mainData.getImage());
                startActivityForResult(intent,EDIT_NOTE_REQUEST);

            }
        });




    }

/*    public void setUpRecyclerView(){

        Toast.makeText(this, "Here we gooooooo", Toast.LENGTH_SHORT).show();

        Toast.makeText(this, etSearch.getText().toString(), Toast.LENGTH_SHORT).show();

           String searchword = etSearch.getText().toString();


           Query query = notebookRef.whereEqualTo(choice, searchword+"\uf8ff"); //startAt(etSearch.getText().toString()).endAt(etSearch.getText().toString()+"\uf8ff");

        //Query query =null;



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



                Intent intent = new Intent(SearchActivity.this,AddUpdate.class);
                intent.putExtra(AddUpdate.EXTRA_ID,id);
                intent.putExtra(AddUpdate.EXTRA_TEXT,mainData.getText());
                intent.putExtra(AddUpdate.EXTRA_NAME,mainData.getName());
                intent.putExtra(AddUpdate.EXTRA_STYLE,mainData.getStyle());
                intent.putExtra(AddUpdate.EXTRA_VOLUME,mainData.getVolume());
                intent.putExtra(AddUpdate.EXTRA_BREWED,mainData.getBrewed());
                intent.putExtra(AddUpdate.EXTRA_BEST,mainData.getBest());
                intent.putExtra(AddUpdate.EXTRA_EXPDATE,String.valueOf(mainData.getExpdate()));
                //intent.putExtra(AddUpdate.EXTRA_ARRAY,mainData.getImage());
                startActivityForResult(intent,EDIT_NOTE_REQUEST);

            }
        });

    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK)
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

    private AlertDialog AskOption(final RecyclerView.ViewHolder viewHolder)
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Are you sure you want to Delete this item?")

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {


                        adapter.deleteItem(viewHolder.getAdapterPosition());

                        Toast.makeText(SearchActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //condition = false;
                        Toast.makeText(SearchActivity.this, "Item not deleted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        setUpRecyclerView();
                    }
                })
                .create();

        return myQuittingDialogBox;
    }
}