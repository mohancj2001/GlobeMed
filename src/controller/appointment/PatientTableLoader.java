/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.appointment;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;
import java.util.List;

/**
 *
 * @author mohan
 */
public class PatientTableLoader {

    private PatientDAO patientDAO;

    public PatientTableLoader(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
    }

    // Method to load all patients
    public DefaultTableModel loadPatientsToTableModel() {
        return loadFilteredPatients(null, null, null, null);
    }

    // Method to load filtered patients
    public DefaultTableModel loadFilteredPatients(Integer patientId, String firstName,
            String lastName, String mobile) {
        // Define column names
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Patient ID");
        columnNames.add("First Name");
        columnNames.add("Last Name");
        columnNames.add("Date of Birth");
        columnNames.add("Contact Number");
        columnNames.add("Email");
        columnNames.add("Address");

        // Create empty table model
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Get filtered patients from DAO
        List<Patient> patients = patientDAO.searchPatients(patientId, firstName, lastName, mobile);

        // Add patients to the model
        for (Patient patient : patients) {
            Vector<Object> row = new Vector<>();
            row.add(patient.getPatientId());
            row.add(patient.getFirstName());
            row.add(patient.getLastName());
            row.add(patient.getDob());
            row.add(patient.getContactNumber());
            row.add(patient.getEmail());
            row.add(patient.getAddress());

            model.addRow(row);
        }

        return model;
    }
}
