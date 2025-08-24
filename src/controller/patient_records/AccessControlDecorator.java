package controller.patient_records;

import javax.swing.table.DefaultTableModel;
import java.util.Collections;

public class AccessControlDecorator extends RecordDecorator {
    public AccessControlDecorator(PatientRecordAccess wrappedRecord) {
        super(wrappedRecord);
    }

    @Override
    public DefaultTableModel getRecord(String role) {
        if ("Doctor".equalsIgnoreCase(role) ||
                "Nurse".equalsIgnoreCase(role) ||
                "Admin".equalsIgnoreCase(role)) {
            return super.getRecord(role);
        } else {
            // Return empty table model for unauthorized access
            String[] columnNames = {"Record ID", "Medical History", "Treatment", "Date"};
            return new DefaultTableModel(columnNames, 0);
        }
    }
}