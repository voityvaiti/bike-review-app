package com.myproject.bikereviewapp.entity;

import jakarta.persistence.*;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.time.LocalDate;

@Entity
@Table(name = "review")
@Data
@AllArgsConstructor
@NoArgsConstructor
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
    @EqualsAndHashCode.Exclude
    private Integer likes;

    @Formula("(SELECT COUNT(r.id) FROM Reaction r WHERE r.review_id = id AND r.is_like = 'FALSE')")
    @EqualsAndHashCode.Exclude
    private Integer dislikes;

}
