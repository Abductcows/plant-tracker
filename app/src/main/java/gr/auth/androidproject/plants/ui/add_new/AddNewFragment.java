package gr.auth.androidproject.plants.ui.add_new;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;

import gr.auth.androidproject.plants.R;
import gr.auth.androidproject.plants.domain.Plant;
import gr.auth.androidproject.plants.domain.PlantDBHandler;

public class AddNewFragment extends Fragment {

    private AddNewViewModel addNewViewModel;
    private EditText nameInput;
    private EditText birthdayInput;
    private EditText wateringIntervalDays, wateringIntervalHours, wateringIntervalMinutes;
    private Button createPlantButton;

    private LocalDateTime birthday = null;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addNewViewModel =
                new ViewModelProvider(this).get(AddNewViewModel.class);


        View root = saveViews(inflater, container);
        createBirthdayInputListener();
        createSavePlantButtonListener();

        return root;
    }

    /**
     * Sets values to the static view objects
     * <p>
     * Extracted from onCreate
     */
    @NotNull
    private View saveViews(@NonNull LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_add_new, container, false);
        nameInput = root.findViewById(R.id.editTextPlantName);
        birthdayInput = root.findViewById(R.id.editTextPlantBirthday);
        wateringIntervalDays = root.findViewById(R.id.editTextWateringIntervalDays);
        wateringIntervalHours = root.findViewById(R.id.editTextWateringIntervalHours);
        wateringIntervalMinutes = root.findViewById(R.id.editTextWateringIntervalMinutes);
        createPlantButton = root.findViewById(R.id.addPlantButton);
        return root;
    }

    /**
     * Special treatment for the DatePicker used in the birthday selection
     */
    private void createBirthdayInputListener() {
        final Calendar cal = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = (view, year, month, dayOfMonth) -> {
            // update label
            birthday = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0);

            birthdayInput.setText(
                    birthday.format(dateFormatter));
        };

        birthdayInput.setOnClickListener(v -> new DatePickerDialog(AddNewFragment.this.getActivity(),
                date, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show());
    }

    /**
     * <p>
     * Creates the save plant button listener.<br>
     * <p>
     * <p>
     * The listener converts all fields into a {@link Plant} object and issues an insert order to
     * the database through the {@link PlantDBHandler} object
     * </p>
     */
    private void createSavePlantButtonListener() {

        createPlantButton.setOnClickListener(v -> {
            // check for name (required)
            if (nameInput.getText().toString().isEmpty()) {
                Toast.makeText(AddNewFragment.this.getActivity(), "Plant name is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            String name = nameInput.getText().toString();

            Duration wateringInterval;
            // check that some watering interval has been provided
            {
                String dayString = wateringIntervalDays.getText().toString();
                String hourString = wateringIntervalHours.getText().toString();
                String minuteString = wateringIntervalMinutes.getText().toString();
                if (dayString.isEmpty()
                        && hourString.isEmpty()
                        && minuteString.isEmpty()) {
                    Toast.makeText(AddNewFragment.this.getActivity(), "No watering interval provided", Toast.LENGTH_SHORT).show();
                    return;
                }
                int durationInMinutes = 0;
                try {
                    if (!dayString.isEmpty()) {
                        durationInMinutes += 1440 * Integer.parseUnsignedInt(dayString);
                    }
                    if (!hourString.isEmpty()) {
                        durationInMinutes += 60 * Integer.parseUnsignedInt(hourString);
                    }
                    if (!minuteString.isEmpty()) {
                        durationInMinutes += Integer.parseUnsignedInt(minuteString);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(AddNewFragment.this.getActivity(), "Invalid watering interval format", Toast.LENGTH_SHORT).show();
                }
                wateringInterval = Duration.ofMinutes(durationInMinutes);
            }

            // TODO maybe add option to set initial last watered time
            // but it may be too cluttered then
            LocalDateTime lastWatered = LocalDateTime.now();

            byte[] photo = null; // TODO add photo processing

            // create the plant object
            Plant theNewPlant = new Plant(name, birthday, lastWatered, wateringInterval, photo);

            // insert into the db
            PlantDBHandler handler = new PlantDBHandler(AddNewFragment.this.getActivity());
            handler.addPlant(theNewPlant);
        });
    }
}