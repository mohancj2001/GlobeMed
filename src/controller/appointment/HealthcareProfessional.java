package controller.appointment;

public class HealthcareProfessional {
    private int staffId;
    private String firstName;
    private String lastName;
    private String username;
    private String speciality;
    private String mobile;
    private String license;
    private int roleId;
    private int statusId;
    private int branchId;

    // Constructors
    public HealthcareProfessional() {}

    public HealthcareProfessional(int staffId, String firstName, String lastName, String username,
                                  String speciality, String mobile, String license,
                                  int roleId, int statusId, int branchId) {
        this.staffId = staffId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.speciality = speciality;
        this.mobile = mobile;
        this.license = license;
        this.roleId = roleId;
        this.statusId = statusId;
        this.branchId = branchId;
    }

    // Getters and setters
    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSpeciality() { return speciality; }
    public void setSpeciality(String speciality) { this.speciality = speciality; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getLicense() { return license; }
    public void setLicense(String license) { this.license = license; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public int getStatusId() { return statusId; }
    public void setStatusId(int statusId) { this.statusId = statusId; }

    public int getBranchId() { return branchId; }
    public void setBranchId(int branchId) { this.branchId = branchId; }

    @Override
    public String toString() {
        return "Dr. " + firstName + " " + lastName + " (" + speciality + ")";
    }
}
