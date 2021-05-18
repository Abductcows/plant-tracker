package gr.auth.androidproject.plants.domain;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Collection of methods that calculate values for a plant
 */
public class PlantUtils {

    /**
     * Calculates the time until the plant needs to be watered again
     *
     * @param plant the plant
     * @return time to the next watering
     */
    public static Duration timeToNextWatering(Plant plant) {
        // get time elapsed since last watering
        Duration timeSinceLast = Duration.between(LocalDateTime.now(), plant.getLastWatered());

        // and subtract the watering interval from it
        return timeSinceLast.minus(plant.getWateringInterval());
    }
}
