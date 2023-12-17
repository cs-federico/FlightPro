package com.example.flightpro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Abstract:  This class is used to create a Benefits Adapter for the list view
 *
 * @author: Federico Marquez Murrieta
 * @since: 2023-11-29
 */

public class BenefitsAdapter extends RecyclerView.Adapter<BenefitsAdapter.ViewHolder> {

    private String[] benefits; // String array that stores the benefits

    public BenefitsAdapter(String[] benefits) {
        this.benefits = benefits;
    } // Constructor

    /**
     * Abstract: This method returns the number of items in the list
     *
     * @param: none
     * @return: int
     */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { // Called when the view holder is created
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.benefit_item, parent, false); // Initializes the view
        return new ViewHolder(view); // Returns the view holder
    }

    /**
     * Abstract: This method returns the item at the specified position
     *
     * @param: int
     * @return: Object
     */

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) { // Called when the view holder is bound
        holder.benefitText.setText(benefits[position]); // Sets the text for the benefit
    }

    /**
     * Abstract: This method returns the number of items in the list
     *
     * @param: none
     * @return: int
     */

    @Override
    public int getItemCount() {
        return benefits.length;
    } // Returns the number of items in the list

    /**
     * Abstract: This class is used to create a view holder for the benefits
     *
     * @param: none
     * @return: int
     */

    public static class ViewHolder extends RecyclerView.ViewHolder { // This class is used to create a view holder for the benefits
        TextView benefitText; // TextView that stores the benefit text

        public ViewHolder(View view) { // Constructor
            super(view);
            benefitText = view.findViewById(R.id.benefit_text); // Initializes the benefit text
        }
    }
}
