package gr.auth.androidproject.plants.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Stores and calculates time span information related to a specific plant.
 *
 * <p><br>
 * Information stored:<br>
 *         <ul>
 *             <li>Birthday (Planting or acquisition date)</li>
 *             <li>Day and time of last watering</li>
 *         </ul>
 * </p>
 *
 * <p>
 * <b>Date strings</b> are in the "<b>YYYY-MM-DD</b>" format<br>
 * <b>Time strings</b> are in the 24 hour "<b>HH</b>:<b>MM</b>" format<br><br>
 * Non-getter methods calculate Time/Date differences e.g. time to next watering
 * </p>
 */
public class PlantCalendar {

    private static final PlantCalendarBuilder builder = new PlantCalendarBuilder();

    /**
     * Birthday of the plant
     */
    private LocalDate birthday;

    /**
     * Exact date and time of last watering
     */
    private LocalDateTime lastWatered;

    /**
     * Returns the builder object
     *
     * @return builder for this class
     */
    public static PlantCalendarBuilder builder() {
        builder.reset();
        return builder;
    }

    /**
     * Empty constructor: sets all dates to now
     */
    private PlantCalendar() {
        birthday = LocalDate.now();
        lastWatered = LocalDateTime.now();
    }

    /**
     * Calculates the time elapsed since the last watering
     *
     * @return duration since last watering
     */
    public Duration timeSinceLastWatering() {
        return Duration.between(LocalDateTime.now(), lastWatered);
    }

    // TODO maybe calculate time till next birthday

    /**
     * Builder object for the class due to different parameter types in constructor
     */
    public static final class PlantCalendarBuilder {
        private PlantCalendar result;

        private PlantCalendarBuilder() {
            this.reset();
        }

        private void reset() {
            result = new PlantCalendar();
        }

        public PlantCalendarBuilder birthday(String birthdayString) {
            result.birthday = LocalDate.parse(birthdayString);
            return this;
        }

        public PlantCalendarBuilder birthday(LocalDate birthday) {
            result.birthday = LocalDate.from(birthday);
            return this;
        }

        public PlantCalendarBuilder lastWatered(LocalDateTime lastDateTime) {
            result.lastWatered = LocalDateTime.from(lastDateTime);
            return this;
        }

        public PlantCalendarBuilder lastWatered(String lastWateredDay, String lastWateredHour) {
            result.lastWatered = LocalDateTime.of(LocalDate.parse(lastWateredDay), LocalTime.parse(lastWateredHour));
            return this;
        }

        public PlantCalendar build() {
            return result;
        }
    }

    /*
        Getters / Setters

        Note that LocalDate and LocalDateTime objects are immutable
     */

    public LocalDate getBirthday() {
        return this.birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public LocalDateTime getLastWatered() {
        return this.lastWatered;
    }

    public final void setLastWatered(String dayWatered, String hourWatered) {
        lastWatered = LocalDateTime.of(LocalDate.parse(dayWatered), LocalTime.parse(hourWatered));
    }

    public void setLastWatered(LocalDateTime lastWatered) {
        this.lastWatered = lastWatered;
    }
}
