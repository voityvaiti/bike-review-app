package com.myproject.bikereviewapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "motorcycle")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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

    @Formula("(SELECT COUNT(r.id) FROM Review r WHERE r.motorcycle_id = id)")
    private Integer reviewsAmount;

    @Formula("(SELECT AVG(r.rating) FROM Review r WHERE r.motorcycle_id = id)")
    private Float avgRating;

    @OneToMany(mappedBy = "motorcycle")
    private List<Review> reviews = new ArrayList<>();


    public Motorcycle(Long id, String model, Brand brand) {
        this.id = id;
        this.model = model;
        this.brand = brand;
    }

    public void setFields(Motorcycle motorcycle) {
        this.brand = motorcycle.brand;
        this.model = motorcycle.model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Motorcycle that = (Motorcycle) o;
        return id.equals(that.id) && Objects.equals(model, that.model) && Objects.equals(brand, that.brand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, model, brand);
    }

    @Override
    public String toString() {
        return "Motorcycle{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", brand=" + brand +
                '}';
    }
}
