package gr.auth.androidproject.plants.ui.home;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import gr.auth.androidproject.plants.R;
import gr.auth.androidproject.plants.domain.Plant;
import gr.auth.androidproject.plants.domain.PlantFormatter;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private final Context context;
    private List<Plant> plants;

    // RecyclerAdapter constructor to pass the context
    public RecyclerAdapter(Context context, List<Plant> p) {
        this.context = context;
        plants = p;
    }

    // Class that holds the items to be displayed (Views in card_layout)
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView plantName;
        private final TextView age;
        private final TextView nextWatering;
        private final ImageView plantImage;
        private int position;

        public ViewHolder(View itemView) {

            super(itemView);
            plantName = itemView.findViewById(R.id.item_title);
            plantImage = itemView.findViewById(R.id.item_image);
            age = itemView.findViewById(R.id.item_age_value);
            nextWatering = itemView.findViewById(R.id.item_watering_value);

            // what to do when an item is clicked
            itemView.setOnClickListener(v -> {
                position = getAbsoluteAdapterPosition();
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                Navigation.findNavController(itemView).
                        navigate(R.id.action_nav_home_to_detailsFragment, bundle);
//                Snackbar.make(v, "Click detected on item " + position,
//                        Snackbar.LENGTH_LONG).show();
            });
        }
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        PlantFormatter plant = new PlantFormatter(context, plants.get(position));
        holder.plantName.setText(plant.name());
        holder.plantImage.setImageBitmap(plant.photo());
        holder.age.setText(plant.birthday());
        holder.nextWatering.setText(plant.timeToNextWatering());

//        holder.plantName.setText(titles[position]);
//        holder.plantImage.setImageResource(images[position]);
//        holder.age.setText(ages[position]);
//        holder.nextWatering.setText(details[position]);
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }
}


//    private String[] titles = {"Chapter One", "Chapter Two", "Chapter Three", "Chapter Four", "Chapter Five",
//            "Chapter Six", "Chapter Seven", "Chapter Eight"};
//    private String[] ages = {"1", "2", "3", "4", "5", "6", "7", "8"};
//    private String[] details = {"Item one details", "Item two details", "Item three details", "Item four details",
//            "Item file details", "Item six details", "Item seven details", "Item eight details"};

//    private int[] images = {R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
//            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
//            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
//            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background }