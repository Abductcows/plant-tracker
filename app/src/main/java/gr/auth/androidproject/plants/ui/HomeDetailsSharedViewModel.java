package gr.auth.androidproject.plants.ui;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import gr.auth.androidproject.plants.domain.Plant;
import gr.auth.androidproject.plants.domain.PlantDBHandler;

public class HomeDetailsSharedViewModel extends ViewModel {
    MutableLiveData<List<Plant>> plants;

    public LiveData<List<Plant>> getPlants(Context context) {
        if (plants == null) {
            plants = new MutableLiveData<>();
        }
        loadPlants(context);
        return plants;
    }

    private void loadPlants(Context context) {
        PlantDBHandler plantDBHandler = new PlantDBHandler(context);
        plants.setValue(plantDBHandler.getAllPlants());
    }

    public void deletePlant(int position, Context context) {
        long id = plants.getValue().get(position).getId();
        PlantDBHandler plantDBHandler = new PlantDBHandler(context);
        plantDBHandler.removePlant(id);
    }
}
