/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view;

import controller.appointment.Appointment;
import controller.appointment.AppointmentDAO;
import controller.appointment.AppointmentService;
import controller.appointment.HealthcareProfessional;
import controller.appointment.HealthcareProfessionalDAO;
import controller.appointment.LookupDAO;
import controller.appointment.PatientDAO;
import controller.appointment.SchedulerMediator;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import model.MySQL;

/**
 *
 * @author mohan
 */
public class Appointments extends javax.swing.JPanel {

    /**
     * Creates new form Appointments
     */
    private AppointmentService appointmentService;
    private HealthcareProfessionalDAO professionalDAO;
    private PatientDAO patientDAO;
    private HealthcareProfessionalDAO doctorDAO;
    private LookupDAO lookupDAO;
    private AppointmentDAO appointmentDAO;

    private HashMap<String, String> apStatusMap = new HashMap<>();

    public Appointments() {

        initComponents();
        initializeServices();
        patientDAO = new PatientDAO();
        loadPatientsToTable();
        doctorDAO = new HealthcareProfessionalDAO();
        lookupDAO = doctorDAO.getLookupDAO();
        loadDoctorsToTable();
        loadApStatus();
        loadAppointments("", "", "", "", "");
    }

    private void initializeServices() {
        try {
            // Initialize DAOs once
            patientDAO = new PatientDAO();
            professionalDAO = new HealthcareProfessionalDAO();
            appointmentDAO = new AppointmentDAO();

            // Initialize mediator and service
            SchedulerMediator mediator = new SchedulerMediator(appointmentDAO, professionalDAO);
            appointmentService = new AppointmentService(mediator, patientDAO, appointmentDAO);

        } catch (Exception e) {
            System.out.println("Error initializing services: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterAppointments() {
        String patientId = jTextField1.getText().trim();
        String staffId = jTextField2.getText().trim();

        // Handle date safely
        String date = "";
        if (appointment_date_field.getDate() != null) {
            date = new SimpleDateFormat("yyyy-MM-dd").format(appointment_date_field.getDate());
        }

        // Handle time safely using LocalTime from LGoodDatePicker
        String time24 = "";
        LocalTime lt = timePicker1.getTime(); // direct LocalTime, no parsing required
        if (lt != null) {
            time24 = lt.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
        System.out.println("Time (24h): " + time24);

        // Handle status
        String status = "";
        if (jComboBox1.getSelectedIndex() > 0) {
            String selectedStatusName = (String) jComboBox1.getSelectedItem();
            status = apStatusMap.get(selectedStatusName);
        }

        // Pass filters to loadAppointments
        loadAppointments(patientId, staffId, date, time24, status);
    }

    private void clearFilters1() {
        jTextField1.setText("");
        jTextField2.setText("");
        appointment_date_field.setDate(null);
        timePicker1.setText("");
        jComboBox1.setSelectedIndex(0);
        loadAppointments("", "", "", "", "");
        dashboard_table.setEnabled(true);
    }

    private void loadAppointments(String patientId, String staffId, String date, String time, String status) {
        try {
            // Build the SQL query with filters
            StringBuilder query = new StringBuilder();
            query.append("SELECT a.appointment_id, a.appointment_date, a.appointment_time, ");
            query.append("a.patients_patient_id, a.staff_staff_id, s.appointment_status_name, ");
            query.append("p.first_name AS patient_first_name, p.last_name AS patient_last_name, ");
            query.append("st.first_name AS staff_first_name, st.last_name AS staff_last_name ");
            query.append("FROM appointments a ");
            query.append("INNER JOIN appointment_status s ON a.appointment_status_appointment_status_id = s.appointment_status_id ");
            query.append("INNER JOIN patients p ON a.patients_patient_id = p.patient_id ");
            query.append("INNER JOIN staff st ON a.staff_staff_id = st.staff_id ");
            query.append("WHERE 1=1 ");

            if (!patientId.isEmpty()) {
                query.append("AND a.patients_patient_id LIKE '").append(patientId).append("%' ");
            }

            if (!staffId.isEmpty()) {
                query.append("AND a.staff_staff_id LIKE '").append(staffId).append("%' ");
            }

            if (!date.isEmpty()) {
                query.append("AND a.appointment_date LIKE '").append(date).append("%' ");
            }

            if (!time.isEmpty()) {
                query.append("AND a.appointment_time LIKE '").append(time).append("%' ");
            }

            if (!status.isEmpty()) {
                query.append("AND a.appointment_status_appointment_status_id = '").append(status).append("' ");
            }

            // Execute query
            ResultSet resultSet = MySQL.execute(query.toString());

            // Clear the table
            DefaultTableModel model = (DefaultTableModel) dashboard_table.getModel();
            model.setRowCount(0);

            // Populate the table with results
            while (resultSet != null && resultSet.next()) {
                Vector<String> row = new Vector<>();
                row.add(resultSet.getString("appointment_id"));
                row.add(resultSet.getString("appointment_date"));
                row.add(resultSet.getString("appointment_time"));
                String patientName = resultSet.getString("patient_first_name") + " "
                        + resultSet.getString("patient_last_name");
                String staffName = resultSet.getString("staff_first_name") + " "
                        + resultSet.getString("staff_last_name");
                row.add(patientName);
                row.add(staffName);
                row.add(resultSet.getString("appointment_status_name"));

                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadApStatus() {
        try {
            ResultSet resultSet = MySQL.execute("SELECT * FROM `appointment_status`");
            Vector v = new Vector();
            v.add("Select");

            while (resultSet.next()) {
                v.add(resultSet.getString("appointment_status_name"));
                apStatusMap.put(resultSet.getString("appointment_status_name"), resultSet.getString("appointment_status_id"));
            }

            DefaultComboBoxModel model = (DefaultComboBoxModel) jComboBox1.getModel();
            model.removeAllElements();

            model.addAll(v);
            jComboBox1.setSelectedIndex(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPatientsToTable() {
        loadFilteredPatients(null, null, null, null);
    }

    private void loadDoctorsToTable() {
        loadFilteredDoctors(null, null, null, null, null, null, null, null);
    }

    private void loadFilteredDoctors(Integer staffId, String firstName, String lastName,
            String speciality, String mobile, String license,
            Integer statusId, Integer branchId) {
        try {
            List<HealthcareProfessional> doctors = doctorDAO.searchDoctors(staffId, firstName, lastName,
                    speciality, mobile, license,
                    statusId, branchId);

            Vector<String> columnNames = new Vector<>();
            columnNames.add("ID");
            columnNames.add("First Name");
            columnNames.add("Last Name");
            columnNames.add("Role");
            columnNames.add("Speciality");
            columnNames.add("Mobile");
            columnNames.add("License");
            columnNames.add("Status");
            columnNames.add("Branch");

            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (HealthcareProfessional doctor : doctors) {
                Vector<Object> row = new Vector<>();
                row.add(doctor.getStaffId());
                row.add(doctor.getFirstName());
                row.add(doctor.getLastName());
                row.add(lookupDAO.getRoleName(doctor.getRoleId()));
                row.add(doctor.getSpeciality());
                row.add(doctor.getMobile());
                row.add(doctor.getLicense());
                row.add(lookupDAO.getStatusName(doctor.getStatusId()));
                row.add(lookupDAO.getBranchName(doctor.getBranchId()));
                model.addRow(row);
            }

            staff_table.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading doctors: " + e.getMessage());
        }
    }

    private void searchDoctors() {
        Integer staffId = null;
        String firstName = null;
        String lastName = null;
        String speciality = null;
        String mobile = null;
        String license = null;
        Integer statusId = 1;
        Integer branchId = null;

        try {
            if (!jTextField2.getText().trim().isEmpty()) {
                staffId = Integer.parseInt(jTextField2.getText().trim());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Staff ID (numbers only)");
            return;
        }

        if (!firstNameField1.getText().trim().isEmpty()) {
            firstName = firstNameField1.getText().trim();
        }
        if (!lastNameField1.getText().trim().isEmpty()) {
            lastName = lastNameField1.getText().trim();
        }
        if (!mobileField3.getText().trim().isEmpty()) {
            speciality = mobileField3.getText().trim();
        }
        if (!mobileField1.getText().trim().isEmpty()) {
            mobile = mobileField1.getText().trim();
        }
//        if (!txtLicense.getText().trim().isEmpty()) {
//            license = txtLicense.getText().trim();
//        }

        // Parse status and branch if provided
//        try {
//            if (!txtStatus.getText().trim().isEmpty()) {
//                statusId = Integer.parseInt(1);
//            }
//        } catch (NumberFormatException e) {
//            // If not a number, search by status name
//            String statusName = txtStatus.getText().trim();
//            if (!statusName.isEmpty()) {
//                statusId = getStatusIdByName(statusName);
//            }
//        }
        try {
            if (!mobileField2.getText().trim().isEmpty()) {
                branchId = Integer.parseInt(mobileField2.getText().trim());
            }
        } catch (NumberFormatException e) {
            // If not a number, search by branch name
            String branchName = mobileField2.getText().trim();
            if (!branchName.isEmpty()) {
                branchId = getBranchIdByName(branchName);
            }
        }

        loadFilteredDoctors(staffId, firstName, lastName, speciality, mobile, license, statusId, branchId);
    }

//    private Integer getStatusIdByName(String statusName) {
//        String sql = "SELECT status_id FROM status WHERE status_name LIKE '%" + statusName + "%'";
//        
//        try {
//            ResultSet rs = model.MySQL.execute(sql);
//            if (rs != null && rs.next()) {
//                return rs.getInt("status_id");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        
//        return null;
//    }
    private Integer getBranchIdByName(String branchName) {
        String sql = "SELECT branch_id FROM branch WHERE branch_name LIKE '%" + branchName + "%'";

        try {
            ResultSet rs = model.MySQL.execute(sql);
            if (rs != null && rs.next()) {
                return rs.getInt("branch_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void clearFilters2() {
        jTextField2.setText("");
        firstNameField1.setText("");
        lastNameField1.setText("");
        mobileField2.setText("");
        mobileField1.setText("");
        mobileField3.setText("");

        loadDoctorsToTable();
    }

    private void loadFilteredPatients(Integer patientId, String firstName, String lastName, String mobile) {
        try {
            List<controller.appointment.Patient> patients = patientDAO.searchPatients(patientId, firstName, lastName, mobile);

            Vector<String> columnNames = new Vector<>();
            columnNames.add("Patient Id");
            columnNames.add("First Name");
            columnNames.add("Last Name");
            columnNames.add("Contact Number");
            columnNames.add("Email");

            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (controller.appointment.Patient patient : patients) {
                Vector<Object> row = new Vector<>();
                row.add(patient.getPatientId());
                row.add(patient.getFirstName());
                row.add(patient.getLastName());
                row.add(patient.getContactNumber());
                row.add(patient.getEmail());
                model.addRow(row);
            }

            patientTable.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading patients: " + e.getMessage());
        }

    }

    private void searchPatients() {
        Integer patientId = null;
        String firstName = null;
        String lastName = null;
        String mobile = null;

        try {
            if (!jTextField1.getText().trim().isEmpty()) {
                patientId = Integer.parseInt(jTextField1.getText().trim());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Patient ID (numbers only)");
            return;
        }

        if (!firstNameField.getText().trim().isEmpty()) {
            firstName = firstNameField.getText().trim();
        }
        if (!lastNameField.getText().trim().isEmpty()) {
            lastName = lastNameField.getText().trim();
        }
        if (!mobileField.getText().trim().isEmpty()) {
            mobile = mobileField.getText().trim();
        }

        loadFilteredPatients(patientId, firstName, lastName, mobile);
    }

    private void clearFilters() {
        jTextField1.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        mobileField.setText("");
        loadPatientsToTable();
    }

//    private void initializeServices() {
//        DefaultTableModel model1 = (DefaultTableModel) dashboard_table.getModel();
//        int selectedRow2 = dashboard_table.getSelectedRow();
//        if (selectedRow2 != -1) {
//            try {
//                // Update part here
//                // Full update of an appointment
//                PatientDAO patientDAO = new PatientDAO();
//                HealthcareProfessionalDAO professionalDAO = new HealthcareProfessionalDAO();
//                AppointmentDAO appointmentDAO = new AppointmentDAO();
//
//                SchedulerMediator mediator = new SchedulerMediator(appointmentDAO, professionalDAO);
//                AppointmentService service = new AppointmentService(mediator, patientDAO, appointmentDAO);
//                int appointmentId = Integer.parseInt(String.valueOf(model1.getValueAt(selectedRow2, 0)));
//
//                java.util.Date utilDate = appointment_date_field.getDate();
//                java.sql.Date appointmentDate = new java.sql.Date(utilDate.getTime());
//
//                String timeString = timePicker1.getTimeStringOrEmptyString();
//                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//                java.util.Date utilTime = sdf.parse(timeString);
//                java.sql.Time selectedTime = new java.sql.Time(utilTime.getTime());
//
//                if (jComboBox1.getSelectedIndex() == 1) {
//                    boolean updateSuccess = appointmentService.updateAppointment(
//                            appointmentId, // appointmentId
//                            appointmentDate, // new date - already java.sql.Date
//                            selectedTime, // new time - already java.sql.Time
//                            AppointmentDAO.STATUS_SCHEDULED // status
//                    );
//                    if (updateSuccess) {
//                        System.out.println("Appointment booked successfully!");
//                    } else {
//                        System.out.println("Failed to book appointment.");
//                    }
//                } else if (jComboBox1.getSelectedIndex() == 2) {
//                    boolean updateSuccess = appointmentService.updateAppointment(
//                            appointmentId, // appointmentId
//                            appointmentDate, // new date - already java.sql.Date
//                            selectedTime, // new time - already java.sql.Time
//                            AppointmentDAO.STATUS_COMPLETED // status
//                    );
//                    if (updateSuccess) {
//                        System.out.println("Appointment booked successfully!");
//                    } else {
//                        System.out.println("Failed to book appointment.");
//                    }
//                } else {
//                    boolean updateSuccess = appointmentService.updateAppointment(
//                            appointmentId, // appointmentId
//                            appointmentDate, // new date - already java.sql.Date
//                            selectedTime, // new time - already java.sql.Time
//                            AppointmentDAO.STATUS_CANCELLED // status
//                    );
//                    if (updateSuccess) {
//                        System.out.println("Appointment booked successfully!");
//                    } else {
//                        System.out.println("Failed to book appointment.");
//                    }
//                }
//                loadAppointments("", "", "", "", "");
//                clearFilters1();
//
//            } catch (Exception e) {
//                System.out.println("Error: " + e.getMessage());
//            }
//        } else {
//            PatientDAO patientDAO = new PatientDAO();
//            HealthcareProfessionalDAO professionalDAO = new HealthcareProfessionalDAO();
//            AppointmentDAO appointmentDAO = new AppointmentDAO();
//
//            SchedulerMediator mediator = new SchedulerMediator(appointmentDAO, professionalDAO);
//            AppointmentService service = new AppointmentService(mediator, patientDAO, appointmentDAO);
//
//            // Check available doctors
//            List<HealthcareProfessional> doctors = professionalDAO.getAllDoctors();
//            System.out.println("Available Doctors:");
//            for (HealthcareProfessional doctor : doctors) {
//                System.out.println("- " + doctor.toString());
//            }
//
//            int selectedRow = staff_table.getSelectedRow();
//
//            if (selectedRow != -1) {
//                String id = String.valueOf(staff_table.getValueAt(selectedRow, 0));
//                int doctorId = Integer.parseInt(id);
//                java.util.Date utilDate = appointment_date_field.getDate();
//                java.sql.Date appointmentDate = new java.sql.Date(utilDate.getTime());
//
//                List<Time> availableSlots = service.checkAvailability(doctorId, appointmentDate);
//
//                // Check availability for a specific doctor
//                System.out.println("\nAvailable slots for Dr. on " + appointmentDate + ":");
//                for (Time slot : availableSlots) {
//                    System.out.println("- " + slot);
//                }
//
//                // Book appointment
//                if (!availableSlots.isEmpty()) {
//                    int selectedRow1 = patientTable.getSelectedRow();
//
//                    if (selectedRow != -1) {
//                        String pid = String.valueOf(patientTable.getValueAt(selectedRow1, 0));
//                        int patientId = Integer.parseInt(pid);
//                        //i want to set time by time picker here
//                        String timeString = timePicker1.getTimeStringOrEmptyString();
//                        if (!timeString.isEmpty()) {
//                            try {
//                                // Parse the time string (assuming format like "14:30")
//                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//                                java.util.Date utilTime = sdf.parse(timeString);
//                                Time selectedTime = new Time(utilTime.getTime());
//
//                                boolean booked = service.bookAppointment(patientId, doctorId, appointmentDate, selectedTime);
//                                if (booked) {
//                                    System.out.println("Appointment booked successfully!");
//                                } else {
//                                    System.out.println("Failed to book appointment.");
//                                }
//                            } catch (Exception e) {
//                                System.out.println("Invalid time format: " + timeString);
//                            }
//                        } else {
//                            System.out.println("Please select a time.");
//                        }
//                    }
//
//                }
//
//            }
//
//            // View all appointments
//            List<Appointment> appointments = service.getAllAppointments();
//            System.out.println("\nAll Appointments:");
//            for (Appointment appt : appointments) {
//                System.out.println("ID: " + appt.getAppointmentId()
//                        + " | Date: " + appt.getAppointmentDate()
//                        + " | Time: " + appt.getAppointmentTime());
//            }
//        }
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        firstNameField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        lastNameField = new javax.swing.JTextField();
        mobileField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        patientTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        firstNameField1 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        lastNameField1 = new javax.swing.JTextField();
        mobileField1 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        mobileField2 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        mobileField3 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        staff_table = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        appointment_date_field = new com.toedter.calendar.JDateChooser();
        timePicker1 = new com.github.lgooddatepicker.components.TimePicker();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        dashboard_table = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(0, 150, 136));

        jLabel8.setText("First  name");
        jLabel8.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));

        firstNameField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        firstNameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                firstNameFieldKeyReleased(evt);
            }
        });

        jLabel9.setText("Last Name");
        jLabel9.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));

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

        mobileField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        mobileField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mobileFieldKeyReleased(evt);
            }
        });

        jLabel11.setText("Mobile ");
        jLabel11.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));

        jButton2.setText("Clear");
        jButton2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel16.setText("Id");
        jLabel16.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));

        jTextField1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(firstNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lastNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mobileField, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel9)
                    .addComponent(jLabel11)
                    .addComponent(mobileField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lastNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(firstNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        patientTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "First Name", "Last Name", "DOB", "Gender", "Mobile", "Email", "Address"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        patientTable.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        patientTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                patientTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(patientTable);

        jPanel2.setBackground(new java.awt.Color(0, 150, 136));

        jLabel10.setText("First  name");
        jLabel10.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));

        firstNameField1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        firstNameField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                firstNameField1KeyReleased(evt);
            }
        });

        jLabel12.setText("Last Name");
        jLabel12.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));

        lastNameField1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lastNameField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lastNameField1KeyReleased(evt);
            }
        });

        mobileField1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        mobileField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mobileField1KeyReleased(evt);
            }
        });

        jLabel13.setText("Mobile ");
        jLabel13.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));

        jButton3.setText("Clear");
        jButton3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel17.setText("Id");
        jLabel17.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));

        jTextField2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
        });

        jLabel14.setText("Branch");
        jLabel14.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));

        mobileField2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        mobileField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mobileField2KeyReleased(evt);
            }
        });

        jLabel15.setText("Speciality");
        jLabel15.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));

        mobileField3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        mobileField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mobileField3KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(firstNameField1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lastNameField1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mobileField1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mobileField2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mobileField3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(mobileField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(mobileField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(firstNameField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lastNameField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(mobileField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton3)
                        .addComponent(jLabel13)
                        .addComponent(jLabel10)
                        .addComponent(jLabel17)
                        .addComponent(jLabel12)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setText("Patients Table");
        jLabel1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel2.setText("Doctors Table");
        jLabel2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        staff_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Id", "First Name", "Last Name", "Role", "Speciality", "Mobile", "License", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        staff_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                staff_tableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(staff_table);

        jPanel3.setBackground(new java.awt.Color(0, 150, 136));

        jLabel18.setText("Appointment Date");
        jLabel18.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));

        jLabel19.setText("Appointment Time");
        jLabel19.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));

        jButton4.setText("Clear");
        jButton4.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel23.setText("Appointment Status");
        jLabel23.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));

        appointment_date_field.setBackground(new java.awt.Color(255, 255, 255));
        appointment_date_field.setForeground(new java.awt.Color(0, 0, 0));
        appointment_date_field.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                appointment_date_fieldPropertyChange(evt);
            }
        });

        timePicker1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                timePicker1PropertyChange(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jButton1.setText("Save");
        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(0, 0, 0));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(appointment_date_field, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(timePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(appointment_date_field, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton4)
                        .addComponent(jLabel18)
                        .addComponent(jLabel19)
                        .addComponent(jLabel23)
                        .addComponent(timePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox1)))
                .addContainerGap())
        );

        dashboard_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "Date", "Time", "Patient", "Doctor", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dashboard_table.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        dashboard_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dashboard_tableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(dashboard_table);

        jLabel3.setText("Appointments Table");
        jLabel3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane3))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void firstNameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_firstNameFieldKeyReleased
        // TODO add your handling code here:
        searchPatients();
    }//GEN-LAST:event_firstNameFieldKeyReleased

    private void lastNameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lastNameFieldKeyReleased
        // TODO add your handling code here:
        searchPatients();
    }//GEN-LAST:event_lastNameFieldKeyReleased

    private void mobileFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mobileFieldKeyReleased
        // TODO add your handling code here:
        searchPatients();
    }//GEN-LAST:event_mobileFieldKeyReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        clearFilters();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        // TODO add your handling code here:
        searchPatients();
    }//GEN-LAST:event_jTextField1KeyReleased

    private void patientTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_patientTableMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_patientTableMouseClicked

    private void firstNameField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_firstNameField1KeyReleased
        // TODO add your handling code here:
        searchDoctors();
    }//GEN-LAST:event_firstNameField1KeyReleased

    private void lastNameField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lastNameField1KeyReleased
        // TODO add your handling code here:
        searchDoctors();
    }//GEN-LAST:event_lastNameField1KeyReleased

    private void mobileField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mobileField1KeyReleased
        // TODO add your handling code here:
        searchDoctors();
    }//GEN-LAST:event_mobileField1KeyReleased

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        clearFilters2();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyReleased
        // TODO add your handling code here:
        searchDoctors();
    }//GEN-LAST:event_jTextField2KeyReleased

    private void lastNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lastNameFieldActionPerformed

    private void mobileField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mobileField2KeyReleased
        // TODO add your handling code here:
        searchDoctors();
    }//GEN-LAST:event_mobileField2KeyReleased

    private void mobileField3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mobileField3KeyReleased
        // TODO add your handling code here:
        searchDoctors();
    }//GEN-LAST:event_mobileField3KeyReleased

    private void staff_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_staff_tableMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_staff_tableMouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        clearFilters1();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void appointment_date_fieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_appointment_date_fieldPropertyChange
        // TODO add your handling code here:
        int selectedRow = -1;
        selectedRow = dashboard_table.getSelectedRow();
        if (selectedRow != -1) {

        } else {
            filterAppointments();
        }
    }//GEN-LAST:event_appointment_date_fieldPropertyChange

    private void timePicker1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_timePicker1PropertyChange
        // TODO add your handling code here:
        int selectedRow = -1;
        selectedRow = dashboard_table.getSelectedRow();
        if (selectedRow != -1) {

        } else {
            filterAppointments();
        }
    }//GEN-LAST:event_timePicker1PropertyChange

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        // TODO add your handling code here:
        int selectedRow = -1;
        selectedRow = dashboard_table.getSelectedRow();
        if (selectedRow != -1) {

        } else {
            filterAppointments();
        }

    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        int selectedRow2 = dashboard_table.getSelectedRow();
        if (selectedRow2 != -1) {
            updateAppointmentAction();
        } else {
            bookNewAppointmentAction();
        }
        clearFilters1();
        loadAppointments("", "", "", "", "");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void updateAppointmentAction() {
        DefaultTableModel model1 = (DefaultTableModel) dashboard_table.getModel();
        int selectedRow2 = dashboard_table.getSelectedRow();
        if (selectedRow2 != -1) {
            try {
                int appointmentId = Integer.parseInt(String.valueOf(model1.getValueAt(selectedRow2, 0)));
                java.util.Date utilDate = appointment_date_field.getDate();
                java.sql.Date appointmentDate = new java.sql.Date(utilDate.getTime());

                String timeString = timePicker1.getTimeStringOrEmptyString();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                java.util.Date utilTime = sdf.parse(timeString);
                java.sql.Time selectedTime = new java.sql.Time(utilTime.getTime());

                int status;
                if (jComboBox1.getSelectedIndex() == 1) {
                    status = AppointmentDAO.STATUS_SCHEDULED;
                    boolean updateSuccess = appointmentService.updateAppointment(
                            appointmentId, appointmentDate, selectedTime, status
                    );

                    if (updateSuccess) {
                        System.out.println("Appointment updated successfully!");
                    } else {
                        System.out.println("Failed to update appointment.");
                    }
                } else if (jComboBox1.getSelectedIndex() == 2) {
                    status = AppointmentDAO.STATUS_COMPLETED;
                    boolean updateSuccess = appointmentService.updateAppointment(
                            appointmentId, appointmentDate, selectedTime, status
                    );

                    if (updateSuccess) {
                        System.out.println("Appointment updated successfully!");
                    } else {
                        System.out.println("Failed to update appointment.");
                    }

                    LocalDateTime now = LocalDateTime.now();
                    try {
                        // ✅ Fix query quotes
                        MySQL.execute("INSERT INTO `bills` (`created_datetime`, `bill_status_bill_status_id`, `bill_appointment_id`) "
                                + "VALUES ('" + now + "', '" + 1 + "', '" + appointmentId + "')");

                        // ✅ Fix alias for LAST_INSERT_ID
                        ResultSet rs = MySQL.execute("SELECT LAST_INSERT_ID() AS bill_id");
                        int billId = -1;
                        if (rs.next()) {
                            billId = rs.getInt("bill_id");
                        }

                        Add_Bill_Prices abp = new Add_Bill_Prices(billId);
                        Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
                        JDialog dialog = new JDialog(window, "Add Bill Prices", Dialog.ModalityType.APPLICATION_MODAL);
                        dialog.getContentPane().add(abp);
                        dialog.pack();
                        dialog.setLocationRelativeTo(this);
                        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        dialog.setVisible(true);

                        dialog.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent e) {
                                loadAppointments("", "", "", "", "");
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    status = AppointmentDAO.STATUS_CANCELLED;
                    boolean updateSuccess = appointmentService.updateAppointment(
                            appointmentId, appointmentDate, selectedTime, status
                    );
                    if (updateSuccess) {
                        System.out.println("Appointment updated successfully!");
                    } else {
                        System.out.println("Failed to update appointment.");
                    }

                }

                // Use the class-level appointmentService
                loadAppointments("", "", "", "", "");
                clearFilters1();

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void bookNewAppointmentAction() {
        int selectedRow = staff_table.getSelectedRow();
        if (selectedRow != -1) {
            try {
                String id = String.valueOf(staff_table.getValueAt(selectedRow, 0));
                int doctorId = Integer.parseInt(id);
                java.util.Date utilDate = appointment_date_field.getDate();
                java.sql.Date appointmentDate = new java.sql.Date(utilDate.getTime());

                // Check availability
                List<Time> availableSlots = appointmentService.checkAvailability(doctorId, appointmentDate);

                System.out.println("\nAvailable slots for Dr. on " + appointmentDate + ":");
                for (Time slot : availableSlots) {
                    System.out.println("- " + slot);
                }

                // Book appointment if slots available
                if (!availableSlots.isEmpty()) {
                    int selectedRow1 = patientTable.getSelectedRow();
                    if (selectedRow1 != -1) {
                        String pid = String.valueOf(patientTable.getValueAt(selectedRow1, 0));
                        int patientId = Integer.parseInt(pid);

                        String timeString = timePicker1.getTimeStringOrEmptyString();
                        if (!timeString.isEmpty()) {
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                            java.util.Date utilTime = sdf.parse(timeString);
                            Time selectedTime = new Time(utilTime.getTime());

                            boolean booked = appointmentService.bookAppointment(patientId, doctorId, appointmentDate, selectedTime);
                            if (booked) {
                                System.out.println("Appointment booked successfully!");
                            } else {
                                System.out.println("Failed to book appointment.");
                            }
                        } else {
                            System.out.println("Please select a time.");
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println("Error booking appointment: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

//    private void viewAllAppointments() {
//        try {
//            List<Appointment> appointments = appointmentService.getAllAppointments();
//            System.out.println("\nAll Appointments:");
//            for (Appointment appt : appointments) {
//                System.out.println("ID: " + appt.getAppointmentId()
//                        + " | Date: " + appt.getAppointmentDate()
//                        + " | Time: " + appt.getAppointmentTime());
//            }
//        } catch (Exception e) {
//            System.out.println("Error viewing appointments: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    private void dashboard_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboard_tableMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            DefaultTableModel model = (DefaultTableModel) dashboard_table.getModel();
            int selectedRow = dashboard_table.getSelectedRow();
            if (selectedRow != -1) {
                String id = String.valueOf(model.getValueAt(selectedRow, 0));
//                System.out.println(id);
                try {
                    ResultSet resultSet = MySQL.execute("SELECT * FROM `appointments` INNER JOIN `appointment_status` ON `appointment_status`.`appointment_status_id`=`appointments`.`appointment_status_appointment_status_id` WHERE `appointment_id`='" + id + "'");
                    if (resultSet.next()) {
                        String date = resultSet.getString("appointment_date");
                        String time = resultSet.getString("appointment_time");
                        String status = resultSet.getString("appointment_status_id");

                        System.out.println(date + "_" + time + "_" + status);
                        Date sqlDate = java.sql.Date.valueOf(date);

                        LocalTime localTime = java.time.LocalTime.parse(time);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
                        String formattedTime = localTime.format(formatter);

                        appointment_date_field.setDate(sqlDate);
                        timePicker1.setText(formattedTime);
                        jComboBox1.setSelectedIndex(Integer.parseInt(status));

//                        dashboard_table.setRowSelectionInterval(0, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            dashboard_table.setEnabled(false);
        }
    }//GEN-LAST:event_dashboard_tableMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser appointment_date_field;
    private javax.swing.JTable dashboard_table;
    private javax.swing.JTextField firstNameField;
    private javax.swing.JTextField firstNameField1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField lastNameField;
    private javax.swing.JTextField lastNameField1;
    private javax.swing.JTextField mobileField;
    private javax.swing.JTextField mobileField1;
    private javax.swing.JTextField mobileField2;
    private javax.swing.JTextField mobileField3;
    private javax.swing.JTable patientTable;
    private javax.swing.JTable staff_table;
    private com.github.lgooddatepicker.components.TimePicker timePicker1;
    // End of variables declaration//GEN-END:variables
}
