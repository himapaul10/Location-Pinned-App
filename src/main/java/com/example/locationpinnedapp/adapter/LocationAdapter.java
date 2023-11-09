package com.example.locationpinnedapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locationpinnedapp.AddLocationActivity;
import com.example.locationpinnedapp.R;
import com.example.locationpinnedapp.database.DatabaseHelper;
import com.example.locationpinnedapp.model.LocationModel;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    List<LocationModel> list;
    Context context;
    DatabaseHelper databaseHelper;

    public LocationAdapter(List<LocationModel> list, Context context, DatabaseHelper databaseHelper) {
        this.list = list;
        this.context = context;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Getting the current item from the list
        LocationModel model = list.get(position);

        // Setting the title and description
        holder.tvAddress.setText(model.getAddress());
        holder.tvLatLong.setText("Latitude: "+model.getLatitude()+", Longitude: "+model.getLongitude());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddLocationActivity.class);
                intent.putExtra("data", model);
                context.startActivity(intent);
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseHelper.deleteLocation(model.getId());
                list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,list.size());
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAddress, tvLatLong;
        RelativeLayout rlMain;
        ImageView ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Finding the views in the item layout
            rlMain = itemView.findViewById(R.id.rlMain);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            tvLatLong = itemView.findViewById(R.id.tvLatLong);
        }
    }
}
