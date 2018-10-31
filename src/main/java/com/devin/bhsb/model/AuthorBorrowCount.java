package com.devin.bhsb.model;

public class AuthorBorrowCount {
    private Long authorId;
    private Long borrowCount;

    public AuthorBorrowCount(Long authorId, Long borrowCount) {
        this.authorId = authorId;
        this.borrowCount = borrowCount;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getBorrowCount() {
        return borrowCount;
    }

    public void setBorrowCount(Long borrowCount) {
        this.borrowCount = borrowCount;
    }
}
