package ru.kata.spring.boot_security.demo.entity;

import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "Name should not be empty")
    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters")
    private String username;

    @Column(name = "password")
    @NotEmpty(message = "Password should not be empty")
    @NotBlank(message = "Password is required")
    @Size(min = 2, max = 68, message = "Password should be between 2 and 30 characters")
    private String password;

    @NotNull(message = "Year of birth is required")
    @Min(value = 1900, message = "Year of birth must be greater than 1900")
    @Max(value = 2024, message = "Year of birth must be less than 2024")
    @Column(name = "year_of_birth")
    private Integer yearOfBirth;

    @NotEmpty(message = "At least one role is required")
    @Column(name = "roles")
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Role> roles;

    public User() {
    }

    public User(String username, String password, Integer yearOfBirth, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.yearOfBirth = yearOfBirth;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotEmpty(message = "Name should not be empty") @NotBlank(message = "Name should not be blank") @Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters") String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(@NotEmpty(message = "Name should not be empty") @NotBlank(message = "Name should not be blank") @Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters") String username) {
        this.username = username;
    }

    @Override
    public Collection<Role> getAuthorities() {
        return roles;
    }

    public @NotEmpty(message = "Password should not be empty") @NotBlank(message = "Password should not be blank") @Size(min = 2, max = 68, message = "Password should be between 2 and 30 characters") String getPassword() {
        return password;
    }

    public void setPassword(@NotEmpty(message = "Password should not be empty") @NotBlank(message = "Password should not be blank") @Size(min = 2, max = 68, message = "Password should be between 2 and 30 characters") String password) {
        this.password = password;
    }

    public @Min(value = 1900, message = "Year of birth must be over 1900") Integer getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(@Min(value = 1900, message = "Year of birth must be over 1900") Integer yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", yearOfBirth=" + yearOfBirth + '}';
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
            this.roles = roles;
    }
}
