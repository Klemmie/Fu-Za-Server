package co.za.turtletech.fuzaserver.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "syncing_table")
public class Syncing {
    @Id
    private int ID;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "cell_number")
    private String cellNumber;

    @Column(name = "app_registration_id")
    private String appRegistrationId;

    @Column(name = "registered_courses")
    private String registeredCourses;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "mongo_id")
    private String mongoId;

    @Column(name = "active")
    private boolean active;

    public Syncing() {
    }

    public Syncing(String companyName, String cellNumber, String appRegistrationId,
                   String registeredCourses, String deviceType, boolean active) {
        this.companyName = companyName;
        this.cellNumber = cellNumber;
        this.appRegistrationId = appRegistrationId;
        this.registeredCourses = registeredCourses;
        this.deviceType = deviceType;
        this.active = active;
    }

    public Syncing(String companyName, String cellNumber, String appRegistrationId,
                   String registeredCourses, String deviceType, String mongoId, boolean active) {
        this.companyName = companyName;
        this.cellNumber = cellNumber;
        this.appRegistrationId = appRegistrationId;
        this.registeredCourses = registeredCourses;
        this.deviceType = deviceType;
        this.mongoId = mongoId;
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

    public String getAppRegistrationId() {
        return appRegistrationId;
    }

    public void setAppRegistrationId(String appRegistrationId) {
        this.appRegistrationId = appRegistrationId;
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

    public String getMongoId() {
        return mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
