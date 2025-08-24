package controller.patient_records;

import model.MySQL;
import java.sql.ResultSet;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class PatientRecordDAO {

    public static DefaultTableModel getAllRecordsTable(int patientId) {
        return getAllRecordsTable(patientId, null, null);
    }

    public static DefaultTableModel getAllRecordsTable(int patientId, Date fromDate, Date toDate) {
        String[] columnNames = {"Record ID", "Medical History", "Treatment", "Date"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try {
            String query = "SELECT r.record_id, r.medical_history, r.treatment, r.record_datetime "
                    + "FROM patient_records r "
                    + "WHERE r.patients_patient_id = " + patientId;
            
            // Add date filtering if dates are provided
            if (fromDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                query += " AND DATE(r.record_datetime) >= '" + sdf.format(fromDate) + "'";
            }
            
            if (toDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                query += " AND DATE(r.record_datetime) <= '" + sdf.format(toDate) + "'";
            }
            
            query += " ORDER BY r.record_datetime DESC";

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