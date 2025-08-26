package controller.role_based_access_control;

interface AccessComponent {
    boolean hasPermission(String permission);
    String getName();
    String getDescription();
    void displayPermissions(int depth);
}