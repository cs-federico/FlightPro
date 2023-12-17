package com.example.flightpro;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Abstract: This class is the activity that displays the places to visit in the city
 *
 * @author: Federico Marquez Murrieta
 * @since: 2023-11-29
 */

public class VisitPlacesActivity extends AppCompatActivity {

    TextView mPlacesText; // TextView that displays the places to visit
    Button returnButton; // Button that returns to the previous activity

    /**
     * Abstract: This method is called when the activity is created
     *
     * @param: savedInstanceState
     * @return: void
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) { // onCreate method
        super.onCreate(savedInstanceState); // Calls the super class onCreate method
        setContentView(R.layout.activity_visit_places); // Sets the content view to the activity_visit_places layout

        returnButton = findViewById(R.id.returnButton); // Gets the returnButton from the layout

        returnButton.setOnClickListener(new View.OnClickListener() { // Sets an onClickListener to the returnButton

            @Override
            public void onClick(View v) { // onClick method
                finish(); // Closes this activity and returns to the previous one
            }
        });
    }
}
