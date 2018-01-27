package se.vgregion.vatskenutrition.model;


import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class ApplicationUser {
    private long id;
    private String username;
    private String password;
    private List<GrantedAuthority> authorities;

    public long getId() {
        return id;
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

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
}