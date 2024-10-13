package com.example.bookrent2.model.book;


import com.example.bookrent2.model.image.Image;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String author;
    private String publisher;

    private LocalDate publishedDate;

    private boolean isRented = false;

    /** ***************************************** */

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;


    @Builder
    public Book(String title, String description,
                String author, String publisher, LocalDate publishedDate, boolean isRented) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.isRented = isRented;
    }
}
