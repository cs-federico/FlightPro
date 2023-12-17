package com.example.flightpro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.flightpro.DB.AppDataBase;
import com.example.flightpro.DB.FlightDAO;
import com.example.flightpro.databinding.ActivityAdminActionBinding;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract: This class is responsible for the Admin Action Activity.
 *
 * @author: Federico Marquez Murrieta
 * @since: 2023-11-29
 */

public class AdminMenuActivity extends AppCompatActivity {

    private static final String ADMIN_ACTION_ACTIVITY_USER = "com.example.flightpro.AdminMenuActivityUser"; // String that stores the username

    TextView mHeaderTextView, mNotificationTextView, mFlightCatalogTitleTextView, mFlightCatalogTextView; // TextViews that display the title, error messages, and flight catalog
    EditText mModifyFlightInput; // EditText that allows the user to enter the flight number
    Button mCreateFlightButton, mUpdateFlightButton, mReturnButton; // Buttons that allow the user to create, update, or return to the previous activity
    FlightDAO mArrivalFlightDAO; // Database access object that allows the user to access the database
    ActivityAdminActionBinding mControlPanelBinding; // Binding that allows the user to access the views in the layout

    /**
     * Abstract: This method is called when the activity is created
     *
     * @param: savedInstanceState
     * @return: void
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Calls the super class onCreate method
        mControlPanelBinding = ActivityAdminActionBinding.inflate(getLayoutInflater()); // Initializes the binding
        setContentView(mControlPanelBinding.getRoot()); // Sets the content view to the activity_admin_action layout
        initializeViews(); // Initializes the views
        setupFlightDAO(); // Sets up the database
        populateFlightList(); // Populates the flight list
        setupListeners(); // Sets the listeners
    }

    /**
     * Abstract: This method initializes the views
     *
     * @param: none
     * @return: void
     */

    private void initializeViews() {
        mHeaderTextView = mControlPanelBinding.adminActionBanner; // Initializes the header text view
        mNotificationTextView = mControlPanelBinding.adminActionMessageDisplay; // Initializes the notification text view
        mFlightCatalogTitleTextView = mControlPanelBinding.adminActionFlightListTitle; // Initializes the flight catalog title text view
        mFlightCatalogTextView = mControlPanelBinding.adminActionFlightListDisplay; // Initializes the flight catalog text view
        mFlightCatalogTextView.setMovementMethod(new ScrollingMovementMethod()); // Sets the movement method for the flight catalog text view
        mModifyFlightInput = mControlPanelBinding.adminActionEditFlight; // Initializes the modify flight input
        mCreateFlightButton = mControlPanelBinding.adminActionAddButton; // Initializes the create flight button
        mUpdateFlightButton = mControlPanelBinding.adminActionEditButton; // Initializes the update flight button
        mReturnButton = mControlPanelBinding.adminActionBackButton; // Initializes the return button
    }

    /**
     * Abstract: This method sets up the database
     *
     * @param: none
     * @return: void
     */

    private void setupFlightDAO() {
        mArrivalFlightDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME) // Initializes the database
                .allowMainThreadQueries() // Allows the database to run on the main thread
                .build() // Builds the database
                .FlightDAO(); // Initializes the database access object
    }

    /**
     * Abstract: This method populates the flight list
     *
     * @param: none
     * @return: void
     */

    private void populateFlightList() {
        List<Flight> mFlightList = mArrivalFlightDAO.getFlights(); // Gets all the flights
        String flightDetails = mFlightList.isEmpty() ? getString(R.string.add_new_flight) :
                mFlightList.stream().map(Flight::toString).collect(Collectors.joining()); // Gets the flight details
        mFlightCatalogTextView.setText(flightDetails); // Sets the flight catalog text view to the flight details
    }

    /**
     * Abstract: This method sets the listeners
     *
     * @param: none
     * @return: void
     */

    private void setupListeners() {
        String currentUsername = getIntent().getStringExtra(ADMIN_ACTION_ACTIVITY_USER); // Gets the username from the intent
        mReturnButton.setOnClickListener(view -> navigateToLandingActivity(currentUsername)); // Sets the return button listener
        mUpdateFlightButton.setOnClickListener(view -> editFlightAction(currentUsername)); // Sets the update flight button listener
        mCreateFlightButton.setOnClickListener(view -> navigateToAddFlightActivity(currentUsername)); // Sets the create flight button listener
    }

    /**
     * Abstract: This method navigates to the landing activity
     *
     * @param: String
     * @return: void
     */

    private void navigateToLandingActivity(String username) { // Navigates to the landing activity
        Intent intent = LandingActivity.getIntent(getApplicationContext(), username); // Initializes the intent
        startActivity(intent); // Starts the activity
    }

    /**
     * Abstract: This method edits the flight
     *
     * @param: String
     * @return: void
     */

    private void editFlightAction(String currentUsername) { // Edits the flight
        String flightNumber = mModifyFlightInput.getText().toString(); // Gets the flight number
        if (editFlight(flightNumber)) { // Checks if the flight is valid
            Intent intent = ModifyFlightActivity.getIntent(getApplicationContext(), currentUsername, flightNumber); // Initializes the intent
            startActivity(intent); // Starts the activity
        }
    }

    /**
     * Abstract: This method edits the flight
     *
     * @param: String
     * @return: boolean
     */

    private void navigateToAddFlightActivity(String username) { // Navigates to the add flight activity
        Intent intent = AddFlightActivity.getIntent(getApplicationContext(), username); // Initializes the intent
        startActivity(intent); // Starts the activity
    }

    /**
     * Abstract: This method edits the flight
     *
     * @param: String
     * @return: boolean
     */

    public boolean editFlight(String flightNumber) { // Edits the flight
        List<Flight> mFlightList = mArrivalFlightDAO.getFlights(); // Gets all the flights
        if (mFlightList.stream().anyMatch(flight -> flightNumber.equals(flight.getFlightNumber()))) { // Checks if the flight number is valid
            return true; // Returns true
        }
        mNotificationTextView.setText(R.string.error_invalid_flight_number); // Sets the notification text view to the error message
        mNotificationTextView.setVisibility(View.VISIBLE); // Sets the notification text view to visible
        return false; // Returns false
    }

    /**
     * Abstract: This method gets the intent
     *
     * @param: Context, String
     * @return: Intent
     */

    public static Intent getIntent(Context context, String username) { // Gets the intent
        Intent intent = new Intent(context, AdminMenuActivity.class); // Initializes the intent
        intent.putExtra(ADMIN_ACTION_ACTIVITY_USER, username); // Puts the username in the intent
        return intent; // Returns the intent
    }
}
