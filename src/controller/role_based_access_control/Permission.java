package controller.role_based_access_control;

public class Permission implements AccessComponent {
    private int permissionId;
    private String name;
    private String description;

    public Permission(int permissionId, String name, String description) {
        this.permissionId = permissionId;
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.name.equals(permission);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public int getPermissionId() {
        return permissionId;
    }

    @Override
    public void displayPermissions(int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("  ");
        }
        System.out.println("- " + name + " (ID: " + permissionId + "): " + description);
    }
}