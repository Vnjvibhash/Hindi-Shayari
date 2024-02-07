package in.innovateria.hindishayari.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import in.innovateria.hindishayari.Adapters.AllShayariAdapter;
import in.innovateria.hindishayari.Models.AllShayariModel;
import in.innovateria.hindishayari.R;

public class AllShayariActivity extends AppCompatActivity {

    RecyclerView allShayari_recyclerView;
    AllShayariAdapter allShayariAdapter;
    FirebaseFirestore db;
    List<AllShayariModel> list = new ArrayList<>();
    String id;
    ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_shayari);

        // Check internet and Wi-Fi connectivity
        if (!isConnectedToInternet() && !isConnectedToWifi()) {
            showEnableInternetDialog();
        }

        //Go back to Home Page of Home screen
        MaterialToolbar btnBack  = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllShayariActivity.this, MainActivity.class));
                finish();
            }
        });



        //get data id and name from ShayariCategoryAdapter class
        // set the tile of page based on shayari category

        String name = getIntent().getStringExtra("name");
        id = getIntent().getStringExtra("id");
        btnBack.setTitle(name);


        //set recyclerview properties
        allShayari_recyclerView = findViewById(R.id.recyclerview_all_shayari);
        allShayari_recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //load all shayari from clous firestore of firebase based on the collection id
        showAllShari(id);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showAllShari(id);
            }
        });

    }

    private void showAllShari(String id) {

        progressBar = findViewById(R.id.mainProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        db = FirebaseFirestore.getInstance();

        db.collection("Shayari").document(id).collection("all")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //callled when data is retrived
                        for (DocumentSnapshot document: task.getResult()){
                            AllShayariModel modelList = new AllShayariModel(
                                    document.getId(),
                                    document.getString("data")
                            );
                            list.add(modelList);
                        }
                        //adapter
                        allShayariAdapter = new AllShayariAdapter(AllShayariActivity.this, list);
                        //set adapter recycler view
                        allShayari_recyclerView.setAdapter(allShayariAdapter);
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // called when failed to load data
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(AllShayariActivity.this, "Failed to Load, Check Internet", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showEnableInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please turn on your internet connection.");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Open settings to enable internet
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle cancel button click
                //finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    private boolean isConnectedToWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // Connected to Wi-Fi
                return true;
            }
        }
        return false;
    }
}