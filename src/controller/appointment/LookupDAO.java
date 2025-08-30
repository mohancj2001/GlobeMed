package controller.appointment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import model.MySQL;

public class LookupDAO {
    
    public Map<Integer, String> getAllRoles() {
        Map<Integer, String> roles = new HashMap<>();
        String sql = "SELECT role_id, role_name FROM roles";
        
        try {
            ResultSet rs = MySQL.execute(sql);
            if (rs != null) {
                while (rs.next()) {
                    roles.put(rs.getInt("role_id"), rs.getString("role_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return roles;
    }
    
    public Map<Integer, String> getAllStatus() {
        Map<Integer, String> status = new HashMap<>();
        String sql = "SELECT status_id, status_name FROM status";
        
        try {
            ResultSet rs = MySQL.execute(sql);
            if (rs != null) {
                while (rs.next()) {
                    status.put(rs.getInt("status_id"), rs.getString("status_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return status;
    }
    
    public Map<Integer, String> getAllBranches() {
        Map<Integer, String> branches = new HashMap<>();
        String sql = "SELECT branch_id, branch_name FROM branch";
        
        try {
            ResultSet rs = MySQL.execute(sql);
            if (rs != null) {
                while (rs.next()) {
                    branches.put(rs.getInt("branch_id"), rs.getString("branch_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return branches;
    }
    
    public String getRoleName(int roleId) {
        String sql = "SELECT role_name FROM roles WHERE role_id = " + roleId;
        
        try {
            ResultSet rs = MySQL.execute(sql);
            if (rs != null && rs.next()) {
                return rs.getString("role_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return "Unknown Role (" + roleId + ")";
    }
    
    public String getStatusName(int statusId) {
        String sql = "SELECT status_name FROM status WHERE status_id = " + statusId;
        
        try {
            ResultSet rs = MySQL.execute(sql);
            if (rs != null && rs.next()) {
                return rs.getString("status_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return "Unknown Status (" + statusId + ")";
    }
    
    public String getBranchName(int branchId) {
        String sql = "SELECT branch_name FROM branch WHERE branch_id = " + branchId;
        
        try {
            ResultSet rs = MySQL.execute(sql);
            if (rs != null && rs.next()) {
                return rs.getString("branch_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return "Unknown Branch (" + branchId + ")";
    }
    
    public Map<Integer, String> getAppointmentStatuses() {
        Map<Integer, String> statuses = new HashMap<>();
        String sql = "SELECT appointment_status_id, appointment_status_name FROM appointment_status";
        
        try {
            ResultSet rs = MySQL.execute(sql);
            if (rs != null) {
                while (rs.next()) {
                    statuses.put(rs.getInt("appointment_status_id"), rs.getString("appointment_status_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return statuses;
    }
    
    public String getAppointmentStatusName(int statusId) {
        String sql = "SELECT appointment_status_name FROM appointment_status WHERE appointment_status_id = " + statusId;
        
        try {
            ResultSet rs = MySQL.execute(sql);
            if (rs != null && rs.next()) {
                return rs.getString("appointment_status_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return "Unknown Status (" + statusId + ")";
    }
}