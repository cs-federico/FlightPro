package com.example.flightpro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.flightpro.DB.AppDataBase;
import com.example.flightpro.DB.BookingDAO;
import com.example.flightpro.DB.FlightDAO;
import com.example.flightpro.DB.UserDAO;
import com.example.flightpro.databinding.ActivityLandingBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract: This class is the landing page for the application.
 *
 * @author: Federico Marquez Murrieta
 * @since: 2023-11-29
 */

public class LandingActivity extends AppCompatActivity {

    private static final String LANDING_ACTIVITY_USER = "com.example.flightpro.LandingActivityUser";  // LANDING_ACTIVITY_USER

    private TextView mWelcomeSign, mNotificationArea, mReservationHeader, mWelcomeNote;  // mWelcomeSign, mNotificationArea, mReservationHeader, mWelcomeNote
    private ListView mReservationsListView;  // mReservationsListView
    private Button mReserveSpotButton, mAdministratorPanelButton, mTouristInfoButton; // mReserveSpotButton, mAdministratorPanelButton, mTouristInfoButton
    private UserDAO mUserDatabaseAccessor; // mUserDatabaseAccessor
    private FlightDAO mFlightDataAccessor; // mFlightDataAccessor
    private BookingDAO mBookingDataAccessor; // mBookingDataAccessor
    private User mActiveUser; // mActiveUser
    private List<Booking> mReservationsList; // mReservationsList
    private List<Flight> mFlightsList; // mFlightsList
    private ActivityLandingBinding mInterfaceBinder; // mInterfaceBinder

    /**
     * Abstract: This method is called when the activity is created
     *
     * @param: savedInstanceState
     * @return: void
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) { // onCreate
        super.onCreate(savedInstanceState); // Calls the super class onCreate method
        setContentView(R.layout.activity_landing); // Sets the content view to the activity_landing layout

        initializeViews(); // Initializes the views
        initializeDatabase(); // Initializes the database
        setCurrentUser(); // Sets the current user

        if (mActiveUser.getIsAdmin() == 1) { // If the user is an admin
            mAdministratorPanelButton.setVisibility(View.VISIBLE); // Sets the admin button to visible
            mWelcomeNote.setText(String.format("Welcome admin: %s! ", mActiveUser.getUsername())); // Sets the welcome note to the admin
        } else { // If the user is not an admin
            mWelcomeNote.setText(String.format("Welcome user: %s! ", mActiveUser.getUsername())); // Sets the welcome note to the user
        }

        setButtonListeners(); // Sets the button listeners
        refreshDisplay();   // Refreshes the display
    }

    /**
     * Abstract: This method initializes the views
     *
     * @param: none
     * @return: void
     */

    private void initializeViews() {  // initializeViews
        mInterfaceBinder = ActivityLandingBinding.inflate(getLayoutInflater()); // Initializes the binding
        setContentView(mInterfaceBinder.getRoot()); // Sets the content view to the activity_landing layout

        mWelcomeSign = mInterfaceBinder.landingBanner; // Gets the welcome sign from the layout
        mReservationHeader = mInterfaceBinder.landingBookingTableTitle; // Gets the reservation header from the layout
        mNotificationArea = mInterfaceBinder.landingMessageDisplay; // Gets the notification area from the layout
        mReservationsListView = mInterfaceBinder.landingBookingDisplayTable; // Gets the reservations list view from the layout
        mReserveSpotButton = mInterfaceBinder.landingBookButton; // Gets the reserve spot button from the layout
        mAdministratorPanelButton = mInterfaceBinder.landingAdminButton; // Gets the administrator panel button from the layout
        mTouristInfoButton = mInterfaceBinder.visitMeButton; // Gets the tourist info button from the layout
        mWelcomeNote = mInterfaceBinder.greetingTextView; // Gets the welcome note from the layout
    }

    /**
     * Abstract: This method initializes the database
     *
     * @param: none
     * @return: void
     */

    private void initializeDatabase() { // initializeDatabase
        AppDataBase db = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME) // Initializes the database
                .allowMainThreadQueries() // Allows the database to run on the main thread
                .build(); // Builds the database
        mUserDatabaseAccessor = db.UserDAO(); // Initializes the user database accessor
        mFlightDataAccessor = db.FlightDAO(); // Initializes the flight data accessor
        mBookingDataAccessor = db.BookingDAO();  // Initializes the booking data accessor
    }

    /**
     * Abstract: This method sets the current user
     *
     * @param: none
     * @return: void
     */

    private void setCurrentUser() { // setCurrentUser
        String currentUsername = getIntent().getStringExtra(LANDING_ACTIVITY_USER); // Gets the current username from the intent
        mActiveUser = mUserDatabaseAccessor.getUserByUsername(currentUsername); // Sets the active user to the current username
    }

    /**
     * Abstract: This method sets the button listeners
     *
     * @param: none
     * @return: void
     */

    private void setButtonListeners() { // setButtonListeners
        mAdministratorPanelButton.setOnClickListener(view -> navigateTo(AdminMenuActivity.getIntent(this, mActiveUser.getUsername()))); // Sets the administrator panel button listener

        mReserveSpotButton.setOnClickListener(view -> {     // Sets the reserve spot button listener
            updateFlightFullness(); // Updates the flight fullness
            navigateTo(ReservationForFlightActivity.getIntent(this, mActiveUser.getUsername())); // Navigates to the reservation for flight activity
        });

        mTouristInfoButton.setOnClickListener(view -> navigateTo(new Intent(this, VisitPlacesActivity.class)));     // Sets the tourist info button listener

        findViewById(R.id.btnShowBenefitsOfFlying).setOnClickListener(view -> navigateTo(new Intent(this, BenefitsOfFlyingActivity.class))); // Sets the benefits of flying button listener
    }

    /**
     * Abstract: This method updates the flight fullness
     *
     * @param: none
     * @return: void
     */

    private void updateFlightFullness() { // updateFlightFullness
        List<Flight> mFlightList = mFlightDataAccessor.getFlights();    // Gets the flight list from the database
        for (Flight flight : mFlightList) { // For each flight in the flight list
            if (flight.getCapacity() == 150) { // If the flight capacity is 150
                mFlightDataAccessor.updateFlightAvailability(flight.getFlightId(), 1); // Updates the flight availability
            }
        }
    }

    /**
     * Abstract: This method navigates to the intent
     *
     * @param: intent
     * @return: void
     */

    private void navigateTo(Intent intent) {
        startActivity(intent);
    }

    /**
     * Abstract: This method refreshes the display
     *
     * @param: none
     * @return: void
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // onCreateOptionsMenu
        MenuInflater inflater = getMenuInflater(); // Initializes the menu inflater
        inflater.inflate(R.menu.log_out_menu, menu); // Inflates the menu
        return true; // Returns true
    }

    /**
     * Abstract: This method handles the menu item selection
     *
     * @param: item
     * @return: boolean
     */

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // onOptionsItemSelected
        int itemId = item.getItemId(); // Gets the item id
        if (itemId == R.id.landingLogOutMenuButton) { // If the item id is the log out menu button
            showAlertDialog("Sign Out", "Would you like to sign out?", // Shows the alert dialog
                    () -> navigateTo(MainScreenActivity.getIntent(this)),   // Navigates to the main screen activity
                    "USER IS STILL SIGNED IN"); // Shows the user is still signed in message
            return true; // Returns true
        } else if (itemId == R.id.landingDeleteUserMenuButton) { // If the item id is the delete user menu button
            showAlertDialog("Obliterate User", "Would you like to Obliterate User?", // Shows the alert dialog
                    this::obliterateUser, // Obliterates the user
                    "USER HAS NOT BEEN OBLITERATED"); // Shows the user has not been obliterated message
            return true; // Returns true
        }
        return super.onOptionsItemSelected(item); // Returns the super class onOptionsItemSelected method
    }

    /**
     * Abstract: This method shows the alert dialog
     *
     * @param: title, message, onPositive, onCancelMessage
     * @return: void
     */

    private void showAlertDialog(String title, String message, Runnable onPositive, String onCancelMessage) { // showAlertDialog
        toastMaker("You have clicked on " + title + ". Did you mean to do this?");  // Shows the toast message
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this); // Initializes the alert dialog builder
        alertBuilder.setMessage(message); // Sets the message

        alertBuilder.setPositiveButton("Yes", (dialogInterface, i) -> onPositive.run()); // Sets the positive button
        alertBuilder.setNegativeButton("No", (dialogInterface, i) -> toastMaker(onCancelMessage));  // Sets the negative button
        alertBuilder.setCancelable(true); // Sets the alert dialog to cancelable
        alertBuilder.setOnCancelListener(dialogInterface -> toastMaker(onCancelMessage)); // Sets the on cancel listener
        alertBuilder.create().show(); // Creates and shows the alert dialog
    }

    /**
     * Abstract: This method obliterates the user
     *
     * @param: none
     * @return: void
     */

    private void obliterateUser() {
        List<Booking> deletedBookings = mBookingDataAccessor.getBookingsByUserId(mActiveUser.getUserId()); // Gets the deleted bookings
        for (Booking booking : deletedBookings) { // For each booking in the deleted bookings
            Flight flight = mFlightDataAccessor.getFlightByFlightId(booking.getFlightId()); // Gets the flight by flight id
            int updatedCapacity = flight.getCapacity() - booking.getQuantity(); // Updates the capacity
            mFlightDataAccessor.updateFlightCapacity(flight.getFlightId(), updatedCapacity); // Updates the flight capacity
            if (updatedCapacity < 150) { // If the updated capacity is less than 150
                mFlightDataAccessor.updateFlightAvailability(flight.getFlightId(), 0); // Updates the flight availability
            }
            mBookingDataAccessor.delete(booking); // Deletes the booking
        }
        mUserDatabaseAccessor.delete(mActiveUser); // Deletes the active user
        navigateTo(MainScreenActivity.getIntent(this)); // Navigates to the main screen activity
    }

    /**
     * Abstract: This method shows the toast message
     *
     * @param: message
     * @return: void
     */

    public void toastMaker(String message) { // toastMaker
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show(); // Shows the toast message
    }

    /**
     * Abstract: This method gets the intent
     *
     * @param: context, username
     * @return: Intent
     */

    public static Intent getIntent(Context context, String username) { // getIntent
        Intent intent = new Intent(context, LandingActivity.class); // Initializes the intent
        intent.putExtra(LANDING_ACTIVITY_USER, username); // Puts the username in the intent
        return intent; // Returns the intent
    }

    /**
     * Abstract: This method refreshes the display
     *
     * @param: none
     * @return: void
     */

    public void refreshDisplay() { // refreshDisplay
        mReservationsList = mBookingDataAccessor.getBookings(); // Gets the reservations list
        mFlightsList = mFlightDataAccessor.getFlights(); // Gets the flights list
        ArrayList<String> list = new ArrayList<>(); // Initializes the list
        for (Booking booking : mReservationsList) { // For each booking in the reservations list
            if (booking.getUserId() == mActiveUser.getUserId()) { // If the booking user id is the active user id
                for (Flight flight : mFlightsList) { // For each flight in the flights list
                    if (flight.getFlightId() == booking.getFlightId()) { // If the flight id is the booking flight id
                        list.add(formatBookingDetails(booking, flight)); // Adds the booking details to the list
                    }
                }
            }
        }
        mReservationsListView.setAdapter(new FlightListAdapter(list, getApplicationContext())); // Sets the reservations list view adapter
    }

    /**
     * Abstract: This method formats the booking details
     *
     * @param: booking, flight
     * @return: String
     */

    private String formatBookingDetails(Booking booking, Flight flight) {
        return new StringBuilder() // Returns the booking details
                .append("Reservation ID: ").append(booking.getBookingId()).append("\n") // Appends the reservation id
                .append("Flight Number: ").append(flight.getFlightNumber()).append("\n") // Appends the flight number
                .append("Origin: ").append(flight.getOrigin()).append("\n") // Appends the origin
                .append("Destiny: ").append(flight.getDestination()).append("\n") // Appends the destiny
                .append("Total Seats Reserved: ").append(booking.getQuantity()).append("\n") // Appends the total seats reserved
                .toString();
    }
}
