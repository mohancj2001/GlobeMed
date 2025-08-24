package controller.patient_records;

import javax.swing.table.DefaultTableModel;

public class DatabasePatientRecord implements PatientRecordAccess {
    private int patientId;

    public DatabasePatientRecord(int patientId) {
        this.patientId = patientId;
    }

    @Override
    public DefaultTableModel getRecord(String role) {
        return PatientRecordDAO.getAllRecordsTable(patientId);
    }
}