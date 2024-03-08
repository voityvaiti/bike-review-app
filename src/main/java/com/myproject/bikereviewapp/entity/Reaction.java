package com.myproject.bikereviewapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "reaction")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_like")
    private boolean isLike;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reaction reaction = (Reaction) o;
        return isLike == reaction.isLike && Objects.equals(id, reaction.id) && Objects.equals(review, reaction.review) && Objects.equals(user, reaction.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isLike, review, user);
    }
}
