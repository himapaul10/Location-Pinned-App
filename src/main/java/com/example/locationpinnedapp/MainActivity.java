package com.example.locationpinnedapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.locationpinnedapp.adapter.LocationAdapter;
import com.example.locationpinnedapp.database.DatabaseHelper;
import com.example.locationpinnedapp.model.LocationModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    ProgressDialog progressDialog;
    FloatingActionButton addNewLocation;
    RecyclerView rvLocation;
    SearchView searchView;
    LocationAdapter locationAdapter;
    List<LocationModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        addNewLocation = findViewById(R.id.addNewLocation);
        rvLocation = findViewById(R.id.rvLocation);
        searchView = findViewById(R.id.searchView);

        addNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddLocationActivity.class));
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Reading the text file, Please wait");
        progressDialog.setCancelable(false);
        databaseHelper = new DatabaseHelper(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.isEmpty()) {
                    // Display all locations if the search query is empty
                    setAdapter();
                } else {
                    // Perform search when user submits the query
                    searchByAddress(query.toLowerCase());
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // Display all locations if the search query is empty
                    setAdapter();
                } else {
                    // Perform search as the user types
                    searchByAddress(newText.toLowerCase());
                }
                return false;
            }
        });
    }

    // Function to read coordinates from a text file
    private List<LatLng> readCoordinatesFromAssets() {
        progressDialog.show();
        List<LatLng> coordinates = new ArrayList<>();
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("coordinates");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    double latitude = Double.parseDouble(parts[0]);
                    double longitude = Double.parseDouble(parts[1]);
                    coordinates.add(new LatLng(latitude, longitude));
                }
            }
            inputStream.close();
        } catch (IOException e) {
            progressDialog.dismiss();
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        return coordinates;
    }

    // Function to get addresses using Geocoder
    private List<String> getAddresses(List<LatLng> coordinates) {
        List<String> addresses = new ArrayList<>();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        for (LatLng coordinate : coordinates) {
            try {
                List<Address> addressList = geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1);
                if (addressList != null && !addressList.isEmpty()) {
                    Address address = addressList.get(0);
                    addresses.add(address.getAddressLine(0));
                    LocationModel locationModel = new LocationModel(address.getAddressLine(0), String.valueOf(coordinate.latitude), String.valueOf(coordinate.longitude));
                    databaseHelper.addLocation(locationModel);
                } else {
                    addresses.add("Address not found");
                }
            } catch (IOException e) {
                e.printStackTrace();
                addresses.add("Error fetching address");
            }
        }
        progressDialog.dismiss();
        return addresses;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        list.addAll(databaseHelper.getAllLocations());
        if (!(list.size() > 0)) {
            List<LatLng> coordinates = readCoordinatesFromAssets();
            getAddresses(coordinates);
            databaseHelper.getAllLocations();
        }
        // Refreshing the adapter when the activity is resumed
        setAdapter();
    }

    // Method to set up the adapter with all locations
    @SuppressLint("NotifyDataSetChanged")
    private void setAdapter() {
        list.clear();
        list.addAll(databaseHelper.getAllLocations());
        if (list.size() > 0) {
            rvLocation.setLayoutManager(new LinearLayoutManager(this));
            locationAdapter = new LocationAdapter(list, this, databaseHelper);
            rvLocation.setAdapter(locationAdapter);
            locationAdapter.notifyDataSetChanged();
        }
    }

    // Method to search location by address
    @SuppressLint("NotifyDataSetChanged")
    private void searchByAddress(String address) {
        list.clear();
        list.addAll(databaseHelper.getLocationByAddress(address));
        if (list.size() > 0) {
            rvLocation.setLayoutManager(new LinearLayoutManager(this));
            locationAdapter = new LocationAdapter(list, MainActivity.this, databaseHelper);
            rvLocation.setAdapter(locationAdapter);
            locationAdapter.notifyDataSetChanged();
        }
    }
}
