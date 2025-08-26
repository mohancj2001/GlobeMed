package controller.role_based_access_control;

import java.util.ArrayList;
import java.util.List;

public class Role implements AccessComponent {
    private int roleId;
    private String name;
    private String description;
    private List<AccessComponent> components = new ArrayList<>();

    public Role(int roleId, String name, String description) {
        this.roleId = roleId;
        this.name = name;
        this.description = description;
    }

    public void addComponent(AccessComponent component) {
        components.add(component);
    }

    @Override
    public boolean hasPermission(String permission) {
        for (AccessComponent comp : components) {
            if (comp.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public int getRoleId() {
        return roleId;
    }

    @Override
    public void displayPermissions(int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("  ");
        }
        System.out.println("Role: " + name + " (ID: " + roleId + ") - " + description);

        for (AccessComponent comp : components) {
            comp.displayPermissions(depth + 1);
        }
    }

    public List<AccessComponent> getComponents() {
        return components;
    }
}