package com.example.flightpro;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.flightpro.DB.AppDataBase;

/**
 * Abstract: This class is the Booking class. It is used to create a booking object.
 *
 * @author: Federico Marquez Murrieta
 * @since: 2023-11-29
 */

@Entity(tableName = AppDataBase.BOOKING_TABLE)
// This annotation is used to create a table in the database
public class Booking { // This class is used to create a booking object
    @PrimaryKey(autoGenerate = true)
    // This annotation is used to create a primary key for the table
    private int mBookingId; // This variable is used to store the booking id
    private int mUserId; // This variable is used to store the user id
    private int mFlightId; // This variable is used to store the flight id
    private int mQuantity; // This variable is used to store the quantity


    public Booking(int userId, int flightId, int quantity) { // This constructor is used to create a booking object
        mUserId = userId; // This line is used to set the user id
        mFlightId = flightId; // This line is used to set the flight id
        mQuantity = quantity; // This line is used to set the quantity
    }

    // Getters and Setters

    public int getBookingId() {
        return mBookingId;
    }

    public void setBookingId(int bookingId) {
        mBookingId = bookingId;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public int getFlightId() {
        return mFlightId;
    }

    public void setFlightId(int flightId) {
        mFlightId = flightId;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }

    @Override
    public String toString() { // This method is used to return the booking object as a string
        return "Booking{" +     // This line is used to return the booking object as a string
                "mBookingId=" + mBookingId + // This line is used to return the booking object as a string
                ", mUserId=" + mUserId + // This line is used to return the booking object as a string
                ", mFlightId=" + mFlightId + // This line is used to return the booking object as a string
                ", mQuantity=" + mQuantity + // This line is used to return the booking object as a string
                '}';
    }
}
