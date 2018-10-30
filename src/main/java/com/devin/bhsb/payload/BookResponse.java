package com.devin.bhsb.payload;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class BookResponse {
    private Long id;
    private Long isbn;
    private String title;
    private String genre;
    private String content;
    private String imageUrl;
    private List<AuthorResponse> authors;
    private List<CopyResponse> copies;
    private UserSummery createdBy;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long selectedCopy;
    private Long totalBorrows;

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

    public List<AuthorResponse> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorResponse> authors) {
        this.authors = authors;
    }

    public List<CopyResponse> getCopies() {
        return copies;
    }

    public void setCopies(List<CopyResponse> copies) {
        this.copies = copies;
    }

    public UserSummery getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserSummery createdBy) {
        this.createdBy = createdBy;
    }

    public Long getSelectedCopy() {
        return selectedCopy;
    }

    public void setSelectedCopy(Long selectedCopy) {
        this.selectedCopy = selectedCopy;
    }

    public Long getTotalBorrows() {
        return totalBorrows;
    }

    public void setTotalBorrows(Long totalBorrows) {
        this.totalBorrows = totalBorrows;
    }
}
