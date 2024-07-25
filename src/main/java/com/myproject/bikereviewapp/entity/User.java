package com.myproject.bikereviewapp.entity;

import com.myproject.bikereviewapp.validation.annotation.UserPassword;
import com.myproject.bikereviewapp.validation.annotation.UserPublicName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usr")
@Data
@AllArgsConstructor
@NoArgsConstructor
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
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private String password;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "public_name")
    @UserPublicName
    private String publicName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "img_id", referencedColumnName = "id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Image image;

    @OneToMany(mappedBy = "user")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Review> reviews = new ArrayList<>();

}
