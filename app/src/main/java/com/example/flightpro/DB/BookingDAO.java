package com.example.flightpro.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.flightpro.Booking;

import java.util.List;


@Dao
public interface BookingDAO {
    @Insert
    void insert(Booking...bookings);

    @Update
    void update(Booking...bookings);

    @Delete
    void delete(Booking booking);

    @Query("SELECT * FROM " + AppDataBase.BOOKING_TABLE)
    List<Booking> getBookings();

    @Query("SELECT * FROM " + AppDataBase.BOOKING_TABLE + " WHERE mUserId = :userId")
    List<Booking> getBookingsByUserId(int userId);
}
