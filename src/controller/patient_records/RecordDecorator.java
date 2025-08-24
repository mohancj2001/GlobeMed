package controller.patient_records;

import javax.swing.table.DefaultTableModel;

class RecordDecorator implements PatientRecordAccess {
    protected PatientRecordAccess wrappedRecord;

    public RecordDecorator(PatientRecordAccess wrappedRecord) {
        this.wrappedRecord = wrappedRecord;
    }

    @Override
    public DefaultTableModel getRecord(String role) {
        return wrappedRecord.getRecord(role);
    }
}