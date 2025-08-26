package controller.patient_records;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class EncryptionDecorator extends RecordDecorator {
    public EncryptionDecorator(PatientRecordAccess wrappedRecord) {
        super(wrappedRecord);
    }

    @Override
    public DefaultTableModel getRecord(String role) {
        DefaultTableModel model = super.getRecord(role);
        
        if ("Doctor".equalsIgnoreCase(role) ||
            "Nurse".equalsIgnoreCase(role) ||
            "Admin".equalsIgnoreCase(role)) {
            return model; 
        } else {
            return encryptTableModel(model);
        }
    }

    private DefaultTableModel encryptTableModel(DefaultTableModel model) {
       
        Vector<String> columnNames = new Vector<>();
        for (int i = 0; i < model.getColumnCount(); i++) {
            columnNames.add(model.getColumnName(i));
        }
        
        DefaultTableModel encryptedModel = new DefaultTableModel(columnNames, 0);

        for (int i = 0; i < model.getRowCount(); i++) {
            Vector<Object> row = new Vector<>();
            for (int j = 0; j < model.getColumnCount(); j++) {
                Object value = model.getValueAt(i, j);
                if (value instanceof String) {
                    row.add(encrypt((String) value));
                } else {
                    row.add(value);
                }
            }
            encryptedModel.addRow(row);
        }
        return encryptedModel;
    }

    private String encrypt(String data) {
        return new StringBuilder(data).reverse().toString();
    }
    
    private String decrypt(String encrypted) {
        return new StringBuilder(encrypted).reverse().toString();
    }
}