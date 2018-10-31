package com.devin.bhsb.model;

import com.devin.bhsb.model.audit.UserDateAudit;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
public class Book extends UserDateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 12)
    private Long isbn;

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 100)
    private String genre;

    @NotBlank
    @Size(max = 140)
    private String content;

    @NotBlank
    @Size(max = 300)
    private String imageUrl;

    @OneToMany(
            mappedBy = "book",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @Size(min = 1, max = 6)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 30)
    private List<Author> authors = new ArrayList<>();

    @OneToMany(
            mappedBy = "book",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @Size(min = 2, max = 6)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 30)
    private List<Copy> copies = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIsbn() {
        return isbn;
    }

    public void setIsbn(Long isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public List<Copy> getCopies() {
        return copies;
    }

    public void setCopies(List<Copy> copies) {
        this.copies = copies;
    }

    public void addAuthor(Author author) {
        authors.add(author);
        author.setBook(this);
    }

    public void removeAuthor(Author author) {
        authors.remove(author);
        author.setBook(this);
    }

    public void addACopy(Copy copy) {
        copies.add(copy);
        copy.setBook(this);
    }

    public void removeCopy(Copy copy) {
        copies.remove(copy);
        copy.setBook(this);
    }
}
