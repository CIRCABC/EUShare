package eu.europa.circabc.eushare.storage.dto;

import java.time.LocalDateTime;

public class LastLogDTO {
    private String id;
    private String email;
    private String name;
    private String username;
    private Long total_space;
    private LocalDateTime last_logged;

    
    public LastLogDTO(String id, String email, String name, String username, Long total_space,
            LocalDateTime last_logged) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.username = username;
        this.total_space = total_space;
        this.last_logged = last_logged;
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Long getTotal_space() {
        return total_space;
    }
    public void setTotal_space(Long total_space) {
        this.total_space = total_space;
    }
    public LocalDateTime getLast_logged() {
        return last_logged;
    }
    public void setLast_logged(LocalDateTime last_logged) {
        this.last_logged = last_logged;
    }
   
}
