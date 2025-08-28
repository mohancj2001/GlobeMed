package controller.appointment;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import model.MySQL;

public class AppointmentDAO {
    public boolean createAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (appointment_date, appointment_time, " +
                "patients_patient_id, staff_staff_id, appointment_status_appointment_status_id) " +
                "VALUES ('" + appointment.getAppointmentDate() + "', '" + appointment.getAppointmentTime() +
                "', " + appointment.getPatientId() + ", " + appointment.getStaffId() +
                ", " + appointment.getAppointmentStatusId() + ")";

        try {
            ResultSet rs = MySQL.execute(sql);

            // For INSERT statements, we need to get the generated keys differently
            // Since our MySQL class doesn't return generated keys, we'll need to query for the last insert ID
            if (rs == null) { // INSERT was successful
                // Get the last inserted ID
                ResultSet generatedKeys = MySQL.execute("SELECT LAST_INSERT_ID()");
                if (generatedKeys != null && generatedKeys.next()) {
                    appointment.setAppointmentId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
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
}