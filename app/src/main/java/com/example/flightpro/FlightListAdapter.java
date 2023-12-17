package com.example.flightpro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.room.Room;

import com.example.flightpro.DB.AppDataBase;
import com.example.flightpro.DB.BookingDAO;
import com.example.flightpro.DB.FlightDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract: This class is used to create a Flight List Adapter for the list view
 *
 * @author: Federico Marquez Murrieta
 * @since: 2023-11-29
 */

public class FlightListAdapter extends BaseAdapter {
    private ArrayList<String> mFlightRecords; // ArrayList that stores the flight records
    private Context mContextEnvironment;    // Context that stores the environment

    public FlightListAdapter(ArrayList<String> data, Context context) { // Constructor
        this.mFlightRecords = data; // Initializes the flight records
        this.mContextEnvironment = context; // Initializes the context
    }

    /**
     * Abstract: This method returns the number of items in the list
     *
     * @param: none
     * @return: int
     */

    @Override
    public int getCount() {
        return mFlightRecords.size();
    }

    /**
     * Abstract: This method returns the item at the specified position
     *
     * @param: int
     * @return: Object
     */

    @Override
    public Object getItem(int position) {
        return mFlightRecords.get(position);
    }

    /**
     * Abstract: This method returns the item ID at the specified position
     *
     * @param: int
     * @return: long
     */

    @Override
    public long getItemId(int position) {
        // As IDs are not used in the list items
        return 0;
    }

    /**
     * Abstract: This method returns the view at the specified position
     *
     * @param: int, View, ViewGroup
     * @return: View
     */

    @Override
    public View getView(final int position, View recycledView, ViewGroup parent) {
        View listItemView = recycledView; // Initializes the listItemView
        if (listItemView == null) { // Checks if the listItemView is null
            LayoutInflater layoutInflater = (LayoutInflater) mContextEnvironment.getSystemService(Context.LAYOUT_INFLATER_SERVICE); // Initializes the layout inflater
            listItemView = layoutInflater.inflate(R.layout.custom_layout, null); // Inflates the custom layout
        }

        TextView mDisplayText = listItemView.findViewById(R.id.textViewContent); // Gets the text view from the layout
        mDisplayText.setText(mFlightRecords.get(position)); // Sets the text view to the flight record

        Button mActionButton = listItemView.findViewById(R.id.btn); // Gets the button from the layout
        mActionButton.setOnClickListener(view -> processBooking(position)); // Sets an onClickListener to the button

        return listItemView; // Returns the listItemView
    }

    /**
     * Abstract: This method processes the booking
     *
     * @param: int
     * @return: void
     */

    private void processBooking(int position) {
        String mFlightDetail = mFlightRecords.get(position); // Gets the flight detail
        Matcher mDetailMatcher = Pattern.compile("\\d+").matcher(mFlightDetail); // Creates a matcher for the flight detail
        mDetailMatcher.find(); // Finds the flight detail
        int mReservationId = Integer.parseInt(mDetailMatcher.group()); // Parses the flight detail to an integer

        AppDataBase appDatabase = Room.databaseBuilder(mContextEnvironment, AppDataBase.class, AppDataBase.DATABASE_NAME) // Initializes the database
                .allowMainThreadQueries() // Allows main thread queries
                .build(); // Builds the database
        BookingDAO mReservationAcessObject = appDatabase.BookingDAO(); // Initializes the booking access object
        FlightDAO mFlightDAO = appDatabase.FlightDAO(); // Initializes the flight access object

        List<Booking> mAllReservations = mReservationAcessObject.getBookings(); // Gets all the reservations
        List<Flight> mEveryFlight = mFlightDAO.getFlights(); // Gets all the flights
        updateFlightDetails(mReservationId, mAllReservations, mEveryFlight, mFlightDAO, mReservationAcessObject); // Updates the flight details
    }

    /**
     * Abstract: This method updates the flight details
     *
     * @param: int, List<Booking>, List<Flight>, FlightDAO, BookingDAO
     * @return: void
     */

    private void updateFlightDetails(int bookingId, List<Booking> bookings, List<Flight> flights, FlightDAO flightDAO, BookingDAO bookingDAO) {
        for (Booking booking : bookings) { // Loops through the bookings
            if (booking.getBookingId() == bookingId) { // Checks if the booking ID matches the booking ID
                int mSeatTotal = getUpdatedSeatCount(booking, flights); // Gets the updated seat count
                flightDAO.updateFlightCapacity(booking.getFlightId(), mSeatTotal); // Updates the flight capacity
                if (mSeatTotal < 150) { // Checks if the seat total is less than 150
                    flightDAO.updateFlightAvailability(booking.getFlightId(), 0); // Updates the flight availability
                }
                bookingDAO.delete(booking); // Deletes the booking
                break; // Breaks out of the loop
            }
        }
    }

    /**
     * Abstract: This method gets the updated seat count
     *
     * @param: Booking, List<Flight>
     * @return: int
     */

    private int getUpdatedSeatCount(Booking booking, List<Flight> flights) { // Gets the updated seat count
        for (Flight flight : flights) { // Loops through the flights
            if (booking.getFlightId() == flight.getFlightId()) { // Checks if the flight ID matches the flight ID
                return flight.getCapacity() - booking.getQuantity(); // Returns the updated seat count
            }
        }
        return 0;
    }
}
