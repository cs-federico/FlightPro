package com.example.flightpro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.flightpro.DB.AppDataBase;
import com.example.flightpro.DB.UserDAO;
import com.example.flightpro.databinding.ActivitySignUpBinding;

import java.util.List;

/**
 * Abstract: This class is responsible for the registration activity. It allows the user to create
 *
 * @author: Federico Marquez Murrieta
 * @since: 2023-11-29
 */

public class RegistrationActivity extends AppCompatActivity {

    private TextView mSignUpTitle, mSignUpMessageView; // TextViews that display the title and error messages
    private EditText mSignUpUsernameField, mSignUpPasswordField, mSignUpPasswordConfirmField;  // EditTexts that allow the user to enter their username and password
    private Button mSuSignUpButton, mSignUpReturnButton; // Buttons that allow the user to sign up or return to the previous activity
    private RadioButton mSignUpAdminOption, mSignUpUserOption; // RadioButtons that allow the user to select their role
    private UserDAO mUserDatabaseAccessObject; // Database access object that allows the user to access the database
    private List<User> mUserCollection; // List that stores the users in the database
    private ActivitySignUpBinding mSignUpConfirmButton; // Binding that allows the user to access the views in the layout

    /**
     * Abstract: This method is called when the activity is created
     *
     * @param: savedInstanceState
     * @return: void
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Calls the super class onCreate method
        mSignUpConfirmButton = ActivitySignUpBinding.inflate(getLayoutInflater()); // Initializes the binding
        setContentView(mSignUpConfirmButton.getRoot()); // Sets the content view to the activity_sign_up layout

        setUpDatabase(); // Sets up the database
        initializeViews(); // Initializes the views
        setListeners(); // Sets the listeners
    }

    /**
     * Abstract: This method sets up the database
     *
     * @param: none
     * @return: void
     */

    private void setUpDatabase() {
        mUserDatabaseAccessObject = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME) // Initializes the database
                .allowMainThreadQueries() // Allows the database to run on the main thread
                .build() // Builds the database
                .UserDAO(); // Initializes the database access object
    }

    /**
     * Abstract: This method initializes the views
     *
     * @param: none
     * @return: void
     */

    private void initializeViews() {
        mSignUpTitle = mSignUpConfirmButton.signUpBanner; // Initializes the title
        mSignUpMessageView = mSignUpConfirmButton.signUpMessageDisplay; // Initializes the message view
        mSignUpUsernameField = mSignUpConfirmButton.signUpUsernameEditText; // Initializes the username field
        mSignUpPasswordField = mSignUpConfirmButton.signUpPasswordEditText; // Initializes the password field
        mSignUpPasswordConfirmField = mSignUpConfirmButton.signUpPasswordConfirmEditText; // Initializes the password confirm field
        mSignUpAdminOption = mSignUpConfirmButton.isAdminRadioButton; // Initializes the admin option
        mSignUpUserOption = mSignUpConfirmButton.isUserRadioButton; // Initializes the user option
        mSuSignUpButton = mSignUpConfirmButton.suSignUpButton; // Initializes the sign up button
        mSignUpReturnButton = mSignUpConfirmButton.suBackButton; // Initializes the return button
        mSignUpMessageView.setVisibility(View.INVISIBLE); // Sets the message view to invisible
    }

    /**
     * Abstract: This method sets the listeners
     *
     * @param: none
     * @return: void
     */

    private void setListeners() { // Sets the listeners
        mSuSignUpButton.setOnClickListener(view -> performSignUp()); // Sets the sign up button listener
        mSignUpReturnButton.setOnClickListener(view -> navigateToMainScreen()); // Sets the return button listener
    }

    /**
     * Abstract: This method performs the sign up
     *
     * @param: none
     * @return: void
     */

    private void performSignUp() {
        if (isSignUpValid()) { // Checks if the sign up is valid
            User newUser = new User(mSignUpUsernameField.getText().toString(), // Creates a new user
                    mSignUpPasswordField.getText().toString(), // Sets the password
                    mSignUpAdminOption.isChecked() ? 1 : 0); // Sets the admin status
            mUserDatabaseAccessObject.insert(newUser); // Inserts the new user into the database
            navigateToMainScreen(); // Navigates to the main screen
        }
    }

    /**
     * Abstract: This method checks if the sign up is valid
     *
     * @param: none
     * @return: boolean
     */

    private boolean isSignUpValid() { // Checks if the sign up is valid
        return validatePasswords() && validateUsername() && validateUserType(); // Returns true if the passwords, username, and user type are valid
    }

    /**
     * Abstract: This method validates the passwords
     *
     * @param: none
     * @return: boolean
     */

    private boolean validatePasswords() { // Validates the passwords
        String password = mSignUpPasswordField.getText().toString(); // Gets the password
        String passwordConfirm = mSignUpPasswordConfirmField.getText().toString(); // Gets the password confirm
        if (!password.equals(passwordConfirm)) { // Checks if the passwords match
            showErrorMessage(R.string.error_password_mismatch); // Shows the error message
            return false; // Returns false
        }
        return true; // Returns true
    }

    /**
     * Abstract: This method validates the username
     *
     * @param: none
     * @return: boolean
     */

    private boolean validateUsername() { // Validates the username
        mUserCollection = mUserDatabaseAccessObject.getUsers(); // Gets the users from the database
        String username = mSignUpUsernameField.getText().toString(); // Gets the username
        for (User user : mUserCollection) { // Loops through the users
            if (user.getUsername().equals(username)) { // Checks if the username is taken
                showErrorMessage(R.string.error_username_taken); // Shows the error message
                return false; // Returns false
            }
        }
        return true; // Returns true
    }

    /**
     * Abstract: This method validates the user type
     *
     * @param: none
     * @return: boolean
     */

    private boolean validateUserType() { // Validates the user type
        if (!mSignUpAdminOption.isChecked() && !mSignUpUserOption.isChecked()) {  // Checks if the user type is selected
            showErrorMessage(R.string.error_role_not_selected); // Shows the error message
            return false; // Returns false
        }
        return true; // Returns true
    }

    /**
     * Abstract: This method shows the error message
     *
     * @param: messageId
     * @return: void
     */

    private void showErrorMessage(int messageId) { // Shows the error message
        mSignUpMessageView.setText(messageId); // Sets the message view text
        mSignUpMessageView.setVisibility(View.VISIBLE); // Sets the message view to visible
    }

    /**
     * Abstract: This method navigates to the main screen
     *
     * @param: none
     * @return: void
     */

    private void navigateToMainScreen() { // Navigates to the main screen
        startActivity(MainScreenActivity.getIntent(getApplicationContext())); // Starts the main screen activity
    }

    /**
     * Abstract: This method gets the intent
     *
     * @param: context
     * @return: Intent
     */

    public static Intent getIntent(Context context) { // Gets the intent
        return new Intent(context, RegistrationActivity.class); // Returns the intent
    }
}
