package co.za.turtletech.fuzaserver.model;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Document("Video")
public class Video {
    @Id
    private String Id;

    private int vidOrder;
    private String name;
    private String path;
    private String course;
    private String level;
    private String guid;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public int getVidOrder() {
        return vidOrder;
    }

    public void setVidOrder(int vidOrder) {
        this.vidOrder = vidOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public String toString(){
        return "Video{vidOrder: "+vidOrder+", name: "+name+", course: "+course+", level: "+level+", guid: "+guid+", path: "+path+"}";

    }
}
