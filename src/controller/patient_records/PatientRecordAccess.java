package controller.patient_records;

import javax.swing.table.DefaultTableModel;

public interface PatientRecordAccess {
    DefaultTableModel getRecord(String role);
}