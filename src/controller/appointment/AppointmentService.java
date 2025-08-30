package controller.appointment;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class AppointmentService {

    private SchedulerMediator schedulerMediator;
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;

    public AppointmentService(SchedulerMediator schedulerMediator,
            PatientDAO patientDAO,
            AppointmentDAO appointmentDAO) {
        this.schedulerMediator = schedulerMediator;
        this.patientDAO = patientDAO;
        this.appointmentDAO = appointmentDAO;
    }

    public boolean bookAppointment(int patientId, int professionalId,
            Date date, Time time) {
        // Verify patient exists
        Patient patient = patientDAO.getPatientById(patientId);
        if (patient == null) {
            System.out.println("Patient not found.");
            return false;
        }

        Appointment appointment = new Appointment();
        appointment.setPatientId(patientId);
        appointment.setStaffId(professionalId);
        appointment.setAppointmentDate(date);
        appointment.setAppointmentTime(time);
        // Status will be set to SCHEDULED by the DAO

        return schedulerMediator.scheduleAppointment(appointment);
    }

    public boolean cancelAppointment(int appointmentId) {
        return schedulerMediator.cancelAppointment(appointmentId);
    }

    public boolean completeAppointment(int appointmentId) {
        return schedulerMediator.completeAppointment(appointmentId);
    }

    public boolean rescheduleAppointment(int appointmentId, Date newDate, Time newTime) {
        return schedulerMediator.rescheduleAppointment(appointmentId, newDate, newTime);
    }

    // New method for full appointment updates
    public boolean updateAppointment(int appointmentId, Date date, Time time, int statusId) {
        // Get the existing appointment to preserve patient and professional
        Appointment existingAppointment = appointmentDAO.getAppointmentById(appointmentId);
        if (existingAppointment == null) {
            System.out.println("Appointment not found.");
            return false;
        }

        // Verify status is valid
        if (statusId < 1 || statusId > 3) {
            System.out.println("Invalid status ID.");
            return false;
        }

        // Create updated appointment object (preserve patient and professional)
        Appointment appointment = new Appointment(
                appointmentId,
                date,
                time,
                existingAppointment.getPatientId(), // Keep original patient
                existingAppointment.getStaffId(), // Keep original professional
                statusId
        );

        // For date/time changes, use the scheduler mediator to check availability
        if (!existingAppointment.getAppointmentDate().equals(date)
                || !existingAppointment.getAppointmentTime().equals(time)) {
            if (!schedulerMediator.isProfessionalAvailable(
                    existingAppointment.getStaffId(), date, time)) {
                System.out.println("Professional is not available at the requested time.");
                return false;
            }
        }

        return appointmentDAO.updateAppointment(appointment);
    }

    public List<Time> checkAvailability(int professionalId, Date date) {
        return schedulerMediator.getAvailableSlots(professionalId, date);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentDAO.getAllAppointments();
    }

    public Appointment getAppointmentById(int appointmentId) {
        return appointmentDAO.getAppointmentById(appointmentId);
    }

    public List<Appointment> getAppointmentsByPatient(int patientId) {
        return appointmentDAO.getAppointmentsByPatient(patientId);
    }

    public List<Appointment> getAppointmentsByProfessional(int staffId, Date date) {
        return appointmentDAO.getAppointmentsByProfessional(staffId, date);
    }
}
