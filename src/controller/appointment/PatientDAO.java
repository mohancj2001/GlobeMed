package controller.appointment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.MySQL;

public class PatientDAO {
    
    public Patient getPatientById(int patientId) {
        Patient patient = null;
        String sql = "SELECT * FROM patients WHERE patient_id = " + patientId;

        try {
            ResultSet rs = MySQL.execute(sql);
            if (rs != null && rs.next()) {
                patient = new Patient(
                        rs.getInt("patient_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("dob"),
                        rs.getInt("gender_gender_id"),
                        rs.getString("contact_number"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patient;
    }
    
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients";
        
        try {
            ResultSet rs = MySQL.execute(sql);
            if (rs != null) {
                while (rs.next()) {
                    Patient patient = new Patient(
                            rs.getInt("patient_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getDate("dob"),
                            rs.getInt("gender_gender_id"),
                            rs.getString("contact_number"),
                            rs.getString("email"),
                            rs.getString("address"),
                            rs.getTimestamp("created_at")
                    );
                    patients.add(patient);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return patients;
    }
    
    public List<Patient> searchPatients(Integer patientId, String firstName, String lastName, String mobile) {
        List<Patient> patients = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM patients WHERE 1=1");
        
        if (patientId != null) {
            sql.append(" AND patient_id = ").append(patientId);
        }
        if (firstName != null && !firstName.trim().isEmpty()) {
            sql.append(" AND first_name LIKE '%").append(firstName.trim()).append("%'");
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            sql.append(" AND last_name LIKE '%").append(lastName.trim()).append("%'");
        }
        if (mobile != null && !mobile.trim().isEmpty()) {
            sql.append(" AND contact_number LIKE '%").append(mobile.trim()).append("%'");
        }
        
        try {
            ResultSet rs = MySQL.execute(sql.toString());
            if (rs != null) {
                while (rs.next()) {
                    Patient patient = new Patient(
                            rs.getInt("patient_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getDate("dob"),
                            rs.getInt("gender_gender_id"),
                            rs.getString("contact_number"),
                            rs.getString("email"),
                            rs.getString("address"),
                            rs.getTimestamp("created_at")
                    );
                    patients.add(patient);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return patients;
    }
}