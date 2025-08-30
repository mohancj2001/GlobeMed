package controller.appointment;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import model.MySQL;

public class AppointmentDAO {
    
    // Status constants based on your database
    public static final int STATUS_SCHEDULED = 1;
    public static final int STATUS_COMPLETED = 2;
    public static final int STATUS_CANCELLED = 3;
    
    public boolean createAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (appointment_date, appointment_time, " +
                "patients_patient_id, staff_staff_id, appointment_status_appointment_status_id) " +
                "VALUES ('" + appointment.getAppointmentDate() + "', '" + appointment.getAppointmentTime() +
                "', " + appointment.getPatientId() + ", " + appointment.getStaffId() +
                ", " + STATUS_SCHEDULED + ")"; // Default to Scheduled status

        try {
            ResultSet rs = MySQL.execute(sql);
            if (rs == null) {
                // Get the last inserted ID
                ResultSet generatedKeys = MySQL.execute("SELECT LAST_INSERT_ID()");
                if (generatedKeys != null && generatedKeys.next()) {
                    appointment.setAppointmentId(generatedKeys.getInt(1));
                    appointment.setAppointmentStatusId(STATUS_SCHEDULED);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Enhanced update method for full appointment updates
    public boolean updateAppointment(Appointment appointment) {
        String sql = "UPDATE appointments SET " +
                "appointment_date = '" + appointment.getAppointmentDate() + "', " +
                "appointment_time = '" + appointment.getAppointmentTime() + "', " +
                "patients_patient_id = " + appointment.getPatientId() + ", " +
                "staff_staff_id = " + appointment.getStaffId() + ", " +
                "appointment_status_appointment_status_id = " + appointment.getAppointmentStatusId() + " " +
                "WHERE appointment_id = " + appointment.getAppointmentId();

        try {
            ResultSet rs = MySQL.execute(sql);
            return rs == null; // If rs is null, the update was successful
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get appointment by ID
    public Appointment getAppointmentById(int appointmentId) {
        String sql = "SELECT * FROM appointments WHERE appointment_id = " + appointmentId;
        
        try {
            ResultSet rs = MySQL.execute(sql);
            if (rs != null && rs.next()) {
                return new Appointment(
                    rs.getInt("appointment_id"),
                    rs.getDate("appointment_date"),
                    rs.getTime("appointment_time"),
                    rs.getInt("patients_patient_id"),
                    rs.getInt("staff_staff_id"),
                    rs.getInt("appointment_status_appointment_status_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public List<Appointment> getAppointmentsByProfessional(int staffId, Date date) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE staff_staff_id = " + staffId +
                " AND appointment_date = '" + date + "'";

        try {
            ResultSet rs = MySQL.execute(sql);
            if (rs != null) {
                while (rs.next()) {
                    Appointment appointment = new Appointment(
                            rs.getInt("appointment_id"),
                            rs.getDate("appointment_date"),
                            rs.getTime("appointment_time"),
                            rs.getInt("patients_patient_id"),
                            rs.getInt("staff_staff_id"),
                            rs.getInt("appointment_status_appointment_status_id")
                    );
                    appointments.add(appointment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_date DESC, appointment_time DESC";

        try {
            ResultSet rs = MySQL.execute(sql);
            if (rs != null) {
                while (rs.next()) {
                    Appointment appointment = new Appointment(
                            rs.getInt("appointment_id"),
                            rs.getDate("appointment_date"),
                            rs.getTime("appointment_time"),
                            rs.getInt("patients_patient_id"),
                            rs.getInt("staff_staff_id"),
                            rs.getInt("appointment_status_appointment_status_id")
                    );
                    appointments.add(appointment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    public boolean updateAppointmentStatus(int appointmentId, int statusId) {
        String sql = "UPDATE appointments SET appointment_status_appointment_status_id = " +
                statusId + " WHERE appointment_id = " + appointmentId;

        try {
            ResultSet rs = MySQL.execute(sql);
            // For UPDATE statements, the MySQL.execute method returns null if successful
            return rs == null; // If rs is null, the update was successful
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Appointment> getAppointmentsByPatient(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patients_patient_id = " + patientId +
                " ORDER BY appointment_date DESC, appointment_time DESC";

        try {
            ResultSet rs = MySQL.execute(sql);
            if (rs != null) {
                while (rs.next()) {
                    Appointment appointment = new Appointment(
                            rs.getInt("appointment_id"),
                            rs.getDate("appointment_date"),
                            rs.getTime("appointment_time"),
                            rs.getInt("patients_patient_id"),
                            rs.getInt("staff_staff_id"),
                            rs.getInt("appointment_status_appointment_status_id")
                    );
                    appointments.add(appointment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }
}