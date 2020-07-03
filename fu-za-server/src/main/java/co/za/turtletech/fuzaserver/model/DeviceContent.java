package co.za.turtletech.fuzaserver.model;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;

@Document("DeviceContent")
public class DeviceContent {
    @Id
    private String id;

    private String appRegistrationId;
    private List<String> contentOnDevice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppRegistrationId() {
        return appRegistrationId;
    }

    public void setAppRegistrationId(String appRegistrationId) {
        this.appRegistrationId = appRegistrationId;
    }

    public List<String> getContentOnDevice() {
        return contentOnDevice;
    }

    public void setContentOnDevice(List<String> contentOnDevice) {
        this.contentOnDevice = contentOnDevice;
    }
}
