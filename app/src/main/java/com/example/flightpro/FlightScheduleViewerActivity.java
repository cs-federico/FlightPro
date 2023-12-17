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
import com.example.flightpro.DB.BookingDAO;
import com.example.flightpro.DB.FlightDAO;
import com.example.flightpro.DB.UserDAO;
import com.example.flightpro.databinding.ActivityCityFlightBinding;

import java.util.List;

/**
 * Abstract: This class is responsible for displaying the flight schedule for a given city.
 *
 * @author: Federico Marquez Murrieta
 * @since: 2023-11-29
 */

public class FlightScheduleViewerActivity extends AppCompatActivity { // FlightScheduleViewerActivity class
    private static final String CITY_FLIGHT_ACTIVITY_USER = "com.example.flightpro.FlightScheduleViewerActivityUser";
    private static final String CITY_FLIGHT_ACTIVITY_CITY = "com.example.flightpro.FlightScheduleViewerActivityCity";
    TextView mMunicipalFlightAnnouncement, mUrbanArrivalFlightTitle, mMunicipalInboundFlightsView, mExodusFlightsTitle, mUrbanOutboundFlightsView, mFlightNotificationDisplay;
    EditText mUrbanFlightDigit, mMunicipalFlightBookingAmount;
    Button mFlightBookingButton, mReturnButtonMunicipalFlight;
    UserDAO mUserDBAccess;
    FlightDAO mAirwayDBAccess;
    BookingDAO mBookingDBAccess;
    User mActiveUser;
    ActivityCityFlightBinding mMunicipalFlightLayout;

    /**
     * Abstract: This method is responsible for creating the activity.
     *
     * @param: savedInstanceState
     * @return: void
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) { // onCreate method
        super.onCreate(savedInstanceState);  // Calls the super class onCreate method
        mMunicipalFlightLayout = ActivityCityFlightBinding.inflate(getLayoutInflater()); // Initializes the binding
        setContentView(mMunicipalFlightLayout.getRoot()); // Sets the content view to the activity_city_flight layout
        bindViews(); // Binds the views
        setupDAOs(); // Sets up the database access objects
        processIntentData(); // Processes the intent data
        setupFlightsDisplay(); // Sets up the flight display
        setupButtons();// Sets up the buttons
    }

    /**
     * Abstract: This method is responsible for binding the views.
     *
     * @param: none
     * @return: void
     */

    private void bindViews() {
        mMunicipalFlightAnnouncement = mMunicipalFlightLayout.cityFlightBanner; // Binds the banner
        mUrbanArrivalFlightTitle = mMunicipalFlightLayout.cityFlightArrivingTitle; // Binds the title
        mMunicipalInboundFlightsView = mMunicipalFlightLayout.cityFlightArrivingDisplay; // Binds the display
        mMunicipalInboundFlightsView.setMovementMethod(new ScrollingMovementMethod()); // Sets the movement method
        mExodusFlightsTitle = mMunicipalFlightLayout.cityFlightDepartingTitle; // Binds the title
        mUrbanOutboundFlightsView = mMunicipalFlightLayout.cityFlightDepartingDisplay; // Binds the display
        mUrbanOutboundFlightsView.setMovementMethod(new ScrollingMovementMethod()); // Sets the movement method
        mFlightNotificationDisplay = mMunicipalFlightLayout.cityFlightMessageDisplay; // Binds the display
        mUrbanFlightDigit = mMunicipalFlightLayout.cityFlightFlightNumber; // Binds the flight number
        mMunicipalFlightBookingAmount = mMunicipalFlightLayout.cityFlightBookingQuantity;
        mFlightBookingButton = mMunicipalFlightLayout.cityFlightBookButton; // Binds the book button
        mReturnButtonMunicipalFlight = mMunicipalFlightLayout.cityFlightBackButton; // Binds the return button
    }

    /**
     * Abstract: This method is responsible for setting up the database access objects.
     *
     * @param: none
     * @return: void
     */

    private void setupDAOs() {
        AppDataBase db = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME) // Initializes the database
                .allowMainThreadQueries() // Allows the database to run on the main thread
                .build(); // Builds the database
        mUserDBAccess = db.UserDAO(); // Initializes the database access object
        mAirwayDBAccess = db.FlightDAO(); // Initializes the database access object
        mBookingDBAccess = db.BookingDAO(); // Initializes the database access object
    }

    /**
     * Abstract: This method is responsible for processing the intent data.
     *
     * @param: none
     * @return: void
     */

    private void processIntentData() {
        String currentUsername = getIntent().getStringExtra(CITY_FLIGHT_ACTIVITY_USER); // Gets the current username
        mActiveUser = mUserDBAccess.getUserByUsername(currentUsername); // Gets the active user
        String currentCity = getIntent().getStringExtra(CITY_FLIGHT_ACTIVITY_CITY); // Gets the current city
        mMunicipalFlightAnnouncement.setText(currentCity + " Flights"); // Sets the announcement
    }

    /**
     * Abstract: This method is responsible for setting up the flight display.
     *
     * @param: none
     * @return: void
     */

    private void setupFlightsDisplay() {
        String currentCity = getIntent().getStringExtra(CITY_FLIGHT_ACTIVITY_CITY); // Gets the current city
        List<Flight> mFlightList = mAirwayDBAccess.getFlights(); // Gets the flight list
        mFlightList.removeIf(flight -> flight.getIsFull() == 1); // Removes the full flights
        displayFlights(mFlightList, currentCity); // Displays the flights
    }

    /**
     * Abstract: This method is responsible for displaying the flights.
     *
     * @param: flights, currentCity
     * @return: void
     */

    private void displayFlights(List<Flight> flights, String currentCity) {
        StringBuilder arrival = new StringBuilder(); // Initializes the arrival string builder
        StringBuilder departure = new StringBuilder(); // Initializes the departure string builder
        for (Flight flight : flights) { // For each flight
            String flightInfo = String.format("ID: %d\nNumber: %s\nFrom: %s\nTo: %s\nCapacity: %d/150\n\n\n",
                    flight.getFlightId(), flight.getFlightNumber(), flight.getOrigin(), flight.getDestination(), flight.getCapacity()); // Formats the flight info
            if (flight.getOrigin().equals(currentCity)) { // If the origin is the current city
                departure.append(flightInfo); // Appends the flight info to the departure string builder
            } else if (flight.getDestination().equals(currentCity)) { // If the destination is the current city
                arrival.append(flightInfo); // Appends the flight info to the arrival string builder
            }
        }
        mMunicipalInboundFlightsView.setText(arrival.length() > 0 ? arrival.toString() : getString(R.string.message_no_flights)); // Sets the arrival text
        mUrbanOutboundFlightsView.setText(departure.length() > 0 ? departure.toString() : getString(R.string.message_no_flights)); // Sets the departure text
    }

    /**
     * Abstract: This method is responsible for setting up the buttons.
     *
     * @param: none
     * @return: void
     */

    private void setupButtons() {
        mReturnButtonMunicipalFlight.setOnClickListener(view -> navigateTo(ReservationForFlightActivity.getIntent(getApplicationContext(), mActiveUser.getUsername()))); // Sets the return button on click listener
        mFlightBookingButton.setOnClickListener(view -> { // Sets the book button on click listener
            if (bookFlight()) { // If the flight is booked
                navigateTo(LandingActivity.getIntent(getApplicationContext(), mActiveUser.getUsername())); // Navigates to the landing activity
            }
        });
    }

    /**
     * Abstract: This method is responsible for navigating to the intent.
     *
     * @param: intent
     * @return: void
     */

    private void navigateTo(Intent intent) {
        startActivity(intent);
    }

    /**
     * Abstract: This method is responsible for booking a flight.
     *
     * @param: none
     * @return: boolean
     */

    public boolean bookFlight() {
        String flightNumber = mUrbanFlightDigit.getText().toString(); // Gets the flight number
        if (flightNumber.isEmpty()) { // If the flight number is empty
            showErrorMessage(R.string.error_missing_flight_number); // Shows the error message
            return false;   // Returns false
        }

        List<Flight> flightList = mAirwayDBAccess.getFlights(); // Gets the flight list
        flightList.removeIf(flight -> !flight.getFlightNumber().equals(flightNumber) || flight.getIsFull() == 1); // Removes the flights that don't match the flight number or are full

        if (flightList.isEmpty()) { // If the flight list is empty
            showErrorMessage(R.string.message_no_flights); // Shows the error message
            return false;  // Returns false
        }

        Flight flight = flightList.get(0); // Gets the flight

        String bookingQuantityText = mMunicipalFlightBookingAmount.getText().toString(); // Gets the booking quantity text
        if (bookingQuantityText.isEmpty()) { // If the booking quantity text is empty
            showErrorMessage(R.string.error_no_booking_quantity); // Shows the error message
            return false; // Returns false
        }

        int quantity = Integer.parseInt(bookingQuantityText); // Parses the  booking quantity text
        if (quantity == 0 || quantity > (150 - flight.getCapacity())) { // If the quantity is 0 or greater than the capacity
            showErrorMessage(quantity == 0 ? R.string.error_invalid_booking_quantity : R.string.error_excess_booking_quantity); // Shows the error message
            return false; // Returns false
        }

        Booking newBooking = new Booking(mActiveUser.getUserId(), flight.getFlightId(), quantity); // Initializes the new booking
        mBookingDBAccess.insert(newBooking); // Inserts the new booking
        mAirwayDBAccess.updateFlightCapacity(flight.getFlightId(), flight.getCapacity() + quantity); // Updates the flight capacity

        if (flight.getCapacity() + quantity >= 150) { // If the flight is full
            mAirwayDBAccess.updateFlightAvailability(flight.getFlightId(), 1); // Updates the flight availability
        }
        return true; // Returns true
    }

    /**
     * Abstract: This method is responsible for showing the error message.
     *
     * @param: messageId
     * @return: void
     */

    private void showErrorMessage(int messageId) { // Shows the error message
        mFlightNotificationDisplay.setText(messageId); // Sets the message view text
        mFlightNotificationDisplay.setVisibility(View.VISIBLE); // Sets the message view to visible
    }

    /**
     * Abstract: This method is responsible for getting the intent.
     *
     * @param: context, username, cityName
     * @return: intent
     */

    public static Intent getIntent(Context context, String username, String cityName) { // Gets the intent
        Intent intent = new Intent(context, FlightScheduleViewerActivity.class); // Initializes the intent
        intent.putExtra(CITY_FLIGHT_ACTIVITY_USER, username); // Puts the username in the intent
        intent.putExtra(CITY_FLIGHT_ACTIVITY_CITY, cityName); // Puts the city name in the intent
        return intent; // Returns the intent
    }
}
