package co.za.turtletech.fuzaserver.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_table")
public class Users {
    @Id
    private int ID;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "cell_number")
    private String cellNumber;

    @Column(name = "registered_courses")
    private String registeredCourses;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "active")
    private boolean active;

    public Users() {
    }

    public Users(String companyName, String cellNumber,
                 String registeredCourses, String deviceType, boolean active) {
        this.companyName = companyName;
        this.cellNumber = cellNumber;
        this.registeredCourses = registeredCourses;
        this.deviceType = deviceType;
        this.active = active;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public String getRegisteredCourses() {
        return registeredCourses;
    }

    public void setRegisteredCourses(String registeredCourses) {
        this.registeredCourses = registeredCourses;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
