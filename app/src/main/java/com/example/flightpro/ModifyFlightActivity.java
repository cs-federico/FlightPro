package com.example.flightpro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.flightpro.DB.AppDataBase;
import com.example.flightpro.DB.BookingDAO;
import com.example.flightpro.DB.FlightDAO;
import com.example.flightpro.databinding.ActivityEditFlightBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract: This class is responsible for the registration activity. It allows the user to create
 *
 * @author: Federico Marquez Murrieta
 * @since: 2023-11-29
 */

public class ModifyFlightActivity extends AppCompatActivity {

    private static final String EDIT_FLIGHT_ACTIVITY_USER = "com.example.project2.ModifyFlightActivityUser"; // String that stores the key for the username
    private static final String EDIT_FLIGHT_ACTIVITY_FLIGHT = "com.example.project2.ModifyFlightActivityFlight"; // String that stores the key for the flight number

    TextView mFlightHeader; // TextView that displays the title
    TextView mReservationHeading; // TextView that displays the reservation heading
    ListView mFlightReservationsList; // ListView that displays the reservations
    RadioButton mUnavailableOption; // RadioButton that allows the user to select the flight as unavailable
    RadioButton mAvailableOption; // RadioButton that allows the user to select the flight as available
    TextView mFlightStatusMessage; // TextView that displays the status message
    Button mModifyFlightButton; // Button that allows the user to modify the flight
    Button mDeleteFlightButton; // Button that allows the user to delete the flight
    Button mReturnButton; // Button that allows the user to return to the previous activity

    Flight mActive_Flight; // Flight object that stores the current flight
    FlightDAO mFlightManagementDAO; // FlightDAO object that allows the user to access the database
    BookingDAO mReservationDAO; // BookingDAO object that allows the user to access the database
    List<Booking> mReservationList; // List that stores the reservations
    List<Flight> mListOfFlight; // List that stores the flights
    ActivityEditFlightBinding mFlightModificationBinding; // Binding that allows the user to access the views in the layout

    /**
     * Abstract: This method is called when the activity is created
     *
     * @param: savedInstanceState
     * @return: void
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) { // onCreate method
        super.onCreate(savedInstanceState); // Calls the super class onCreate method
        mFlightModificationBinding = ActivityEditFlightBinding.inflate(getLayoutInflater()); // Initializes the binding
        setContentView(mFlightModificationBinding.getRoot()); // Sets the content view to the activity_edit_flight layout
        bindViews(); // Binds the views
        initDAOs(); // Initializes the DAOs

        String currentUsername = getIntent().getStringExtra(EDIT_FLIGHT_ACTIVITY_USER); // Gets the username from the intent
        String currentFlightNumber = getIntent().getStringExtra(EDIT_FLIGHT_ACTIVITY_FLIGHT); // Gets the flight number from the intent

        setTitleForBookingList(currentFlightNumber); // Sets the title for the booking list
        refreshDisplay(currentFlightNumber); // Refreshes the display
        setupButtonListeners(currentUsername, currentFlightNumber); // Sets up the button listeners
    }

    /**
     * Abstract: This method binds the views
     *
     * @param: none
     * @return: void
     */

    private void bindViews() {
        mFlightHeader = mFlightModificationBinding.editFlightBanner; // Binds the flight header
        mReservationHeading = mFlightModificationBinding.editFlightBookingTitle; // Binds the reservation heading
        mFlightReservationsList = mFlightModificationBinding.editFlightBookingList; // Binds the reservation list
        mUnavailableOption = mFlightModificationBinding.notAvailableRadioButton; // Binds the unavailable option
        mAvailableOption = mFlightModificationBinding.isAvailableRadioButton; // Binds the available option
        mFlightStatusMessage = mFlightModificationBinding.editFlightMessageDisplay; // Binds the flight status message
        mModifyFlightButton = mFlightModificationBinding.editFlightEditButton; // Binds the modify flight button
        mDeleteFlightButton = mFlightModificationBinding.editFlightRemoveButton; // Binds the delete flight button
        mReturnButton = mFlightModificationBinding.editFlightBackButton; // Binds the return button
    }

    /**
     * Abstract: This method initializes the DAOs
     *
     * @param: none
     * @return: void
     */

    private void initDAOs() {
        AppDataBase db = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME) // Initializes the database
                .allowMainThreadQueries() // Allows the database to run on the main thread
                .build(); // Builds the database
        mFlightManagementDAO = db.FlightDAO(); // Initializes the flight management DAO
        mReservationDAO = db.BookingDAO(); // Initializes the reservation DAO
    }

    /**
     * Abstract: This method sets the title for the booking list
     *
     * @param: flightNumber
     * @return: void
     */

    private void setTitleForBookingList(String flightNumber) {
        mReservationHeading.setText(String.format("Reservations For Flight %s", flightNumber)); // Sets the title for the booking list
    }

    /**
     * Abstract: This method sets up the button listeners
     *
     * @param: username, flightNumber
     * @return: void
     */

    private void setupButtonListeners(String username, String flightNumber) {
        mReturnButton.setOnClickListener(view -> startAdminActionActivity(username)); // Sets the return button listener
        mDeleteFlightButton.setOnClickListener(view -> removeFlight(flightNumber, username)); // Sets the delete flight button listener
        mModifyFlightButton.setOnClickListener(view -> { // Sets the modify flight button listener
            if (editFlight(flightNumber)) { // Checks if the flight was edited
                startAdminActionActivity(username); // Starts the admin action activity
            }
        });
    }

    /**
     * Abstract: This method starts the admin action activity
     *
     * @param: username
     * @return: void
     */

    private void startAdminActionActivity(String username) {
        Intent intent = AdminMenuActivity.getIntent(getApplicationContext(), username); // Initializes the intent
        startActivity(intent); // Starts the activity
    }

    /**
     * Abstract: This method refreshes the display
     *
     * @param: currentFlightNumber
     * @return: void
     */

    public void refreshDisplay(String currentFlightNumber) {
        mReservationList = mReservationDAO.getBookings(); // Gets the reservations
        mActive_Flight = findCurrentFlight(currentFlightNumber); // Finds the current flight

        ArrayList<String> list = new ArrayList<>(); // Initializes the list
        for (Booking booking : mReservationList) { // Loops through the reservations
            if (booking.getFlightId() == mActive_Flight.getFlightId()) { // Checks if the flight ID matches the current flight ID
                String bookingDetails = String.format("Reservation ID: %d\nFlight Number: %s\nOrigin: %s\nDestiny: %s\nSeats: %d\n",
                        booking.getBookingId(), mActive_Flight.getFlightNumber(), mActive_Flight.getOrigin(), mActive_Flight.getDestination(), booking.getQuantity()); // Formats the booking details
                list.add(bookingDetails); // Adds the booking details to the list
            }
        }
        mFlightReservationsList.setAdapter(new FlightListAdapter(list, getApplicationContext())); // Sets the adapter for the list
    }

    /**
     * Abstract: This method finds the current flight
     *
     * @param: flightNumber
     * @return: Flight
     */

    private Flight findCurrentFlight(String flightNumber) {
        mListOfFlight = mFlightManagementDAO.getFlights(); // Gets the flights
        for (Flight flight : mListOfFlight) { // Loops through the flights
            if (flightNumber.equals(flight.getFlightNumber())) { // Checks if the flight number matches the current flight number
                return flight; // Returns the flight
            }
        }
        return null; // Returns null
    }

    /**
     * Abstract: This method edits the flight
     *
     * @param: currentFlightNumber
     * @return: boolean
     */

    public boolean editFlight(String currentFlightNumber) {
        mActive_Flight = findCurrentFlight(currentFlightNumber); // Finds the current flight

        if (mUnavailableOption.isChecked()) {  // Checks if the unavailable option is checked
            mFlightManagementDAO.updateFlightAvailability(mActive_Flight.getFlightId(), 1); // Updates the flight availability
            return true; // Returns true
        } else if (mAvailableOption.isChecked()) { // Checks if the available option is checked
            mFlightManagementDAO.updateFlightAvailability(mActive_Flight.getFlightId(), 0); // Updates the flight availability
            return true; // Returns true
        } else {
            mFlightStatusMessage.setText(R.string.error_role_not_selected); // Sets the flight status message
            mFlightStatusMessage.setVisibility(View.VISIBLE); // Sets the flight status message to visible
            return false; // Returns false
        }
    }

    /**
     * Abstract: This method removes the flight
     *
     * @param: currentFlightNumber, username
     * @return: void
     */

    public void removeFlight(String currentFlightNumber, String username) { // Removes the flight
        mActive_Flight = findCurrentFlight(currentFlightNumber); // Finds the current flight
        for (Booking booking : mReservationList) { // Loops through the reservations
            if (booking.getFlightId() == mActive_Flight.getFlightId()) { // Checks if the flight ID matches the current flight ID
                mReservationDAO.delete(booking); // Deletes the booking
            }
        }
        mFlightManagementDAO.delete(mActive_Flight); // Deletes the flight
        startAdminActionActivity(username); // Starts the admin action activity
    }

    /**
     * Abstract: This method gets the intent
     *
     * @param: context, username, flightNumber
     * @return: Intent
     */

    public static Intent getIntent(Context context, String username, String flightNumber) {
        Intent intent = new Intent(context, ModifyFlightActivity.class); // Initializes the intent
        Bundle extras = new Bundle(); // Initializes the bundle
        extras.putString(EDIT_FLIGHT_ACTIVITY_USER, username); // Puts the username in the bundle
        extras.putString(EDIT_FLIGHT_ACTIVITY_FLIGHT, flightNumber); // Puts the flight number in the bundle
        intent.putExtras(extras); // Puts the bundle in the intent
        return intent; // Returns the intent
    }
}
