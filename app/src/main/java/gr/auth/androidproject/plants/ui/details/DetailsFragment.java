package gr.auth.androidproject.plants.ui.details;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import gr.auth.androidproject.plants.R;
import gr.auth.androidproject.plants.domain.Plant;
import gr.auth.androidproject.plants.domain.PlantFormatter;
import gr.auth.androidproject.plants.ui.HomeDetailsSharedViewModel;

public class DetailsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_details, container, false);
        HomeDetailsSharedViewModel sharedViewModel =
                new ViewModelProvider(requireActivity()).get(HomeDetailsSharedViewModel.class);
        Context context = getContext();

        assert getArguments() != null;
        int p = getArguments().getInt("position");
        Plant current_plant =
                Objects.requireNonNull(sharedViewModel.getPlants(context).getValue()).get(p);
        PlantFormatter plantFormatter = new PlantFormatter(context, current_plant);

        // setting the views to the according values of the selected plant
        TextView name = root.findViewById(R.id.textViewDetails2);
        ImageView plant_image = root.findViewById(R.id.imageViewDetails1);
        TextView next_watering = root.findViewById(R.id.textViewDetails4);
        TextView age = root.findViewById(R.id.textViewDetails6);
        name.setText(plantFormatter.name());
        if (plantFormatter.photo().isPresent())
            plant_image.setImageBitmap(plantFormatter.photo().get());
        next_watering.setText(plantFormatter.timeToNextWatering());
        if (plantFormatter.birthday().isPresent())
            age.setText(plantFormatter.birthday().get());

        // getting the delete button view
        Button delete_button = root.findViewById(R.id.buttonDetails2);
        delete_button.setOnClickListener(v -> sharedViewModel.deletePlant(p, context));
        return root;
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(DetailsViewModel.class);
//        // TODO: Use the ViewModel
//    }

}