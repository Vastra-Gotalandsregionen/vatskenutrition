package se.vgregion.vatskenutrition.model.jwt;


import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class JwtUser {
    private String username;
    private List<GrantedAuthority> authorities;
    private String displayName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}