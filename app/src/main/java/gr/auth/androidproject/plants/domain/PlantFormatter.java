package gr.auth.androidproject.plants.domain;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
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
     * Formats the Duration object to an app specific standard
     *
     * @param duration the duration to be formatted
     * @return string formatted representation of the duration
     * @implSpec This implementation formats the duration as ".. years .. months .. days .. hours .. minutes", omitting
     * any zeros
     */
    private String formattedDuration(Duration duration, TimespanUnits minUnit) {
        Resources strRes = context.getResources();
        StringBuilder builder = new StringBuilder();

        // sort the time durations in descending order (YEAR, MONTH etc)
        final List<TimespanUnits> timeUnits = Arrays.stream(TimespanUnits.values())
                .filter(unit -> unit.minutesInThis >= minUnit.minutesInThis) // discard lower than min
                .sorted(TimespanUnits.descendingOrder) // sort descending
                .collect(Collectors.toCollection(ArrayList::new));

        // get their display string resources
        final Map<TimespanUnits, Integer> unitToStringRes = new HashMap<>();
        unitToStringRes.put(TimespanUnits.YEARS, R.string.duration_formatter_years);
        unitToStringRes.put(TimespanUnits.MONTHS, R.string.duration_formatter_months);
        unitToStringRes.put(TimespanUnits.DAYS, R.string.duration_formatter_days);
        unitToStringRes.put(TimespanUnits.HOURS, R.string.duration_formatter_hours);
        unitToStringRes.put(TimespanUnits.MINUTES, R.string.duration_formatter_minutes);

        boolean addLeadingSpace = false; // to separate
        Duration durationLeft = Duration.from(duration);

        for (TimespanUnits currentTimespanUnit : timeUnits) {
            long durationLeftMinutes = durationLeft.toMinutes();
            long currentUnitLeft = currentTimespanUnit.fromMinutes(durationLeftMinutes);
            if (currentUnitLeft == 0) continue; // nothing to add for this unit, continue

            // add the current time unit number and caption
            if (addLeadingSpace) builder.append(' ');
            builder.append(currentUnitLeft);
            builder.append(' ');
            builder.append(strRes.getString(
                    Objects.requireNonNull(unitToStringRes.get(currentTimespanUnit))
            ));

            // update the duration left to format
            durationLeft = durationLeft.minusMinutes(
                    currentTimespanUnit.toMinutes(currentUnitLeft));

            if (durationLeft.toMinutes() > 0) {
                // more to come, will need a space in next
                addLeadingSpace = true;
            }
        }

        return builder.toString();
    }

    /**
     * Version of {@link #formattedDuration(Duration, TimespanUnits)} with no lower bound
     */
    private String formattedDuration(Duration duration) {
        return formattedDuration(duration, TimespanUnits.MINUTES);
    }

    /**
     * Handles representation and conversion of time units
     * <p>
     * Contains YEARS and MONTHS that are not included in {@link Duration} but not ms, ns etc
     */
    private enum TimespanUnits {

        YEARS(525_600), MONTHS(43_805), // assuming 30.42 day months
        DAYS(1440), HOURS(60), MINUTES(1);
        static final Comparator<TimespanUnits> descendingOrder = Comparator
                .comparingLong(TimespanUnits::getMinutesInThis)
                .reversed();

        /**
         * Number of minutes in one unit of the respective time unit
         */
        private final int minutesInThis;

        TimespanUnits(int numberOfMinutes) {
            minutesInThis = numberOfMinutes;
        }

        long fromMinutes(long minutes) {
            return minutes / minutesInThis;
        }

        /**
         * <p>
         * Converts a value from the scale of {@code this} to minutes.<br>
         * </p>
         * <p>
         * e.g. YEARS.toMinutes(2) will produce the number of minutes in 2 years
         * </p>
         *
         * @param scaledValue value in the scale of this object
         * @return equivalent value in minutes
         */
        long toMinutes(long scaledValue) {
            return scaledValue * minutesInThis;
        }

        int getMinutesInThis() {
            return minutesInThis;
        }
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
