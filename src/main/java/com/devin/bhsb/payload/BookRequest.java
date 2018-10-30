package com.devin.bhsb.payload;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class BookRequest {
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

    @NotNull
    @Size(min = 1, max = 6)
    @Valid
    private List<AuthorRequest> authors;

    @NotNull
    @Size(min = 2, max = 6)
    @Valid
    private List<CopyRequest> copies;

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

    public List<AuthorRequest> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorRequest> authors) {
        this.authors = authors;
    }

    public List<CopyRequest> getCopies() {
        return copies;
    }

    public void setCopies(List<CopyRequest> copies) {
        this.copies = copies;
    }
}
