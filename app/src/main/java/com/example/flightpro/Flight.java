package com.example.flightpro;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.flightpro.DB.AppDataBase;

/**
 * Abstract: This class is the Flight class. It is used to create a flight object.
 *
 * @author: Federico Marquez Murrieta
 * @since: 2023-11-29
 */

@Entity(tableName = AppDataBase.FLIGHT_TABLE)
// This annotation is used to create a table in the database
public class Flight { // This class is used to create a flight object
    @PrimaryKey(autoGenerate = true)
    // This annotation is used to create a primary key for the table
    private int mFlightId; // This variable is used to store the flight id
    private String mFlightNumber; // This variable is used to store the flight number
    private String mOrigin; // This variable is used to store the origin
    private String mDestination; // This variable is used to store the destination
    private int mCapacity;  // This variable is used to store the capacity
    private int mIsFull; // This variable is used to store the availability

    public Flight(String flightNumber, String origin, String destination) { // This constructor is used to create a flight object
        mFlightNumber = flightNumber; // This line is used to set the flight number
        mOrigin = origin; // This line is used to set the origin
        mDestination = destination; // This line is used to set the destination
        mCapacity = 0; // This line is used to set the capacity
        mIsFull = 0; // This line is used to set the availability
    }

    // Getters and Setters
    public int getFlightId() {
        return mFlightId;
    }

    public void setFlightId(int flightId) {
        mFlightId = flightId;
    }

    public String getFlightNumber() {
        return mFlightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        mFlightNumber = flightNumber;
    }

    public String getOrigin() {
        return mOrigin;
    }

    public void setOrigin(String origin) {
        mOrigin = origin;
    }

    public String getDestination() {
        return mDestination;
    }

    public void setDestination(String destination) {
        mDestination = destination;
    }

    public int getCapacity() {
        return mCapacity;
    }

    public void setCapacity(int capacity) {
        mCapacity = capacity;
    }

    public int getIsFull() {
        return mIsFull;
    }

    public void setIsFull(int isFull) {
        mIsFull = isFull;
    }

    // toString method
    @Override
    public String toString() {
        String availability = mIsFull == 1 ? "Not Available" : "Available"; // This line is used to set the availability
        return "Flight ID: " + mFlightId + "\n" // This line is used to return a string representation of the flight object
                + "Flight Number: " + mFlightNumber + "\n"  // This line is used to return a string representation of the flight object
                + "Origin: " + mOrigin + "\n" // This line is used to return a string representation of the flight object
                + "Destiny: " + mDestination + "\n"     // This line is used to return a string representation of the flight object
                + "Seats Taken: " + mCapacity + " of 150 Seats\n" // This line is used to return a string representation of the flight object
                + "Availability: " + availability + "\n" // This line is used to return a string representation of the flight object
                + "================================\n"; // This line is used to return a string representation of the flight object
    }
}
