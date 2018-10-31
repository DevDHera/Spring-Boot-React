package com.devin.bhsb.util;

import com.devin.bhsb.model.Book;
import com.devin.bhsb.model.User;
import com.devin.bhsb.payload.AuthorResponse;
import com.devin.bhsb.payload.BookResponse;
import com.devin.bhsb.payload.CopyResponse;
import com.devin.bhsb.payload.UserSummery;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelMapper {
    public static BookResponse mapBookToBookResponse(Book book, Map<Long, Long> copyBorrowsMap, Map<Long, Long> authorBorrowsMap, User creator, Long userBorrow) {
        BookResponse bookResponse = new BookResponse();
        bookResponse.setId(book.getId());
        bookResponse.setIsbn(book.getIsbn());
        bookResponse.setTitle(book.getTitle());
        bookResponse.setGenre(book.getGenre());
        bookResponse.setContent(book.getContent());
        bookResponse.setImageUrl(book.getImageUrl());

        List<CopyResponse> copyResponses = book.getCopies().stream().map(copy -> {
            CopyResponse copyResponse = new CopyResponse();
            copyResponse.setId(copy.getId());
            copyResponse.setStatus(copy.getStatus());

            if (copyBorrowsMap.containsKey(copy.getId())) {
                copyResponse.setBorrowCount(copyBorrowsMap.get(copy.getId()));
            } else {
                copyResponse.setBorrowCount(0);
            }
            return copyResponse;
        }).collect(Collectors.toList());

        bookResponse.setCopies(copyResponses);

        List<AuthorResponse> authorResponses = book.getAuthors().stream().map(author -> {
            AuthorResponse authorResponse = new AuthorResponse();
            authorResponse.setId(author.getId());
            authorResponse.setName(author.getName());

            if (authorBorrowsMap.containsKey(author.getId())) {
                authorResponse.setBorrowCount(authorBorrowsMap.get(author.getId()));
            } else {
                authorResponse.setBorrowCount(0);
            }
            return authorResponse;
        }).collect(Collectors.toList());

        bookResponse.setAuthors(authorResponses);

        UserSummery creatorSummery = new UserSummery(creator.getId(), creator.getUsername(), creator.getName());
        bookResponse.setCreatedBy(creatorSummery);

        if(userBorrow != null) {
            bookResponse.setSelectedCopy(userBorrow);
        }

        long totalBorrows = bookResponse.getCopies().stream().mapToLong(CopyResponse::getBorrowCount).sum();
        bookResponse.setTotalBorrows(totalBorrows);

        return bookResponse;
    }
}
