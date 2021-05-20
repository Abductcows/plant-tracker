package gr.auth.androidproject.plants.domain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import gr.auth.androidproject.plants.R;

/**
 * <p>
 * Read only wrapper for a plant that formats its content to be human readable <br>
 * </p>
 * <p>
 * It formats all the fields related to a plant and also calculates the time to next watering
 * </p>
 * <p>
 * TODO maybe the formatter class should not return optionals but handle
 *  the absence of the values internally and return a fitting result. Ideas:
 *  - empty string or (not specified) for birthday
 *  - default cached Bitmap avatar for no photo
 *  number 2 is non-trivial and will need to be handled later anyway
 */
public class PlantFormatter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.SHORT); // 5/14/21, 5:59 PM
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofLocalizedDate(FormatStyle.SHORT); // 5/14/21

    private final Context context;
    /**
     * The {@link Plant} object being wrapped
     */
    private final Plant plant;

    public PlantFormatter(Plant plant) {
        this(null, plant);
    }

    public PlantFormatter(Context context, Plant plant) {
        this.context = context;
        this.plant = plant;
    }

    /**
     * Checks if time elapsed between now and last watering is greater than the watering interval
     * and returns the appropriate message
     *
     * @return string message informing the user of when to water the plant
     */
    public String timeToNextWatering() {
        Duration timeToNext = PlantUtils.timeToNextWatering(plant);

        if (timeToNext.isNegative() || timeToNext.isZero()) {
            if (Objects.nonNull(context)) {
                return context.getResources().getString(R.string.formatter_water_now_message);
            } else {
                return "0";
            }
        }

        return formattedDuration(timeToNext);
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
        return Stream.of(plant.getPhoto())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(blob -> BitmapFactory.decodeByteArray(blob, 0, blob.length))
                .findAny();
    }

    private String formattedDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
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

    /**
     * Helper class that declares plant bitmap encoding format/quality
     */
    public static class BitmapEncoding {
        public static final Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
        public static final int quality = 100;
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
