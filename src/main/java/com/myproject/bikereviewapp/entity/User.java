package com.myproject.bikereviewapp.entity;

import com.myproject.bikereviewapp.validation.annotation.UserPassword;
import com.myproject.bikereviewapp.validation.annotation.UserPublicName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "usr")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true)
    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 19, message = "Username must be between 3 and 19 characters.")
    private String username;

    @Column(name = "password")
    @UserPassword
    private String password;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "public_name")
    @UserPublicName
    private String publicName;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();


    public User(Long id, String username, String password, boolean enabled, Role role, String publicName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.role = role;
        this.publicName = publicName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return enabled == user.enabled && Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(password, user.password) && role == user.role && Objects.equals(publicName, user.publicName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, enabled, role, publicName);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", role=" + role +
                ", publicName='" + publicName + '\'' +
                '}';
    }
}
