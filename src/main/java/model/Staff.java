package model;

/**
 * Staff - represents an authorized clinic staff member who can log in
 * and operate the system. Maps to the 'staff' table.
 */
public class Staff {

    private String staffId;
    private String username;
    private String password;
    private String fullName;
    private String role;

    public Staff() {
    }

    public Staff(String staffId, String username, String password, String fullName, String role) {
        this.staffId = staffId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "staffId='" + staffId + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}