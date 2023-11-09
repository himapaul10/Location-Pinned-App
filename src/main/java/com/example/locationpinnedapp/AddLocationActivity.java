package com.example.locationpinnedapp;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.locationpinnedapp.database.DatabaseHelper;
import com.example.locationpinnedapp.model.LocationModel;

public class AddLocationActivity extends AppCompatActivity {
    EditText etAddress, etLatitude, etLongitude;
    AppCompatButton btnSaveLocation, btnCancel;
    TextView tvLabel;
    DatabaseHelper databaseHelper;
    LocationModel updatedModel;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        etAddress = findViewById(R.id.etAddress);
        etLatitude = findViewById(R.id.etLatitude);
        etLongitude = findViewById(R.id.etLongitude);
        tvLabel = findViewById(R.id.tvLabel);
        btnSaveLocation = findViewById(R.id.btnSaveLocation);
        btnCancel = findViewById(R.id.btnCancel);
        databaseHelper = new DatabaseHelper(this);

        if (getIntent().getExtras() != null){
            updatedModel = (LocationModel) getIntent().getSerializableExtra("data");
            etAddress.setText(updatedModel.getAddress());
            etLatitude.setText(updatedModel.getLatitude());
            etLongitude.setText(updatedModel.getLongitude());
            tvLabel.setText("Update Location");
            btnSaveLocation.setText("Update");
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Getting input values
                String address = etAddress.getText().toString();
                String latitude = etLatitude.getText().toString();
                String longitude = etLongitude.getText().toString();

                // Validating inputs
                if (address.isEmpty()) {
                    Toast.makeText(AddLocationActivity.this, "Please enter address", Toast.LENGTH_SHORT).show();
                } else if (latitude.isEmpty()) {
                    Toast.makeText(AddLocationActivity.this, "Please enter latitude", Toast.LENGTH_SHORT).show();
                }else if (longitude.isEmpty()) {
                    Toast.makeText(AddLocationActivity.this, "Please enter longitude", Toast.LENGTH_SHORT).show();
                } else {

                    if (updatedModel != null){
                        // Creating a new Location object
                        LocationModel locationModel = new LocationModel(updatedModel.getId(), address, latitude, longitude);
                        // Adding the Location to the database
                        databaseHelper.updateLocation(locationModel);
                        // Showing success message
                        Toast.makeText(AddLocationActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();

                    }else{
                        // Creating a new Location object
                        LocationModel locationModel = new LocationModel(address, latitude, longitude);
                        // Adding the Location to the database
                        databaseHelper.addLocation(locationModel);
                        // Showing success message
                        Toast.makeText(AddLocationActivity.this, "Successfully Saved", Toast.LENGTH_SHORT).show();

                    }
                    // Closing the activity
                    finish();
                }
            }
        });

    }
}