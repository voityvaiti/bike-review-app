package com.myproject.bikereviewapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "motorcycle")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Motorcycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "model")
    @NotBlank(message = "Model cannot be blank.")
    @Size(max = 50, message = "Model cannot be longer then 50 characters.")
    private String model;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    @NotNull(message = "Brand must be selected.")
    private Brand brand;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "img_id", referencedColumnName = "id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Image image;

    @Formula("(SELECT COUNT(r.id) FROM Review r WHERE r.motorcycle_id = id)")
    @EqualsAndHashCode.Exclude
    private Integer reviewsAmount;

    @Formula("(SELECT AVG(r.rating) FROM Review r WHERE r.motorcycle_id = id)")
    @EqualsAndHashCode.Exclude
    private Float avgRating;

    @OneToMany(mappedBy = "motorcycle")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Review> reviews = new ArrayList<>();

}
