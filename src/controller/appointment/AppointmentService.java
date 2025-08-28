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
        appointment.setAppointmentStatusId(1); // Assuming 1 is "Scheduled"

        return schedulerMediator.scheduleAppointment(appointment);
    }

    public boolean cancelAppointment(int appointmentId) {
        return schedulerMediator.cancelAppointment(appointmentId);
    }

    public boolean rescheduleAppointment(int appointmentId, Date newDate, Time newTime) {
        return schedulerMediator.rescheduleAppointment(appointmentId, newDate, newTime);
    }

    public List<Time> checkAvailability(int professionalId, Date date) {
        return schedulerMediator.getAvailableSlots(professionalId, date);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentDAO.getAllAppointments();
    }
}
