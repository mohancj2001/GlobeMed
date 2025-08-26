package controller.role_based_access_control;

import model.MySQL;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RBACSystem {
    private List<Role> availableRoles = new ArrayList<>();
    private List<Permission> availablePermissions = new ArrayList<>();

    public void loadFromDatabase() {
        loadPermissions();
        loadRoles();
        linkRolesAndPermissions();
    }

    private void loadPermissions() {
        try {
            String query = "SELECT permission_id, permission_name FROM permissions";
            ResultSet rs = MySQL.execute(query);

            while (rs != null && rs.next()) {
                int permissionId = rs.getInt("permission_id");
                String permissionName = rs.getString("permission_name");

                Permission permission = new Permission(permissionId, permissionName, 
                    "Permission: " + permissionName);
                availablePermissions.add(permission);
            }
            System.out.println("Loaded " + availablePermissions.size() + " permissions");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRoles() {
        try {
            String query = "SELECT role_id, role_name FROM roles";
            ResultSet rs = MySQL.execute(query);

            while (rs != null && rs.next()) {
                int roleId = rs.getInt("role_id");
                String roleName = rs.getString("role_name");

                Role role = new Role(roleId, roleName, "Role: " + roleName);
                availableRoles.add(role);
            }
            System.out.println("Loaded " + availableRoles.size() + " roles");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void linkRolesAndPermissions() {
        try {
            String query = "SELECT roles_role_id, permissions_permission_id FROM roles_has_permissions";
            ResultSet rs = MySQL.execute(query);
            int count = 0;

            while (rs != null && rs.next()) {
                int roleId = rs.getInt("roles_role_id");
                int permissionId = rs.getInt("permissions_permission_id");

                Role role = getRoleById(roleId);
                Permission permission = getPermissionById(permissionId);

                if (role != null && permission != null) {
                    role.addComponent(permission);
                    count++;
                } else {
                    System.out.println("Warning: Could not find role " + roleId + " or permission " + permissionId);
                }
            }
            System.out.println("Linked " + count + " role-permission relationships");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Role getRoleById(int roleId) {
        for (Role role : availableRoles) {
            if (role.getRoleId() == roleId) {
                return role;
            }
        }
        return null;
    }

    public Permission getPermissionById(int permissionId) {
        for (Permission permission : availablePermissions) {
            if (permission.getPermissionId() == permissionId) {
                return permission;
            }
        }
        return null;
    }

    public Role getRoleByName(String name) {
        for (Role role : availableRoles) {
            if (role.getName().equals(name)) {
                return role;
            }
        }
        return null;
    }

    public void addRole(Role role) {
        availableRoles.add(role);
    }

    public void addPermission(Permission permission) {
        availablePermissions.add(permission);
    }

    public void displayAllRoles() {
        System.out.println("Available Roles in the System:");
        for (Role role : availableRoles) {
            role.displayPermissions(1);
            System.out.println();
        }
    }

    public MedicalStaff loadStaffMember(int staffId) {
        try {
            String query = "SELECT staff_id, first_name, last_name, roles_role_id FROM staff WHERE staff_id = " + staffId;
            ResultSet rs = MySQL.execute(query);

            if (rs != null && rs.next()) {
                int id = rs.getInt("staff_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                int roleId = rs.getInt("roles_role_id");

                MedicalStaff staff = new MedicalStaff(id, firstName, lastName);
                Role role = getRoleById(roleId);

                if (role != null) {
                    staff.addRole(role);
                    System.out.println("Assigned role '" + role.getName() + "' to staff member " + firstName + " " + lastName);
                } else {
                    System.out.println("Warning: Role ID " + roleId + " not found for staff member " + staffId);
                }

                return staff;
            } else {
                System.out.println("Staff member with ID " + staffId + " not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to check if a staff member has a specific permission using database query
    public static boolean staffHasPermission(int staffId, String permission) {
        try {
            String query = "SELECT COUNT(*) as count " +
                    "FROM staff s " +
                    "JOIN roles r ON s.roles_role_id = r.role_id " +
                    "JOIN roles_has_permissions rp ON r.role_id = rp.roles_role_id " +
                    "JOIN permissions p ON rp.permissions_permission_id = p.permission_id " +
                    "WHERE s.staff_id = " + staffId + " AND p.permission_name = '" + permission + "'";

            ResultSet rs = MySQL.execute(query);
            if (rs != null && rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to get all permissions for a staff member
    public static List<String> getStaffPermissions(int staffId) {
        List<String> permissions = new ArrayList<>();
        try {
            String query = "SELECT p.permission_name " +
                    "FROM staff s " +
                    "JOIN roles r ON s.roles_role_id = r.role_id " +
                    "JOIN roles_has_permissions rp ON r.role_id = rp.roles_role_id " +
                    "JOIN permissions p ON rp.permissions_permission_id = p.permission_id " +
                    "WHERE s.staff_id = " + staffId;

            ResultSet rs = MySQL.execute(query);
            while (rs != null && rs.next()) {
                permissions.add(rs.getString("permission_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return permissions;
    }

    // Debug method to check system state
    public void debugSystem() {
        System.out.println("=== RBAC System Debug ===");
        System.out.println("Available permissions:");
        for (Permission p : availablePermissions) {
            System.out.println("  - " + p.getName() + " (ID: " + p.getPermissionId() + ")");
        }
        
        System.out.println("\nAvailable roles with permissions:");
        for (Role role : availableRoles) {
            System.out.println("Role: " + role.getName() + " (ID: " + role.getRoleId() + ")");
            for (AccessComponent comp : role.getComponents()) {
                if (comp instanceof Permission) {
                    Permission p = (Permission) comp;
                    System.out.println("  - " + p.getName() + " (ID: " + p.getPermissionId() + ")");
                }
            }
        }
    }
}