package controller.appointment;

import java.sql.Time;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

public class SchedulerMediator {
    private AppointmentDAO appointmentDAO;
    private HealthcareProfessionalDAO professionalDAO;

    public SchedulerMediator(AppointmentDAO appointmentDAO, HealthcareProfessionalDAO professionalDAO) {
        this.appointmentDAO = appointmentDAO;
        this.professionalDAO = professionalDAO;
    }

    public boolean scheduleAppointment(Appointment appointment) {
        // Check if the professional is available at the requested time
        if (!isProfessionalAvailable(appointment.getStaffId(),
                (Date) appointment.getAppointmentDate(),
                appointment.getAppointmentTime())) {
            System.out.println("Professional is not available at the requested time.");
            return false;
        }

        return appointmentDAO.createAppointment(appointment);
    }

    public boolean cancelAppointment(int appointmentId) {
        return appointmentDAO.updateAppointmentStatus(appointmentId, AppointmentDAO.STATUS_CANCELLED);
    }

    public boolean completeAppointment(int appointmentId) {
        return appointmentDAO.updateAppointmentStatus(appointmentId, AppointmentDAO.STATUS_COMPLETED);
    }

    public boolean rescheduleAppointment(int appointmentId, Date newDate, Time newTime) {
        // Get the existing appointment
        Appointment existingAppointment = appointmentDAO.getAppointmentById(appointmentId);
        if (existingAppointment == null) {
            return false;
        }

        // Check if the professional is available at the new time
        if (!isProfessionalAvailable(existingAppointment.getStaffId(), newDate, newTime)) {
            System.out.println("Professional is not available at the new time.");
            return false;
        }

        // Update the appointment with new date and time
        existingAppointment.setAppointmentDate(newDate);
        existingAppointment.setAppointmentTime(newTime);
        existingAppointment.setAppointmentStatusId(AppointmentDAO.STATUS_SCHEDULED);

        return appointmentDAO.updateAppointment(existingAppointment);
    }

    public boolean isProfessionalAvailable(int professionalId, Date date, Time time) {
        List<Appointment> existingAppointments = appointmentDAO.getAppointmentsByProfessional(professionalId, date);

        for (Appointment appointment : existingAppointments) {
            if (appointment.getAppointmentTime().equals(time) &&
                    appointment.getAppointmentStatusId() != AppointmentDAO.STATUS_CANCELLED) {
                return false; // Professional already has an appointment at this time
            }
        }

        return true;
    }

    public List<Time> getAvailableSlots(int professionalId, Date date) {
        // This would typically check working hours, breaks, etc.
        // For simplicity, we'll assume 9 AM to 5 PM with 30-minute slots
        List<Time> allSlots = generateTimeSlots();
        List<Appointment> bookedAppointments = appointmentDAO.getAppointmentsByProfessional(professionalId, date);

        // Remove booked slots
        for (Appointment appointment : bookedAppointments) {
            if (appointment.getAppointmentStatusId() != AppointmentDAO.STATUS_CANCELLED) {
                allSlots.remove(appointment.getAppointmentTime());
            }
        }

        return allSlots;
    }

    private List<Time> generateTimeSlots() {
        List<Time> slots = new ArrayList<>();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 9);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);

        for (int i = 0; i < 16; i++) { // 8 hours * 2 slots per hour
            slots.add(new Time(cal.getTimeInMillis()));
            cal.add(java.util.Calendar.MINUTE, 30);
        }

        return slots;
    }
}