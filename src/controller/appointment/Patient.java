package controller.appointment;

import java.util.Date;

public class Patient {
    private int patientId;
    private String firstName;
    private String lastName;
    private Date dob;
    private int genderId;
    private String contactNumber;
    private String email;
    private String address;
    private Date createdAt;

    // Constructors
    public Patient() {}

    public Patient(int patientId, String firstName, String lastName, Date dob,
                   int genderId, String contactNumber, String email, String address, Date createdAt) {
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.genderId = genderId;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }

    public int getGenderId() { return genderId; }
    public void setGenderId(int genderId) { this.genderId = genderId; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
