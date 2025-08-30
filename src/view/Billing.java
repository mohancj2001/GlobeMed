/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view;

import controller.bill.BillVerificationHandler;
import controller.bill.ClaimHandler;
import controller.bill.DirectPaymentHandler;
import controller.bill.InsuranceApprovalHandler;
import controller.bill.InsuranceClaimRequest;
import controller.bill.SettlementHandler;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.MySQL;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author mohan
 */
public class Billing extends javax.swing.JPanel {

    /**
     * Creates new form Billing
     */
    private ClaimHandler directPaymentChain;
    private ClaimHandler insuranceClaimChain;
    private HashMap<String, String> insuranceMap = new HashMap<>();
    private HashMap<String, String> claimMap = new HashMap<>();

    public Billing() {
        initComponents();
        loadBillTable("", "", "");
        invoiceTotalCost_txt.setEditable(false);
        balance_txt.setEditable(false);
        loadInsurance();
        loadClaim();
        jComboBox2.setEnabled(false);
    }

    public void updateClaimStatus(int claimId, int newStatusId) {
        try {
            String updateQuery = "UPDATE insurance_claims SET claim_status_claim_status_id = " + newStatusId
                    + " WHERE claim_id = " + claimId;

            System.out.println("Updating claim status: " + updateQuery);
            MySQL.execute(updateQuery);

            String verifyQuery = "SELECT * FROM insurance_claims WHERE claim_id = " + claimId;
            ResultSet result = MySQL.execute(verifyQuery);
            if (result != null && result.next()) {
                System.out.println("✅ Claim status updated successfully!");
                System.out.println("New status ID: " + result.getInt("claim_status_claim_status_id"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error updating claim status:");
            e.printStackTrace();
        }
    }

    private void buildInsuranceClaimChain() {
        ClaimHandler billVerification = new BillVerificationHandler();
        ClaimHandler insuranceApproval = new InsuranceApprovalHandler();
        ClaimHandler settlement = new SettlementHandler();

        // Chain them together
        billVerification.setNext(insuranceApproval);
        insuranceApproval.setNext(settlement);

        this.insuranceClaimChain = billVerification;
    }

    public void processInsuranceClaim(int billId, int providerId) {
        System.out.println("🏥 Starting insurance claim process for Bill ID: " + billId + ", Provider ID: " + providerId);

        // Build the chain if it doesn't exist
        if (insuranceClaimChain == null) {
            buildInsuranceClaimChain();
        }

        InsuranceClaimRequest request = new InsuranceClaimRequest(billId, providerId, 0);
        insuranceClaimChain.handle(request);

        if (request.isSettled()) {
            System.out.println("✅ Insurance claim submitted successfully!");
            loadInsuranceClaimsTable(billId);
            try {
                Thread.sleep(1000); // Wait for 1000 milliseconds (1 second)
//                System.out.println("1 second has passed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Sleep was interrupted");
            }
            //print invoice
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

                HashMap<String, Object> reportParameters = new HashMap<>();
                reportParameters.put("date", simpleDateFormat.format(new Date()));
                Date date = new Date();
                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm:ss a");
                String ftime = simpleTimeFormat.format(date);

                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                int selectedRow = jTable1.getSelectedRow();
                if (selectedRow != -1) {
                    String invoiceId = String.valueOf(model.getValueAt(selectedRow, 0));
//                    String fname = String.valueOf(model.getValueAt(selectedRow, 1));
//                    String lname = String.valueOf(model.getValueAt(selectedRow, 2));
                    String totalamount = String.valueOf(model.getValueAt(selectedRow, 3));
                    String dateStr = String.valueOf(model.getValueAt(selectedRow, 4));

                    reportParameters.put("invoice_num", String.valueOf(invoiceId));
                    reportParameters.put("date", dateStr);
                    reportParameters.put("total_amount", totalamount);

                    DefaultTableModel model1 = (DefaultTableModel) jTable2.getModel();
                    int selectedRow1 = jTable1.getSelectedRow();
                    if (selectedRow1 != -1) {
                        String submitted_date = String.valueOf(model1.getValueAt(selectedRow, 1));
                        String provider = String.valueOf(model1.getValueAt(selectedRow, 2));

                        reportParameters.put("provider", provider);
                        reportParameters.put("submitted_date", submitted_date);
                    }

                    JRDataSource dataSource = new JRTableModelDataSource(jTable1.getModel());
                    String reportPath = "src//reports//insurance_bill.jasper";

                    JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, reportParameters, dataSource);
                    JasperViewer.viewReport(jasperPrint, false);
                }

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null,
                        "Invalid number format in amounts: " + nfe.getMessage(),
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                nfe.printStackTrace();
            } catch (JRException jre) {
                JOptionPane.showMessageDialog(null,
                        "Report generation failed: " + jre.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                jre.printStackTrace();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Unexpected error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
            //print invoice

//            loadBillTable("", "", ""); // Refresh table
        } else {
            System.out.println("❌ Insurance claim failed.");
            System.out.println("Valid bill: " + request.isValidBill());
            System.out.println("Insurance approved: " + request.isInsuranceApproved());
        }
    }

    private void loadInsurance() {
        try {
            ResultSet resultSet = MySQL.execute("SELECT * FROM `insurance_providers`");
            Vector v = new Vector();
            v.add("Select");

            while (resultSet.next()) {
                v.add(resultSet.getString("provider_name"));
                insuranceMap.put(resultSet.getString("provider_name"), resultSet.getString("provider_id"));
            }

            DefaultComboBoxModel model = (DefaultComboBoxModel) jComboBox1.getModel();
            model.removeAllElements();

            model.addAll(v);
            jComboBox1.setSelectedIndex(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadClaim() {
        try {
            ResultSet resultSet = MySQL.execute("SELECT * FROM `claim_status`");
            Vector v = new Vector();
            v.add("Select");

            while (resultSet.next()) {
                v.add(resultSet.getString("claim_status_name"));
                claimMap.put(resultSet.getString("claim_status_name"), resultSet.getString("claim_status_id"));
            }

            DefaultComboBoxModel model = (DefaultComboBoxModel) jComboBox2.getModel();
            model.removeAllElements();

            model.addAll(v);
            jComboBox2.setSelectedIndex(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildDirectPaymentChain() {
        ClaimHandler billVerification = new BillVerificationHandler();
        ClaimHandler directPayment = new DirectPaymentHandler();

        billVerification.setNext(directPayment);
        this.directPaymentChain = billVerification;
    }

    public void processDirectPayment(int billId) {
        System.out.println("💳 Starting direct payment process for Bill ID: " + billId);

        // Create request (providerId = 0 for direct payment, amount will be set by BillVerificationHandler)
        InsuranceClaimRequest request = new InsuranceClaimRequest(billId, 0, 0);

        directPaymentChain.handle(request);

        if (request.isSettled()) {
            System.out.println("✅ Direct payment completed successfully!");

            //print invoice
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

                HashMap<String, Object> reportParameters = new HashMap<>();
                reportParameters.put("date", simpleDateFormat.format(new Date()));
                Date date = new Date();
                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm:ss a");
                String ftime = simpleTimeFormat.format(date);

                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                int selectedRow = jTable1.getSelectedRow();
                if (selectedRow != -1) {
                    String invoiceId = String.valueOf(model.getValueAt(selectedRow, 0));
//                    String fname = String.valueOf(model.getValueAt(selectedRow, 1));
//                    String lname = String.valueOf(model.getValueAt(selectedRow, 2));
//                    String totalamount = String.valueOf(model.getValueAt(selectedRow, 3));
                    String dateStr = String.valueOf(model.getValueAt(selectedRow, 4));

                    reportParameters.put("invoice_num", String.valueOf(invoiceId));
                    reportParameters.put("date", dateStr);

                    reportParameters.put("total_amount", invoiceTotalCost_txt.getText());
                    reportParameters.put("paid", paidAmount_txt.getText());
                    reportParameters.put("balance", balance_txt.getText());

                    JRDataSource dataSource = new JRTableModelDataSource(jTable1.getModel());
                    String reportPath = "src//reports//direct_bill.jasper";

                    JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, reportParameters, dataSource);
                    JasperViewer.viewReport(jasperPrint, false);
                }

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null,
                        "Invalid number format in amounts: " + nfe.getMessage(),
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                nfe.printStackTrace();
            } catch (JRException jre) {
                JOptionPane.showMessageDialog(null,
                        "Report generation failed: " + jre.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                jre.printStackTrace();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Unexpected error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
            //print invoice

            // Refresh your table here
            loadBillTable("", "", ""); // Refresh the table to show updated status
        } else {
            System.out.println("❌ Direct payment failed.");
            System.out.println("Valid bill: " + request.isValidBill());
            System.out.println("Insurance approved: " + request.isInsuranceApproved());
        }
    }

    private void loadBillTable(String fname, String lname, String date) {
        try {
            String sql = "SELECT * FROM `bills` "
                    + "INNER JOIN `appointments` ON `bills`.`bill_appointment_id`=`appointments`.`appointment_id` "
                    + "INNER JOIN `patients` ON `appointments`.`patients_patient_id`=`patients`.`patient_id` "
                    + "INNER JOIN `bill_status` ON `bills`.`bill_status_bill_status_id`=`bill_status`.`bill_status_id` "
                    + "WHERE `patients`.`first_name` LIKE '" + fname + "%' AND `patients`.`last_name` LIKE '" + lname + "%' ";

            if (date != null && !date.trim().isEmpty()) {
                sql += "AND DATE(`created_datetime`)='" + date + "' ";
            }

            sql += "ORDER BY `bills`.`bill_id` DESC";

            ResultSet resultSet = MySQL.execute(sql);

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
            headerRenderer.setHorizontalAlignment(JLabel.CENTER);
            jTable1.getTableHeader().setDefaultRenderer(headerRenderer);

            DefaultTableCellRenderer dataRenderer = new DefaultTableCellRenderer();
            dataRenderer.setHorizontalAlignment(JLabel.CENTER);
            jTable1.setDefaultRenderer(Object.class, dataRenderer);
            jTable1.setShowGrid(true);

            while (resultSet != null && resultSet.next()) {
                String t_id = resultSet.getString("bill_id");
                String t_fname = resultSet.getString("first_name");
                String t_lname = resultSet.getString("last_name");
                String t_totalAmount = resultSet.getString("total_amount");
                String t_datetime = resultSet.getString("created_datetime");
                String t_appointment_id = resultSet.getString("appointment_id");
                String t_bill_status_name = resultSet.getString("bill_status_name");

                Vector<String> v = new Vector<>();
                v.add(t_id);
                v.add(t_fname);
                v.add(t_lname);
                v.add(t_totalAmount);
                v.add(t_datetime);
                v.add(t_appointment_id);
                v.add(t_bill_status_name);

                model.addRow(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        firstNameField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        lastNameField = new javax.swing.JTextField();
        appointment_date_field = new com.toedter.calendar.JDateChooser();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        paidAmount_txt = new javax.swing.JTextField();
        invoiceTotalCost_txt = new javax.swing.JTextField();
        balance_txt = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jButton4 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Patient First Name", "Patient Last Name", "Total Amount", "Date Time", "Appointment Id", "Bill Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jTable1MouseEntered(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel1.setBackground(new java.awt.Color(0, 150, 136));

        jLabel8.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Patient First  name");

        firstNameField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        firstNameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                firstNameFieldKeyReleased(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Patient Last Name");

        lastNameField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lastNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastNameFieldActionPerformed(evt);
            }
        });
        lastNameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lastNameFieldKeyReleased(evt);
            }
        });

        appointment_date_field.setBackground(new java.awt.Color(255, 255, 255));
        appointment_date_field.setForeground(new java.awt.Color(0, 0, 0));
        appointment_date_field.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                appointment_date_fieldPropertyChange(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Date");

        jLabel11.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Total Cost");

        jLabel12.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Paid Amount");

        jLabel13.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Balance");

        paidAmount_txt.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        paidAmount_txt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                paidAmount_txtKeyReleased(evt);
            }
        });

        invoiceTotalCost_txt.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        balance_txt.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(0, 0, 0));
        jButton1.setText("Pay");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jButton2.setText("Clear");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jButton3.setText("Add Insurance Claims");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jSeparator1.setBackground(new java.awt.Color(255, 255, 255));

        jComboBox1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel16.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Insurance Provider");

        jComboBox2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel17.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Claim Status");

        jSeparator2.setBackground(new java.awt.Color(255, 255, 255));

        jButton4.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jButton4.setText("Save");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(firstNameField)
                    .addComponent(lastNameField)
                    .addComponent(appointment_date_field, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(invoiceTotalCost_txt)
                    .addComponent(paidAmount_txt)
                    .addComponent(balance_txt)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(jLabel16)
                            .addComponent(jLabel17))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jSeparator2)
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(firstNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lastNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(appointment_date_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(invoiceTotalCost_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paidAmount_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addGap(1, 1, 1)
                .addComponent(balance_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addContainerGap(17, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap(418, Short.MAX_VALUE)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(246, 246, 246)))
        );

        jLabel14.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 0, 0));
        jLabel14.setText("Bill Table");

        jLabel15.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setText("Insurance Claims Table");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Submitted Date", "Insurance Provider", "Clain Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 783, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void firstNameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_firstNameFieldKeyReleased
        // TODO add your handling code here:
        String fname = firstNameField.getText();
        String lname = lastNameField.getText();
        String date = "";
        if (appointment_date_field.getDate() != null) {
            date = new SimpleDateFormat("yyyy-MM-dd").format(appointment_date_field.getDate());
        }

        loadBillTable(fname, lname, date);
    }//GEN-LAST:event_firstNameFieldKeyReleased

    private void lastNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lastNameFieldActionPerformed

    private void lastNameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lastNameFieldKeyReleased
        String fname = firstNameField.getText();
        String lname = lastNameField.getText();
        String date = "";
        if (appointment_date_field.getDate() != null) {
            date = new SimpleDateFormat("yyyy-MM-dd").format(appointment_date_field.getDate());
        }

        loadBillTable(fname, lname, date);
    }//GEN-LAST:event_lastNameFieldKeyReleased

    private void appointment_date_fieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_appointment_date_fieldPropertyChange
        String fname = firstNameField.getText();
        String lname = lastNameField.getText();
        String date = "";
        if (appointment_date_field.getDate() != null) {
            date = new SimpleDateFormat("yyyy-MM-dd").format(appointment_date_field.getDate());
        }

        loadBillTable(fname, lname, date);
    }//GEN-LAST:event_appointment_date_fieldPropertyChange

    private void paidAmount_txtKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_paidAmount_txtKeyReleased

        updatePaymentCalculations();
    }//GEN-LAST:event_paidAmount_txtKeyReleased

    private void updatePaymentCalculations() {
        try {
            String totalAmountStr = invoiceTotalCost_txt.getText();
            String paidText = paidAmount_txt.getText().trim();

            double totalAmount = totalAmountStr.isEmpty() ? 0.0 : Double.parseDouble(totalAmountStr);
            double paid = paidText.isEmpty() ? 0.0 : Double.parseDouble(paidText);

            double balance = totalAmount - paid;

            if (balance < 0) {
                balance_txt.setText(String.format("%.2f", Math.abs(balance)));
            } else {
                balance_txt.setText(String.format("%.2f", balance));
            }

        } catch (NumberFormatException e) {
            balance_txt.setText(invoiceTotalCost_txt.getText());
            paidAmount_txt.setText("");
            JOptionPane.showMessageDialog(this,
                    "Invalid payment amount entered",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clear() {
        firstNameField.setText("");
        lastNameField.setText("");
        appointment_date_field.setDate(null);
        invoiceTotalCost_txt.setText("");
        paidAmount_txt.setText("");
        balance_txt.setText("");
        jTable1.setEnabled(true);
        loadBillTable("", "", "");
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);
        jComboBox2.setEnabled(false);
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTable1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable1MouseEntered

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow != -1) {
                int id = Integer.parseInt(String.valueOf(model.getValueAt(selectedRow, 0)));
                String fname = String.valueOf(model.getValueAt(selectedRow, 1));
                String lname = String.valueOf(model.getValueAt(selectedRow, 2));
                String totalamount = String.valueOf(model.getValueAt(selectedRow, 3));
                String dateStr = String.valueOf(model.getValueAt(selectedRow, 4));

                firstNameField.setText(fname);
                lastNameField.setText(lname);

                // Fix: Parse the string to double first, then format it
                try {
                    double totalAmountValue = Double.parseDouble(totalamount);
                    invoiceTotalCost_txt.setText(String.format("%.2f", totalAmountValue));
                } catch (NumberFormatException e) {
                    invoiceTotalCost_txt.setText(totalamount); // Keep original if parsing fails
                }

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = sdf.parse(dateStr);
                    appointment_date_field.setDate(date);
                } catch (Exception e) {
                    e.printStackTrace();
                    appointment_date_field.setDate(null);
                }

                jTable1.setEnabled(false);
                paidAmount_txt.requestFocus(true);
                if (jTable1.getRowCount() > 0) {
                    jTable1.setRowSelectionInterval(0, 0);
                    jTable1.setColumnSelectionInterval(0, 0);
                }

                loadInsuranceClaimsTable(id);

            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void loadInsuranceClaimsTable(int id) {
        try {
            String sql = "SELECT * FROM `insurance_claims` INNER JOIN `claim_status` ON `insurance_claims`.`claim_status_claim_status_id`=`claim_status`.`claim_status_id` "
                    + "INNER JOIN `insurance_providers` ON `insurance_claims`.`insurance_providers_provider_id`=`insurance_providers`.`provider_id` "
                    + "INNER JOIN `bills` ON `insurance_claims`.`bills_bill_id`=`bills`.`bill_id` WHERE `bills_bill_id`='" + id + "'";

            ResultSet resultSet = MySQL.execute(sql);

            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0);

            DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
            headerRenderer.setHorizontalAlignment(JLabel.CENTER);
            jTable2.getTableHeader().setDefaultRenderer(headerRenderer);

            DefaultTableCellRenderer dataRenderer = new DefaultTableCellRenderer();
            dataRenderer.setHorizontalAlignment(JLabel.CENTER);
            jTable2.setDefaultRenderer(Object.class, dataRenderer);
            jTable2.setShowGrid(true);

            while (resultSet != null && resultSet.next()) {
                String t_id = resultSet.getString("claim_id");
                String t_datetime = resultSet.getString("submitted_date");
                String t_provider = resultSet.getString("provider_name");
                String t_status_name = resultSet.getString("claim_status_name");

                Vector<String> v = new Vector<>();
                v.add(t_id);
                v.add(t_datetime);
                v.add(t_provider);
                v.add(t_status_name);

                model.addRow(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        buildDirectPaymentChain();

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow != -1) {
            String idStr = String.valueOf(model.getValueAt(selectedRow, 0));
            String status = String.valueOf(model.getValueAt(selectedRow, 6));

            if (status.equals("Pending")) {
                int id = Integer.parseInt(idStr);
//                System.out.println("Selected Bill ID: " + id); 
                processDirectPayment(id);
            } else {
                JOptionPane.showMessageDialog(this, "Cannot Change paid Bills",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }

        } else {
            System.out.println("No row selected!");
        }
        clear();
    }//GEN-LAST:event_jButton1ActionPerformed


    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        int selectedRow = jTable1.getSelectedRow();

        if (selectedRow != -1) {
            String idStr = String.valueOf(model.getValueAt(selectedRow, 0));
            int billId = Integer.parseInt(idStr);

            // Get selected insurance provider from ComboBox
            String selectedProvider = (String) jComboBox1.getSelectedItem();

            if (selectedProvider != null && !selectedProvider.equals("Select")) {
                // Get provider ID from the HashMap
                String providerIdStr = insuranceMap.get(selectedProvider);

                if (providerIdStr != null) {
                    try {
                        int providerId = Integer.parseInt(providerIdStr);
                        processInsuranceClaim(billId, providerId);
                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing provider ID: " + providerIdStr);
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Provider ID not found for: " + selectedProvider);
                }
            } else {
                System.out.println("Please select an insurance provider!");
            }
        } else {
            System.out.println("No row selected!");
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        String selectedName = (String) jComboBox2.getSelectedItem();
        if (selectedName != null && claimMap.containsKey(selectedName)) {
            int selectedId = Integer.parseInt(claimMap.get(selectedName));
            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            int selectedRow = jTable2.getSelectedRow();
            if (selectedRow != -1) {
                int id = Integer.parseInt(String.valueOf(model.getValueAt(selectedRow, 0)));
                processApprovedClaim(selectedId, id);
            }
            System.out.println("Selected Claim ID: " + selectedId);
        }
        clear();
        jComboBox2.setEnabled(false);

    }//GEN-LAST:event_jButton4ActionPerformed

    public void processApprovedClaim(int statusId, int claimId) {
        try {

            // Update claim status to approved/processed
            String updateClaimQuery = "UPDATE insurance_claims SET claim_status_claim_status_id = "
                    + statusId + " WHERE claim_id = " + claimId;
            MySQL.execute(updateClaimQuery);

            System.out.println("✅ Claim approved and bill marked as paid!");

        } catch (Exception e) {
            System.out.println("❌ Error processing approved claim:");
            e.printStackTrace();
        }
    }

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        // TODO add your handling code here:
        jComboBox2.setEnabled(true);
        jTable2.setEnabled(false);
        jComboBox2.requestFocus(true);
        if (jTable2.getRowCount() > 0) {
            jTable2.setRowSelectionInterval(0, 0);
            jTable2.setColumnSelectionInterval(0, 0);
        }
    }//GEN-LAST:event_jTable2MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser appointment_date_field;
    private javax.swing.JTextField balance_txt;
    private javax.swing.JTextField firstNameField;
    private javax.swing.JTextField invoiceTotalCost_txt;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField lastNameField;
    private javax.swing.JTextField paidAmount_txt;
    // End of variables declaration//GEN-END:variables
}
