package controller.appointment;

import java.sql.Time;
import java.util.Date;

public class Appointment {
    private int appointmentId;
    private Date appointmentDate;
    private Time appointmentTime;
    private int patientId;
    private int staffId;
    private int appointmentStatusId;

    // Constructors
    public Appointment() {}

    public Appointment(int appointmentId, Date appointmentDate, Time appointmentTime,
                       int patientId, int staffId, int appointmentStatusId) {
        this.appointmentId = appointmentId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.patientId = patientId;
        this.staffId = staffId;
        this.appointmentStatusId = appointmentStatusId;
    }

    // Getters and setters
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }

    public Time getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(Time appointmentTime) { this.appointmentTime = appointmentTime; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public int getAppointmentStatusId() { return appointmentStatusId; }
    public void setAppointmentStatusId(int appointmentStatusId) { this.appointmentStatusId = appointmentStatusId; }
}
