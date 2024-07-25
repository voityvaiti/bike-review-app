package com.myproject.bikereviewapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "brand")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "Name cannot be blank.")
    @Size(max = 30, message = "Name cannot be longer then 30 characters.")
    private String name;

    @Column(name = "country")
    @NotBlank(message = "Country cannot be blank.")
    @Size(max = 30, message = "Country cannot be longer then 30 characters.")
    private String country;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "img_id", referencedColumnName = "id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Image image;

    @OneToMany(mappedBy = "brand")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Motorcycle> motorcycles = new ArrayList<>();
}
