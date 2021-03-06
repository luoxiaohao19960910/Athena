package com.athena.model.security;

import io.swagger.annotations.ApiModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Created by tommy on 2017/3/22.
 */
@ApiModel(value="Account",description = "Account that represents User info")
public class Account implements UserDetails {

    private User user;

    public Account() {
        this.user = new User();
    }

    public Account(User user) {
        this.user = user;
    }

    public Long getId() {
        return user.getId();
    }

    public void setId(Long id) {
        user.setId(id);
    }

    public User getUser() {
        return Objects.requireNonNull(this.user);
    }

    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public void setUsername(String username) {
        user.setUsername(username);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String identity : user.getIdentity()) {
            authorities.add(new SimpleGrantedAuthority(identity));
        }
        return authorities;
    }

    public String getPassword() {
        return user.getPassword();
    }

    public void setPassword(String password) {
        user.setPassword(password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        return user != null ? user.equals(account.user) : account.user == null;
    }

    @Override
    public int hashCode() {
        return user != null ? user.hashCode() : 0;
    }

    @Override
    public String toString() {
        String authentiries = this.getAuthorities().toString();
        return "[" + authentiries + "]" + this.user.getUsername();
    }
}
