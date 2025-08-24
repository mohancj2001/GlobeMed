package controller.patient_records;

import javax.swing.table.DefaultTableModel;
import java.util.Date;

public class DatabasePatientRecord implements PatientRecordAccess {
    private int patientId;
    private Date fromDate;
    private Date toDate;

    public DatabasePatientRecord(int patientId) {
        this.patientId = patientId;
    }

    public DatabasePatientRecord(int patientId, Date fromDate, Date toDate) {
        this.patientId = patientId;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    public DefaultTableModel getRecord(String role) {
        return PatientRecordDAO.getAllRecordsTable(patientId, fromDate, toDate);
    }
}