package gr.auth.androidproject.plants.domain;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

import gr.auth.androidproject.plants.R;

/**
 * <p>
 * Read only wrapper for a plant that formats its content to be human readable <br>
 * </p>
 * <p>
 * It formats all the fields related to a plant and also calculates the time to next watering
 * </p>
 */
public class PlantFormatter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.SHORT); // 5/14/21, 5:59 PM
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofLocalizedDate(FormatStyle.SHORT); // 5/14/21

    /**
     * The {@link Plant} object being wrapped
     */
    private final Plant plant;


    public PlantFormatter(Plant plant) {
        this.plant = plant;
    }

    public String timeToNextWatering() {
        return formattedDuration(
                Duration.between(LocalDateTime.now(), plant.getLastWatered()));
    }

    public long id() {
        return plant.getId();
    }

    public String name() {
        return plant.getName();
    }

    public Optional<String> birthday() {
        if (plant.getBirthday().isPresent()) {
            return Optional.of(
                    formattedDateTime(plant.getBirthday().get())
            );
        }
        return Optional.empty();
    }

    public String lastWatered() {
        return formattedDateTime(plant.getLastWatered());
    }

    public String wateringInterval() {
        return formattedDuration(plant.getWateringInterval());
    }

    public Optional<Bitmap> photo() {
        if (plant.getPhoto().isPresent()) {
            byte[] blob = plant.getPhoto().get();
            return Optional.of(
                    BitmapFactory.decodeByteArray(blob, 0, blob.length)
            );
        }
        return Optional.empty();
    }

    private String formattedDateTime(LocalDateTime dateTime) {
        return dateTime.toLocalDate().format(DATE_TIME_FORMATTER);
    }

    private String formattedDate(LocalDateTime date) {
        return date.format(DATE_FORMATTER);
    }

    /**
     * Formats the Duration object to a app specific standard
     *
     * @param duration the duration to be formatted
     * @return string formatted representation of the duration
     * @implSpec This implementation formats the frequency as "D days H hours M minutes", omitting
     * any zeros
     */
    private String formattedDuration(Duration duration) {
        StringBuilder builder = new StringBuilder();

        long days = duration.toDays();
        boolean addLeadingSpace = false;

        if (days != 0) {
            builder.append(days);
            builder.append(' ');
            builder.append(R.string.duration_formatter_days);

            duration = duration.minusDays(days);
            addLeadingSpace = true;
        }

        long hours = duration.toHours();
        if (hours != 0) {
            if (addLeadingSpace) {
                builder.append(' ');
            }
            builder.append(hours);
            builder.append(' ');
            builder.append(R.string.duration_formatter_hours);

            duration = duration.minusHours(hours);
            addLeadingSpace = true;
        }

        long minutes = duration.toMinutes();
        if (minutes != 0) {
            if (addLeadingSpace) {
                builder.append(' ');
            }
            builder.append(minutes);
            builder.append(' ');
            builder.append(R.string.duration_formatter_minutes);
        }

        return builder.toString();
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ID = ");
        builder.append(id());

        builder.append(", name = ");
        builder.append(name());


        builder.append(", birthday = ");
        Optional<String> birthday = birthday();
        if (birthday.isPresent()) {
            builder.append(birthday);
        } else {
            builder.append("null");
        }

        builder.append(", last_watered = ");
        builder.append(lastWatered());

        builder.append(", watering_interval = ");
        builder.append(wateringInterval());


        builder.append(", has_photo = ");
        builder.append(photo().isPresent());

        return builder.toString();
    }
}
