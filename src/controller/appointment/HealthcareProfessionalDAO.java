package controller.appointment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.MySQL;

public class HealthcareProfessionalDAO {
    
    private LookupDAO lookupDAO;
    
    public HealthcareProfessionalDAO() {
        this.lookupDAO = new LookupDAO();
    }
    
    public List<HealthcareProfessional> getAllDoctors() {
        List<HealthcareProfessional> doctors = new ArrayList<>();
        String sql = "SELECT s.*, r.role_name, st.status_name, b.branch_name " +
                    "FROM staff s " +
                    "LEFT JOIN roles r ON s.roles_role_id = r.role_id " +
                    "LEFT JOIN status st ON s.status_id = st.status_id " +
                    "LEFT JOIN branch b ON s.branch_branch_id = b.branch_id " +
                    "WHERE s.roles_role_id IN (2)";

        try {
            ResultSet rs = MySQL.execute(sql);
            if (rs != null) {
                while (rs.next()) {
                    HealthcareProfessional doctor = new HealthcareProfessional(
                            rs.getInt("staff_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("username"),
                            rs.getString("speciality"),
                            rs.getString("mobile"),
                            rs.getString("license"),
                            rs.getInt("roles_role_id"),
                            rs.getInt("status_id"),
                            rs.getInt("branch_branch_id")
                    );
                    doctors.add(doctor);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return doctors;
    }
    
    public List<HealthcareProfessional> searchDoctors(Integer staffId, String firstName, String lastName, 
                                                     String speciality, String mobile, String license, 
                                                     Integer statusId, Integer branchId) {
        List<HealthcareProfessional> doctors = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT s.*, r.role_name, st.status_name, b.branch_name " +
            "FROM staff s " +
            "LEFT JOIN roles r ON s.roles_role_id = r.role_id " +
            "LEFT JOIN status st ON s.status_id = st.status_id " +
            "LEFT JOIN branch b ON s.branch_branch_id = b.branch_id " +
            "WHERE s.roles_role_id IN (2)"
        );
        
        if (staffId != null) {
            sql.append(" AND s.staff_id = ").append(staffId);
        }
        if (firstName != null && !firstName.trim().isEmpty()) {
            sql.append(" AND s.first_name LIKE '%").append(firstName.trim()).append("%'");
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            sql.append(" AND s.last_name LIKE '%").append(lastName.trim()).append("%'");
        }
        if (speciality != null && !speciality.trim().isEmpty()) {
            sql.append(" AND s.speciality LIKE '%").append(speciality.trim()).append("%'");
        }
        if (mobile != null && !mobile.trim().isEmpty()) {
            sql.append(" AND s.mobile LIKE '%").append(mobile.trim()).append("%'");
        }
        if (license != null && !license.trim().isEmpty()) {
            sql.append(" AND s.license LIKE '%").append(license.trim()).append("%'");
        }
        if (statusId != null) {
            sql.append(" AND s.status_id = ").append(statusId);
        }
        if (branchId != null) {
            sql.append(" AND s.branch_branch_id = ").append(branchId);
        }
        
        try {
            ResultSet rs = MySQL.execute(sql.toString());
            if (rs != null) {
                while (rs.next()) {
                    HealthcareProfessional doctor = new HealthcareProfessional(
                            rs.getInt("staff_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("username"),
                            rs.getString("speciality"),
                            rs.getString("mobile"),
                            rs.getString("license"),
                            rs.getInt("roles_role_id"),
                            rs.getInt("status_id"),
                            rs.getInt("branch_branch_id")
                    );
                    doctors.add(doctor);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return doctors;
    }
    
    public HealthcareProfessional getDoctorById(int staffId) {
        HealthcareProfessional doctor = null;
        String sql = "SELECT s.* FROM staff s WHERE s.staff_id = " + staffId + " AND s.roles_role_id IN (2)";

        try {
            ResultSet rs = MySQL.execute(sql);
            if (rs != null && rs.next()) {
                doctor = new HealthcareProfessional(
                        rs.getInt("staff_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("username"),
                        rs.getString("speciality"),
                        rs.getString("mobile"),
                        rs.getString("license"),
                        rs.getInt("roles_role_id"),
                        rs.getInt("status_id"),
                        rs.getInt("branch_branch_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return doctor;
    }
    
    // Getter for lookupDAO
    public LookupDAO getLookupDAO() {
        return lookupDAO;
    }
}