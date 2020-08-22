package co.za.turtletech.fuzaserver.model;

public class AdminScreenModel {
    String companyName;
    String cellNumber;
    String course;
    String videoName;
    String watched;
    String date;

    public AdminScreenModel() {
    }

    public AdminScreenModel(String companyName, String cellNumber, String course, String videoName, String watched, String date) {
        this.companyName = companyName;
        this.cellNumber = cellNumber;
        this.course = course;
        this.videoName = videoName;
        this.watched = watched;
        this.date = date;
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

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getWatched() {
        return watched;
    }

    public void setWatched(String watched) {
        this.watched = watched;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
