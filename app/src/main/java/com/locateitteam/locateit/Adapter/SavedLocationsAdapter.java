package com.locateitteam.locateit.Adapter;

import static com.locateitteam.locateit.Activity.MapsActivity.destinationLatLong;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.locateitteam.locateit.Activity.DirectionActivity;
import com.locateitteam.locateit.Activity.LoginActivity;
import com.locateitteam.locateit.Activity.SignupActivity;
import com.locateitteam.locateit.Model.DirectionPlaceModel.DirectionStepModel;
import com.locateitteam.locateit.R;
import com.locateitteam.locateit.SavedPlaceModel;
import com.locateitteam.locateit.databinding.CardviewSavedLocationsBinding;
import com.locateitteam.locateit.databinding.StepItemLayoutBinding;

import java.util.List;

public class SavedLocationsAdapter extends RecyclerView.Adapter<SavedLocationsAdapter.ViewHolder> {

private final LayoutInflater layoutInflater;
private List<SavedPlaceModel> items;
private Context contextdb;

public SavedLocationsAdapter(Context context, List<SavedPlaceModel> Items){
        this.items = Items;
        this.layoutInflater = LayoutInflater.from(context);
        this.contextdb = context;
        }


public void setItemFilteredList(List<SavedPlaceModel> filteredList){
        this.items = filteredList;
        notifyDataSetChanged();
}

@NonNull
@Override
public SavedLocationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.cardview_saved_locations,parent,false);
        return new SavedLocationsAdapter.ViewHolder(v);
        }

@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    SavedPlaceModel item = items.get(position);
        holder.recentName.setText(item.getName());
        holder.address.setText(item.getAddress());

    holder.btnDirect.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            LatLng l = new LatLng(item.getLat(),item.getLng());
            destinationLatLong = l;
            Intent intent = new Intent(contextdb, DirectionActivity.class);
            contextdb.startActivity(intent);
        }
    });
   }

@Override
public int getItemCount() {
        return items.size();
        }

public  class ViewHolder extends RecyclerView.ViewHolder{

    TextView recentName,address;
    ImageView btnDirect;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        recentName = itemView.findViewById(R.id.txtPlaceName);
        address = itemView.findViewById(R.id.txtPlaceAddress);
        btnDirect = itemView.findViewById(R.id.GoToSavedLocation);

    }

}
}