package controller.patient_records;

import model.MySQL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class PatientRecordDAO {

//    public static List<String> getAllRecordsByPatientId(int patientId) {
//        List<String> records = new ArrayList<>();
//        try {
//            String query = "SELECT CONCAT(p.patient_id, ' ', p.first_name, ' ', p.last_name, ' | ', r.medical_history, ' | ', r.treatment, ' | ', r.record_datetime) AS record " +
//                    "FROM patients p " +
//                    "JOIN patient_records r ON p.patient_id = r.patients_patient_id " +
//                    "WHERE p.patient_id = " + patientId + " " +
//                    "ORDER BY r.record_datetime DESC";
//
//            ResultSet rs = MySQL.execute(query);
//            while (rs != null && rs.next()) {
//                records.add(rs.getString("record"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return records;
    
    
    public static DefaultTableModel getAllRecordsTable(int patientId) {
        String[] columnNames = {"Record ID", "Medical History", "Treatment", "Date"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try {
            String query = "SELECT r.record_id, r.medical_history, r.treatment, r.record_datetime "
                    + "FROM patient_records r "
                    + "WHERE r.patients_patient_id = " + patientId + " "
                    + "ORDER BY r.record_datetime DESC";

            ResultSet rs = MySQL.execute(query);
            while (rs != null && rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("record_id"));
                row.add(rs.getString("medical_history"));
                row.add(rs.getString("treatment"));
                row.add(rs.getTimestamp("record_datetime"));
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return model;

    }
}
