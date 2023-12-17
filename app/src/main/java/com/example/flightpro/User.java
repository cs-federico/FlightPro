package com.example.flightpro;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.flightpro.DB.AppDataBase;

/**
 * Abstract: This class is the User class. It is used to create a user object.
 *
 * @author: Federico Marquez Murrieta
 * @since: 2023-11-29
 */


@Entity(tableName = AppDataBase.USER_TABLE)
// This annotation is used to create a table in the database
public class User { // This class is used to create a user object

    @PrimaryKey(autoGenerate = true)
    // This annotation is used to create a primary key for the table
    private int mUserId; // This variable is used to store the user id
    private String mUsername; // This variable is used to store the username
    private String mPassword; // This variable is used to store the password
    private int mIsAdmin; // This variable is used to store the admin status

    public User(String username, String password, int isAdmin) { // This constructor is used to create a user object
        this.mUsername = username; // This line is used to set the username
        this.mPassword = password; // This line is used to set the password
        this.mIsAdmin = isAdmin; // This line is used to set the admin status
    }

    /**
     * Abstract: This method is used to return a string representation of the user object
     *
     * @return: String
     */

    @Override
    public String toString() {
        return String.format("User{mUserId='%d', mUsername='%s', mPassword='%s', mIsAdmin=%d}",
                mUserId, mUsername, mPassword, mIsAdmin);
    }

    // Getters and Setters
    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        this.mUserId = userId;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public int getIsAdmin() {
        return mIsAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.mIsAdmin = isAdmin;
    }
}