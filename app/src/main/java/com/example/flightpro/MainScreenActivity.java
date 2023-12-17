package com.example.flightpro;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.example.flightpro.DB.AppDataBase;
import com.example.flightpro.DB.FlightDAO;
import com.example.flightpro.DB.UserDAO;
import com.example.flightpro.databinding.ActivityMainBinding;

/**
 * Abstract: This class is the main screen activity
 *
 * @author: Federico Marquez Murrieta
 * @since: 2023-11-29
 */


public class MainScreenActivity extends AppCompatActivity {


    private static final String MAIN_ACTIVITY_USER = "com.example.flightpro.MainScreenActivityUser"; // String that stores the key for the user object

    TextView mLoginHeader; // TextView that displays the title
    TextView mDisplayMessage; // TextView that displays error messages
    EditText mInputUsername; // EditTexts that allow the user to enter their username and password
    EditText mInputPassword; // EditTexts that allow the user to enter their username and password

    Button mSignInButton; // Buttons that allow the user to sign in or return to the previous activity
    Button mRegisterButton; // Buttons that allow the user to sign in or return to the previous activity

    UserDAO mUserDataAccess; // Database access object that allows the user to access the database

    User mUserProfile; // User object that stores the user's information

    ActivityMainBinding mViewBinding; // Binding that allows the user to access the views in the layout

    /**
     * Abstract: This method is called when the activity is created
     *
     * @param: savedInstanceState
     * @return: void
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) { // onCreate method
        super.onCreate(savedInstanceState); // Calls the super class onCreate method
        setContentView(R.layout.activity_main); // Sets the content view to the activity_main layout

        mViewBinding = ActivityMainBinding.inflate(getLayoutInflater()); // Initializes the binding
        setContentView(mViewBinding.getRoot()); // Sets the content view to the activity_main layout

        mLoginHeader = mViewBinding.mainSignInBanner; // Initializes the views
        mDisplayMessage = mViewBinding.mainMessageDisplay; // Initializes the views
        mDisplayMessage.setVisibility(View.INVISIBLE); // Initializes the views
        mInputUsername = mViewBinding.mainUsernameEditText; // Initializes the views
        mInputPassword = mViewBinding.mainPasswordEditText; // Initializes the views
        mSignInButton = mViewBinding.mainLogInButton; // Initializes the views
        mRegisterButton = mViewBinding.mainSignUpButton; // Initializes the views

        mUserDataAccess = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME) // Initializes the database
                .allowMainThreadQueries() // Allows the database to run on the main thread
                .build() // Builds the database
                .UserDAO(); // Initializes the database access object

        addPredefinedUsers(); // Adds the predefined users to the database

        mSignInButton.setOnClickListener(view -> LogIn()); // Sets an onClickListener to the signInButton

        mRegisterButton.setOnClickListener(view -> {
            Intent intent = RegistrationActivity.getIntent(getApplicationContext()); // Creates an intent to go to the registration activity
            startActivity(intent); // Starts the registration activity
        });

        setUpPasswordVisibilityToggle(); // Sets up the password visibility toggle
        createNotificationChannel(); // Creates the notification channel
        addPredefinedFlights(); // Adds the predefined flights to the database
    }

    /**
     * Abstract: This method creates the notification channel
     *
     * @param: none
     * @return: void
     */

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Checks if the API level is greater than or equal to 26
            CharSequence name = getString(R.string.channel_name); // Initializes the name
            String description = getString(R.string.channel_description); // Initializes the description
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("LOGIN_CHANNEL", name, importance); // Initializes the notification channel
            channel.setDescription(description); // Sets the description
            NotificationManager notificationManager = getSystemService(NotificationManager.class); // Initializes the notification manager
            notificationManager.createNotificationChannel(channel); // Creates the notification channel
        }
    }

    /**
     * Abstract: This method sends the login notification
     *
     * @param: username
     * @return: void
     */

    private void sendLoginNotification(String username) {
        try { // Tries to send the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "LOGIN_CHANNEL")  // Initializes the notification builder
                    .setSmallIcon(R.drawable.ic_notification) // Sets the icon
                    .setContentTitle("Login Successful") // Sets the title
                    .setContentText("Welcome back, " + username + "!") // Sets the text
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT); // Sets the priority

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this); // Initializes the notification manager
            notificationManager.notify(1, builder.build()); // Sends the notification
        } catch (SecurityException e) { // Catches the security exception
            e.printStackTrace(); // Prints the stack trace
        }
    }

    /**
     * Abstract: This method sets up the password visibility toggle
     *
     * @param: none
     * @return: void
     */
    private void setUpPasswordVisibilityToggle() {
        mInputPassword.setOnTouchListener((v, event) -> { // Sets an onTouchListener to the password field
            final int DRAWABLE_RIGHT = 2; // Initializes the drawable right
            if (event.getAction() == MotionEvent.ACTION_UP) { // Checks if the action is up
                if (event.getRawX() >= (mInputPassword.getRight() - mInputPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) { // Checks if the drawable is clicked
                    if (mInputPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) { // Checks if the password is hidden
                        mInputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD); // Sets the password to visible
                        mInputPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_visibility), null); // Sets the drawable
                    } else {
                        mInputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); // Sets the password to hidden
                        mInputPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_visibility_off), null); // Sets the drawable
                    }
                    return true; // Returns true
                }
            }
            return false; // Returns false
        });
    }

    /**
     * Abstract: This method logs the user in
     *
     * @param: none
     * @return: void
     */

    private void LogIn() { // LogIn method
        String username = mInputUsername.getText().toString(); // Gets the username
        String password = mInputPassword.getText().toString(); // Gets the password
        mUserProfile = mUserDataAccess.getUserByUsernameAndPassword(username, password); // Gets the user profile
        if (mUserProfile != null) { // Checks if the user profile is not null

            sendLoginNotification(mUserProfile.getUsername()); // Sends the login notification

            Toast.makeText(this, R.string.success_login_message, Toast.LENGTH_SHORT).show(); // Shows the success message

            Intent intent = LandingActivity.getIntent(getApplicationContext(), username); // Creates an intent to go to the landing activity
            startActivity(intent); // Starts the landing activity
        } else {
            mDisplayMessage.setText(R.string.error_invalid_credentials); // Sets the error message
            mDisplayMessage.setVisibility(View.VISIBLE); // Sets the error message to visible
        }
    }

    /**
     * Abstract: This method adds the predefined users to the database
     *
     * @param: none
     * @return: void
     */


    private void addPredefinedUsers() {
        User testUser1 = mUserDataAccess.getUserByUsername("testuser1"); // Gets the user
        User admin2 = mUserDataAccess.getUserByUsername("admin2"); // Gets the user

        if (testUser1 == null) { // Checks if the user is null
            testUser1 = new User("testuser1", "testuser1", 0); // Initializes the user
            mUserDataAccess.insert(testUser1); // Inserts the user
        }

        if (admin2 == null) { // Checks if the user is null
            admin2 = new User("admin2", "admin2", 1); // 1 indicates admin
            mUserDataAccess.insert(admin2);     // Initializes the user
        }
    }

    /**
     * Abstract: This method adds the predefined flights to the database
     *
     * @param: none
     * @return: void
     */

    private void addPredefinedFlights() { // Adds the predefined flights to the database
        FlightDAO flightDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME) // Initializes the database
                .allowMainThreadQueries() // Allows the database to run on the main thread
                .build() // Builds the database
                .FlightDAO(); // Initializes the database access object

        // Define predefined flights
        Flight[] flights = new Flight[]{
                new Flight("FL101", "San Francisco, USA", "Kyoto, Japan"),
                new Flight("FL102", "Oakland, USA", "Yucatan, Mexico"),
                new Flight("FL103", "Los Angeles, USA", "Bozeman, USA"),
                new Flight("FL104", "San Diego, USA", "Honolulu, Hawaii"),
                new Flight("FL105", "Los Angeles, USA", "Paris, France"),
                new Flight("FL106", "San Jose, USA", "Queenstown, New Zealand"),
                new Flight("FL107", "Los Angeles, USA", "Ibiza, Spain"),
                new Flight("FL108", "San Diego, USA", "Capetown, South Africa"),
                new Flight("FL109", "San Francisco, USA", "Istanbul, Turkey"),
                new Flight("FL110", "Los Angeles, USA", "Dubrovnik, Croatia")
        };

        // Check and insert flights
        for (Flight flight : flights) {     // Loops through the flights
            if (flightDAO.getFlightByFlightNumber(flight.getFlightNumber()) == null) {
                flightDAO.insert(flight); // Insert the flight only if it doesn't exist
            }
        }
    }

    /**
     * Abstract: This method navigates to the main screen
     *
     * @param: none
     * @return: void
     */

    public static Intent getIntent(Context context) { // Gets the intent
        return new Intent(context, MainScreenActivity.class); // Returns the intent
    }
}
