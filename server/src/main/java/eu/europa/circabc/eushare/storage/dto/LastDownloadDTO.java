package eu.europa.circabc.eushare.storage.dto;

import java.time.LocalDateTime;

public class LastDownloadDTO {
    private String uploader_email;
    private String recipient;
    private String filename;
    private String path;
    private String password;
    private String shorturl;
    private Boolean download_notification;
    private LocalDateTime download_date;



    
    public LastDownloadDTO(String uploader_email, String recipient, String filename, String path, String password,
            String shorturl, Boolean download_notification, LocalDateTime download_date) {
        this.uploader_email = uploader_email;
        this.recipient = recipient;
        this.filename = filename;
        this.path = path;
        this.password = password;
        this.shorturl = shorturl;
        this.download_notification = download_notification;
        this.download_date = download_date;
    }
    
    public String getUploader_email() {
        return uploader_email;
    }
    public void setUploader_email(String uploader_email) {
        this.uploader_email = uploader_email;
    }
    public String getRecipient() {
        return recipient;
    }
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getShorturl() {
        return shorturl;
    }
    public void setShorturl(String shorturl) {
        this.shorturl = shorturl;
    }
    public Boolean getDownload_notification() {
        return download_notification;
    }
    public void setDownload_notification(Boolean download_notification) {
        this.download_notification = download_notification;
    }
    public LocalDateTime getDownload_date() {
        return download_date;
    }
    public void setDownload_date(LocalDateTime download_date) {
        this.download_date = download_date;
    }
    
}
