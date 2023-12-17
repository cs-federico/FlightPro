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
import com.example.flightpro.databinding.ActivityBookFlightBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract: This class is used to book a flight for a user.
 *
 * @author: Federico Marquez Murrieta
 * @since: 2023-11-29
 */

public class ReservationForFlightActivity extends AppCompatActivity {
    private static final String BOOK_FLIGHT_ACTIVITY_USER = "com.example.flightpro.ReservationForFlightActivityUser"; // Key for the username of the current user

    TextView mFlightReservationBanner, mReservationListTitle, mReservationDisplay, mReservationMessage; // TextViews for the banner, list title, list display, and message display
    EditText mReservationOfFlightCity, mReservationOfFlightByNumber, mFlightReservationAmount; // EditTexts for the city, flight number, and amount of flights to book
    Button mCitySearchButton, mReservationByNumberButton, mReservationFlightBackButton; // Buttons for searching by city, flight number, and going back

    UserDAO mReservationOfFlightUserDAO; // UserDAO for the current user
    FlightDAO mAssertingReservationFlightDAO; // FlightDAO for the flights
    BookingDAO mReservationFlightReserveDAO; // BookingDAO for the bookings

    User mActiveReservationUser; // User for the current user

    ActivityBookFlightBinding mReservationBinding; // Binding for the activity

    /**
     * Abstract: This method is used to create the activity.
     *
     * @param: savedInstanceState - Bundle for the activity
     * @return: void - This method does not return anything
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Calling the super method
        mReservationBinding = ActivityBookFlightBinding.inflate(getLayoutInflater()); // Setting the binding
        setContentView(mReservationBinding.getRoot()); // Setting the content view
        initializeUI(); // Calling the initializeUI method
        setupDatabase(); // Calling the setupDatabase method
        displayFlights(); // Calling the displayFlights method
        String currentUsername = getIntent().getStringExtra(BOOK_FLIGHT_ACTIVITY_USER); // Getting the username from the intent
        mActiveReservationUser = mReservationOfFlightUserDAO.getUserByUsername(currentUsername); // Getting the user from the database
        setupListeners(currentUsername); // Calling the setupListeners method

        /**
         * Abstract: This method is used to initialize the UI.
         * @param: None
         * @return: void - This method does not return anything
         * */
    }

    private void initializeUI() {
        mFlightReservationBanner = mReservationBinding.bookFlightBanner; // Setting the banner
        mReservationListTitle = mReservationBinding.bookFlightFlightListTitle; // Setting the list title
        mReservationDisplay = mReservationBinding.bookFlightListDisplay; // Setting the list display
        mReservationDisplay.setMovementMethod(new ScrollingMovementMethod()); // Setting the list display to scroll
        mReservationMessage = mReservationBinding.bookFlightMessageDisplay; // Setting the message display
        mReservationOfFlightCity = mReservationBinding.bookFlightByCity; // Setting the city
        mCitySearchButton = mReservationBinding.bookFlightByCityButton; // Setting the city search button
        mReservationOfFlightByNumber = mReservationBinding.bookFlightByFlightNumber; // Setting the flight number
        mFlightReservationAmount = mReservationBinding.bookFlightBookingQuantity; // Setting the amount of flights to book
        mReservationByNumberButton = mReservationBinding.bookFlightByNumberButton; // Setting the flight number search button
        mReservationFlightBackButton = mReservationBinding.bookFlightBackButton; // Setting the back button
    }

    /**
     * Abstract: This method is used to setup the database.
     *
     * @param: None
     * @return: void - This method does not return anything
     */

    private void setupDatabase() {
        AppDataBase database = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME) // Setting the database
                .allowMainThreadQueries() // Allowing main thread queries
                .build(); // Building the database
        mReservationOfFlightUserDAO = database.UserDAO(); // Setting the user DAO
        mAssertingReservationFlightDAO = database.FlightDAO(); // Setting the flight DAO
        mReservationFlightReserveDAO = database.BookingDAO(); // Setting the booking DAO
    }

    private void displayFlights() { // Setting the display flights method
        List<Flight> mFlightList = mAssertingReservationFlightDAO.getFlights(); // Getting the flights from the database
        ArrayList<String> cityList = new ArrayList<>(); // Creating a list of cities
        if (!mFlightList.isEmpty()) { // If the flight list is not empty
            String flightDetails = mFlightList.stream() // Setting the flight details
                    .filter(flight -> flight.getIsFull() == 0) // Filtering the flight list
                    .map(Flight::toString) // Mapping the flight list
                    .collect(Collectors.joining()); // Collecting the flight list
            mReservationDisplay.setText(flightDetails.isEmpty() ? getString(R.string.message_no_flights) : flightDetails); // Setting the flight details

            cityList.addAll(mFlightList.stream() // Adding the cities to the list
                    .filter(flight -> flight.getIsFull() == 0) // Filtering the flight list
                    .flatMap(flight -> Stream.of(flight.getOrigin(), flight.getDestination())) // Mapping the flight list
                    .distinct() // Distincting the flight list
                    .collect(Collectors.toList())); // Collecting the flight list
        } else { // If the flight list is empty
            mReservationDisplay.setText(R.string.message_no_flights); // Setting the flight details
        }
    }

    /**
     * Abstract: This method is used to setup the listeners.
     *
     * @param: currentUsername - String for the current username
     * @return: void - This method does not return anything
     */

    private void setupListeners(String currentUsername) { // Setting the setup listeners method
        mReservationFlightBackButton.setOnClickListener(view -> startActivity(LandingActivity.getIntent(getApplicationContext(), currentUsername))); // Setting the back button
        mCitySearchButton.setOnClickListener(view -> bookByCity(currentUsername)); // Setting the city search button
        mReservationByNumberButton.setOnClickListener(view -> { // Setting the flight number search button
            if (byFlightNumber()) { // If the flight number is valid
                startActivity(LandingActivity.getIntent(getApplicationContext(), currentUsername)); // Start the landing activity
            }
        });
    }

    /**
     * Abstract: This method is used to book a flight by city.
     *
     * @param: currentUsername - String for the current username
     * @return: void - This method does not return anything
     */

    private void bookByCity(String currentUsername) {
        String enteredCity = mReservationOfFlightCity.getText().toString(); // Getting the entered city
        List<Flight> mFlightList = mAssertingReservationFlightDAO.getFlights(); // Getting the flights from the database
        ArrayList<String> cityList = new ArrayList<>(); // Creating a list of cities
        for (Flight flight : mFlightList) { // For each flight in the flight list
            if (flight.getIsFull() == 0) { // If the flight is not full
                if (!cityList.contains(flight.getOrigin())) { // If the city list does not contain the origin
                    cityList.add(flight.getOrigin()); // Add the origin to the city list
                }
                if (!cityList.contains(flight.getDestination())) { // If the city list does not contain the destination
                    cityList.add(flight.getDestination()); // Add the destination to the city list
                }
            }
        }
        if (cityList.contains(enteredCity)) { // If the city list contains the entered city
            Intent intent = FlightScheduleViewerActivity.getIntent(getApplicationContext(), currentUsername, enteredCity); // Setting the intent
            startActivity(intent); // Starting the activity
        } else if (enteredCity.equals("")) { // If the entered city is empty
            mReservationMessage.setText(R.string.error_no_city_entered); // Setting the message
            mReservationMessage.setVisibility(View.VISIBLE); // Setting the visibility
        } else {
            mReservationMessage.setText(R.string.error_city_not_available); // Setting the message
            mReservationMessage.setVisibility(View.VISIBLE); // Setting the visibility
        }
    }

    /**
     * Abstract: This method is used to book a flight by flight number.
     *
     * @param: None
     * @return: boolean - This method returns a boolean
     */

    public boolean byFlightNumber() {
        List<Flight> flightList = mAssertingReservationFlightDAO.getFlights(); // Getting the flights from the database
        if (!flightList.isEmpty()) { // If the flight list is not empty
            flightList = flightList.stream() // Setting the flight list
                    .filter(flight -> flight.getIsFull() == 0 && flight.getCapacity() < 150) // Filtering the flight list
                    .collect(Collectors.toList()); // Collecting the flight list
        } else {
            mReservationMessage.setText(R.string.message_no_flights); // Setting the message
            mReservationMessage.setVisibility(View.VISIBLE); // Setting the visibility
            return false; // Returning false
        }

        String flightNumber = mReservationOfFlightByNumber.getText().toString(); // Getting the flight number
        if (flightNumber.equals("")) { // If the flight number is empty
            mReservationMessage.setText(R.string.error_missing_flight_number); // Setting the message
            mReservationMessage.setVisibility(View.VISIBLE); // Setting the visibility
            return false; // Returning false
        }

        for (Flight flight : flightList) { // For each flight in the flight list
            if (flightNumber.equals(flight.getFlightNumber())) { // If the flight number matches the flight number
                int quantity = mFlightReservationAmount.getText().toString().isEmpty() ? 0 : // Setting the quantity
                        Integer.parseInt(mFlightReservationAmount.getText().toString()); // Getting the quantity
                if (quantity == 0) { // If the quantity is 0
                    mReservationMessage.setText(R.string.error_invalid_booking_quantity); // Setting the message
                    mReservationMessage.setVisibility(View.VISIBLE); // Setting the visibility
                    return false; // Returning false
                } else if (quantity > (150 - flight.getCapacity())) { // If the quantity is greater than the capacity
                    mReservationMessage.setText(R.string.error_excess_booking_quantity); // Setting the message
                    mReservationMessage.setVisibility(View.VISIBLE); // Setting the visibility
                    return false; // Returning false
                }

                Booking newBooking = new Booking(mActiveReservationUser.getUserId(), flight.getFlightId(), quantity); // Setting the new booking
                mReservationFlightReserveDAO.insert(newBooking); // Inserting the new booking
                mAssertingReservationFlightDAO.updateFlightCapacity(flight.getFlightId(), quantity + flight.getCapacity()); // Updating the flight capacity
                if (flight.getCapacity() + quantity >= 150) { // If the capacity is greater than or equal to 150
                    mAssertingReservationFlightDAO.updateFlightAvailability(flight.getFlightId(), 1); // Updating the flight availability
                }
                return true; // Returning true
            }
        }
        mReservationMessage.setText(R.string.error_invalid_flight_number); // Setting the message
        mReservationMessage.setVisibility(View.VISIBLE); // Setting the visibility
        return false; // Returning false
    }

    /**
     * Abstract: This method is used to get the intent.
     *
     * @param: context - Context for the activity
     * @param: username - String for the username
     * @return: Intent - This method returns an intent
     */

    public static Intent getIntent(Context context, String username) {
        Intent intent = new Intent(context, ReservationForFlightActivity.class); // Setting the intent
        intent.putExtra(BOOK_FLIGHT_ACTIVITY_USER, username); // Putting the username in the intent
        return intent; // Returning the intent
    }
}

