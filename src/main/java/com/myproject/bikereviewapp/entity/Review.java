package com.myproject.bikereviewapp.entity;

import jakarta.persistence.*;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "review")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Review {

    public static final short MIN_RATING = 1;
    public static final short MAX_RATING = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "body")
    @NotBlank(message = "Review is blank.")
    @Size(min = 5, max = 999, message = "Review must be between 5 and 999 characters.")
    private String body;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "rating")
    @NotNull(message = "Rating is required.")
    @Min(MIN_RATING)
    @Max(MAX_RATING)
    private Short rating;

    @ManyToOne
    @JoinColumn(name = "motorcycle_id")
    private Motorcycle motorcycle;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Formula("(SELECT COUNT(r.id) FROM Reaction r WHERE r.review_id = id AND r.is_like = 'TRUE')")
    private Integer likes;

    @Formula("(SELECT COUNT(r.id) FROM Reaction r WHERE r.review_id = id AND r.is_like = 'FALSE')")
    private Integer dislikes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Objects.equals(id, review.id) && Objects.equals(body, review.body) && Objects.equals(publicationDate, review.publicationDate) && Objects.equals(rating, review.rating) && Objects.equals(motorcycle, review.motorcycle) && Objects.equals(user, review.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, body, publicationDate, rating, motorcycle, user);
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", body='" + body + '\'' +
                ", motorcycle=" + motorcycle +
                ", user=" + user +
                '}';
    }
}
