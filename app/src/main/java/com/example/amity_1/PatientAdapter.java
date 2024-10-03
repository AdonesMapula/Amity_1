package com.example.amity_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> implements Filterable {
    private List<Patient> patientList; // Original list
    private List<Patient> patientListFull; // Copy of original list
    private Context context;

    public PatientAdapter(List<Patient> patientList, Context context) {
        this.patientList = patientList;
        this.context = context;
        this.patientListFull = new ArrayList<>(patientList); // Create a copy for filtering
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_item, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient currentPatient = patientList.get(position);
        holder.bind(currentPatient);
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    @Override
    public Filter getFilter() {
        return new PatientFilter();
    }

    private class PatientFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Patient> filteredPatients = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredPatients.addAll(patientListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Patient patient : patientListFull) {
                    if (patient.getName().toLowerCase().contains(filterPattern) ||
                            patient.getPhoneNumber().toLowerCase().contains(filterPattern) ||
                            patient.getAddress().toLowerCase().contains(filterPattern)) {
                        filteredPatients.add(patient);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredPatients;
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            patientList.clear();
            patientList.addAll((List<Patient>) results.values);
            notifyDataSetChanged();
        }
    }

    static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView patientNameTextView;
        TextView patientPhoneTextView;
        TextView patientAddressTextView;
        TextView checkupDateTextView;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            patientNameTextView = itemView.findViewById(R.id.patientNameTextView);
            patientPhoneTextView = itemView.findViewById(R.id.patientPhoneTextView);
            patientAddressTextView = itemView.findViewById(R.id.patientAddressTextView);
            checkupDateTextView = itemView.findViewById(R.id.checkupDateTextView);
        }

        public void bind(Patient patient) {
            patientNameTextView.setText(patient.getName());
            patientPhoneTextView.setText(patient.getPhoneNumber());
            patientAddressTextView.setText(patient.getAddress());
            checkupDateTextView.setText(patient.getCheckupDate());
        }
    }
}
