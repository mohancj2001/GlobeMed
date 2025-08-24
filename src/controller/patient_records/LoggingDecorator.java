package controller.patient_records;

import javax.swing.table.DefaultTableModel;

public class LoggingDecorator extends RecordDecorator {
    public LoggingDecorator(PatientRecordAccess wrappedRecord) {
        super(wrappedRecord);
    }

    @Override
    public DefaultTableModel getRecord(String role) {
        System.out.println("[LOG] " + role + " accessed patient records at " + java.time.LocalDateTime.now());
        return super.getRecord(role);
    }
}