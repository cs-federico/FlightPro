package com.example.flightpro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.flightpro.DB.AppDataBase;
import com.example.flightpro.DB.FlightDAO;
import com.example.flightpro.databinding.ActivityAddFlightBinding;

import java.util.List;

/**
 * Abstract: This activity allows the admin to add a flight to the database.
 *
 * @author: Federico Marquez Murrieta
 * @since: 2023-11-29
 */

public class AddFlightActivity extends AppCompatActivity {

    private static final String ADD_FLIGHT_ACTIVITY_USER = "com.example.project2.AddFlightActivityUser"; // String that stores the username

    TextView mBannerTextView; // TextView that displays the banner
    EditText mFlightNumberEditText; // EditText that allows the user to enter the flight number
    EditText mFlightOriginEditText; // EditText that allows the user to enter the flight origin
    EditText mFlightDestinationEditText; // EditText that allows the user to enter the flight destination
    TextView mMessageDisplayTextView; // TextView that displays the error message

    Button mAddButtonForFlight; // Button that allows the user to add a flight
    Button mBackButtonForFlight; // Button that allows the user to return to the previous activity

    FlightDAO mFlightDaoForAdd; // Database access object that allows the user to access the database
    ActivityAddFlightBinding mActivityBindingForFlight; // Binding that allows the user to access the views in the layout

    /**
     * Abstract: This method is called when the activity is created
     *
     * @param: savedInstanceState
     * @return: void
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) { // This method is called when the activity is created
        super.onCreate(savedInstanceState); // Calls the super class onCreate method
        mActivityBindingForFlight = ActivityAddFlightBinding.inflate(getLayoutInflater()); // Initializes the binding
        setContentView(mActivityBindingForFlight.getRoot()); // Sets the content view to the activity_add_flight layout
        initializeUI(); // Initializes the UI
        setupDatabase(); // Sets up the database
        setupListeners(); // Sets the listeners
    }

    /**
     * Abstract: This method initializes the UI
     *
     * @param: none
     * @return: void
     */

    private void initializeUI() {
        mBannerTextView = mActivityBindingForFlight.addFlightBanner; // Initializes the banner
        mFlightNumberEditText = mActivityBindingForFlight.addFlightEnterFlightNumber; // Initializes the flight number EditText
        mFlightOriginEditText = mActivityBindingForFlight.addFlightEnterFlightOrigin; // Initializes the flight origin EditText
        mFlightDestinationEditText = mActivityBindingForFlight.addFlightEnterFlightDestination; // Initializes the flight destination EditText
        mMessageDisplayTextView = mActivityBindingForFlight.addFlightMessageDisplay; // Initializes the message display TextView
        mAddButtonForFlight = mActivityBindingForFlight.addFlightAddButton; // Initializes the add button
        mBackButtonForFlight = mActivityBindingForFlight.addFlightBackButton;   // Initializes the back button
    }

    /**
     * Abstract: This method sets up the database
     *
     * @param: none
     * @return: void
     */

    private void setupDatabase() { // This method sets up the database
        mFlightDaoForAdd = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME) // Initializes the database
                .allowMainThreadQueries() // Allows the database to run on the main thread
                .build() // Builds the database
                .FlightDAO(); // Initializes the database access object
    }

    /**
     * Abstract: This method sets the listeners
     *
     * @param: none
     * @return: void
     */

    private void setupListeners() { // This method sets the listeners
        String mActiveUsername = getIntent().getStringExtra(ADD_FLIGHT_ACTIVITY_USER); // Gets the username from the intent
        mAddButtonForFlight.setOnClickListener(view -> attemptToAddFlight(mActiveUsername)); // Sets the listener for the add button
        mBackButtonForFlight.setOnClickListener(view -> navigateToAdminActionActivity(mActiveUsername)); // Sets the listener for the back button
    }

    /**
     * Abstract: This method attempts to add a flight
     *
     * @param: currentUsername
     * @return: void
     */

    private void attemptToAddFlight(String currentUsername) { // This method attempts to add a flight
        if (addFlight()) { // If the flight is added
            navigateToAdminActionActivity(currentUsername); // Navigates to the admin action activity
        }
    }

    /**
     * Abstract: This method navigates to the admin action activity
     *
     * @param: username
     * @return: void
     */

    private void navigateToAdminActionActivity(String username) { // This method navigates to the admin action activity
        Intent intent = AdminMenuActivity.getIntent(getApplicationContext(), username); // Initializes the intent
        startActivity(intent); // Starts the activity
    }

    /**
     * Abstract: This method adds a flight
     *
     * @param: none
     * @return: boolean
     */

    public boolean addFlight() { // This method adds a flight
        if (!validateFlightDetails()) return false; // If the flight details are not valid
        String flightNumber = mFlightNumberEditText.getText().toString(); // Gets the flight number
        String mEnteredOrigin = mFlightOriginEditText.getText().toString(); // Gets the flight origin
        String mEnteredDestination = mFlightDestinationEditText.getText().toString(); // Gets the flight destination
        Flight mFlightToAdd = new Flight(flightNumber, mEnteredOrigin, mEnteredDestination); // Initializes the flight to add
        mFlightDaoForAdd.insert(mFlightToAdd); // Inserts the flight into the database
        return true; // Returns true
    }

    /**
     * Abstract: This method validates the flight details
     *
     * @param: none
     * @return: boolean
     */

    private boolean validateFlightDetails() { // This method validates the flight details
        String mEnteredFlightNumber = mFlightNumberEditText.getText().toString(); // Gets the flight number
        if (mEnteredFlightNumber.isEmpty()) { // If the flight number is empty
            displayErrorMessage(R.string.error_missing_flight_number); // Displays the error message
            return false; // Returns false
        }
        if (isFlightNumberNotUnique(mEnteredFlightNumber)) { // If the flight number is not unique
            displayErrorMessage(R.string.error_duplicate_flight_number); // Displays the error message
            return false; // Returns false
        }
        if (mEnteredFlightNumber.length() > 6) { // If the flight number is too long
            displayErrorMessage(R.string.error_incorrect_flight_number_length); // Displays the error message
            return false; // Returns false
        }
        if (mFlightOriginEditText.getText().toString().isEmpty()) { // If the flight origin is empty
            displayErrorMessage(R.string.error_missing_origin); // Displays the error message
            return false; // Returns false
        }
        if (mFlightDestinationEditText.getText().toString().isEmpty()) { // If the flight destination is empty
            displayErrorMessage(R.string.error_missing_destination); // Displays the error message
            return false; // Returns false
        }
        return true; // Returns true
    }

    /**
     * Abstract: This method checks if the flight number is unique
     *
     * @param: flightNumber
     * @return: boolean
     */

    private boolean isFlightNumberNotUnique(String flightNumber) {
        List<Flight> mExistingFlightsList = mFlightDaoForAdd.getFlights(); // Gets all the flights
        return mExistingFlightsList.stream().anyMatch(flight -> flightNumber.equals(flight.getFlightNumber())); // Returns true if the flight number is not unique
    }

    /**
     * Abstract: This method displays the error message
     *
     * @param: messageId
     * @return: void
     */

    private void displayErrorMessage(int messageId) {
        mMessageDisplayTextView.setText(messageId); // Sets the message display text view to the error message
        mMessageDisplayTextView.setVisibility(View.VISIBLE); // Sets the message display text view to visible
    }

    /**
     * Abstract: This method gets the intent
     *
     * @param: context, username
     * @return: Intent
     */

    public static Intent getIntent(Context context, String username){
        Intent intent = new Intent(context, AddFlightActivity.class); // Initializes the intent
        intent.putExtra(ADD_FLIGHT_ACTIVITY_USER, username); // Puts the username in the intent
        return intent; // Returns the intent
    }
}
