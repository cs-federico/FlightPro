package com.example.flightpro;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Abstract: This activity displays the benefits of flying.
 *
 * @author: Federico Marquez Murrieta
 * @since: 2023-11-29
 */
public class BenefitsOfFlyingActivity extends AppCompatActivity {

    private RecyclerView recyclerView; // RecyclerView that displays the benefits
    private BenefitsAdapter adapter; // Adapter that allows the user to access the benefits

    /**
     * Abstract: This method is called when the activity is created
     *
     * @param: savedInstanceState
     * @return: void
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Called when the activity is created
        super.onCreate(savedInstanceState); // Calls the super class onCreate method
        setContentView(R.layout.activity_benefits_of_flying); // Sets the content view to the activity_benefits_of_flying layout

        recyclerView = findViewById(R.id.recyclerViewBenefits); // Initializes the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Sets the layout manager for the RecyclerView

        String[] benefits = { // Initializes the benefits
                getString(R.string.benefit_time_efficiency),
                getString(R.string.benefit_safety),
                getString(R.string.benefit_comfort),
                getString(R.string.benefit_environmental_impact),
                getString(R.string.benefit_remote_access),
                getString(R.string.benefit_global_connectivity),
                getString(R.string.benefit_travel_fatigue),
                getString(R.string.benefit_business_efficiency),
                getString(R.string.benefit_healthcare_services),
                getString(R.string.benefit_scenic_views)

        };

        adapter = new BenefitsAdapter(benefits); // Initializes the adapter
        recyclerView.setAdapter(adapter); // Sets the adapter for the RecyclerView

        // Setup the return button
        Button btnReturnToLanding = findViewById(R.id.btnReturnToLanding); // Initializes the return button
        btnReturnToLanding.setOnClickListener(new View.OnClickListener() { // Sets the listener for the return button
            @Override
            public void onClick(View v) { // Called when the return button is clicked
                finish(); // Close this activity and return to LandingActivity
            }
        });
    }
}
