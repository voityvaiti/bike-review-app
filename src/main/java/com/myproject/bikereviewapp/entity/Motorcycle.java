package com.myproject.bikereviewapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String model;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "motorcycle")
    private List<Review> reviews = new ArrayList<>();


    public Motorcycle(Long id, String model, Brand brand) {
        this.id = id;
        this.model = model;
        this.brand = brand;
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
