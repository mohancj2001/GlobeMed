package controller.role_based_access_control;

import java.util.ArrayList;
import java.util.List;

public class MedicalStaff {
    private int staffId;
    private String firstName;
    private String lastName;
    private List<Role> roles = new ArrayList<>();

    public MedicalStaff(int staffId, String firstName, String lastName) {
        this.staffId = staffId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public boolean hasPermission(String permission) {
        for (Role role : roles) {
            if (role.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    public void displayPermissions() {
        System.out.println("Permissions for " + firstName + " " + lastName + " (ID: " + staffId + "):");
        for (Role role : roles) {
            role.displayPermissions(1);
        }
    }

    public int getStaffId() {
        return staffId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<Role> getRoles() {
        return roles;
    }
}