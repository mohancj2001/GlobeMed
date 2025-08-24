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
        
        // For authorized roles, return decrypted data. For others, return encrypted.
        if ("Doctor".equalsIgnoreCase(role) ||
            "Nurse".equalsIgnoreCase(role) ||
            "Admin".equalsIgnoreCase(role)) {
            return model; // Return as-is (decrypted) for authorized roles
        } else {
            return encryptTableModel(model); // Encrypt for unauthorized roles
        }
    }

    private DefaultTableModel encryptTableModel(DefaultTableModel model) {
        // Get column names by iterating through columns
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
                    row.add(value); // Keep non-string values as is
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