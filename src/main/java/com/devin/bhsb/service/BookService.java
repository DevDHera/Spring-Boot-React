package com.devin.bhsb.service;

import com.devin.bhsb.exception.BadRequestException;
import com.devin.bhsb.exception.ResourceNotFoundException;
import com.devin.bhsb.model.*;
import com.devin.bhsb.payload.BookRequest;
import com.devin.bhsb.payload.BookResponse;
import com.devin.bhsb.payload.BorrowRequest;
import com.devin.bhsb.payload.PagedResponse;
import com.devin.bhsb.repository.BookRepository;
import com.devin.bhsb.repository.BorrowRepository;
import com.devin.bhsb.repository.UserRepository;
import com.devin.bhsb.security.UserPrincipal;
import com.devin.bhsb.util.AppConstants;
import com.devin.bhsb.util.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    public PagedResponse<BookResponse> getAllBooks(UserPrincipal currentUser, int page, int size) {
        validatePageNumberAndSize(page, size);

        // Retrive Books
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Book> books = bookRepository.findAll(pageable);

        if (books.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), books.getNumber(),
                    books.getSize(), books.getTotalElements(), books.getTotalPages(), books.isLast());
        }

        // Map Books to BookResponses containing borrow counts
        List<Long> bookIds = books.map(Book::getId).getContent();
        Map<Long, Long>  copyBorrowCountMap = getCopyBorrowCountMap(bookIds);
        Map<Long, Long>  authorBorrowCountMap = getAuthorBorrowCountMap(bookIds);
        Map<Long, Long> bookUserBorrowMap = getBookUserBorrowMap(currentUser, bookIds);
        Map<Long, User> creatorMap = getBookCreatorMap(books.getContent());

        List<BookResponse> bookResponses = books.map(book -> {
            return ModelMapper.mapBookToBookResponse(book,
                    copyBorrowCountMap,
                    authorBorrowCountMap,
                    creatorMap.get(book.getCreatedBy()),
                    bookUserBorrowMap == null ? null : bookUserBorrowMap.getOrDefault(book.getId(), null));
        }).getContent();

        return new PagedResponse<>(bookResponses, books.getNumber(),
                books.getSize(), books.getTotalElements(), books.getTotalPages(), books.isLast());
    }

    public PagedResponse<BookResponse> getBooksCreatedBy(String username, UserPrincipal currentUser, int page, int size) {
        validatePageNumberAndSize(page, size);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Retrive all books created by the given username
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Book> books = bookRepository.findByCreatedBy(user.getId(), pageable);

        if (books.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), books.getNumber(),
                    books.getSize(), books.getTotalElements(), books.getTotalPages(), books.isLast());
        }

        // Map Polls to PollResponses containing borrow counts
        List<Long> bookIds = books.map(Book::getId).getContent();
        Map<Long, Long> copyBorrowCountMap = getCopyBorrowCountMap();
        Map<Long, Long> authorBorrowCountMap = getAuthorBorrowCountMap();
        Map<Long, Long> bookUserBorrowMap = getBookUserBorrowMap();

        List<BookResponse> bookResponses = books.map(book -> {
            return ModelMapper.mapBookToBookResponse(book,
                    copyBorrowCountMap,
                    authorBorrowCountMap,
                    user,
                    bookUserBorrowMap == null ? null : bookUserBorrowMap.getOrDefault(book.getId(), null));
        }).getContent();

        return new PagedResponse<>(bookResponses, books.getNumber(),
                books.getSize(), books.getTotalElements(), books.getTotalPages(), books.isLast());
    }

    public PagedResponse<BookResponse> getBooksBorrowedBy(String username, UserPrincipal currentUser, int page, int size) {
        validatePageNumberAndSize(page, size);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Retrieve all bookIds in which given username has borrowed
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Long> userBorrowedBookIds = borrowRepository.findBorrowedBooksByUserId(user.getId(), pageable);

        if (userBorrowedBookIds.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), userBorrowedBookIds.getNumber(),
                    userBorrowedBookIds.getSize(), userBorrowedBookIds.getTotalElements(),
                    userBorrowedBookIds.getTotalPages(), userBorrowedBookIds.isLast());
        }

        // Retrieve all book details from borrowed bookIds
        List<Long> bookIds = userBorrowedBookIds.getContent();

        Sort sort = new Sort(Sort.Direction.DESC, "createdAt");
        List<Book> books = bookRepository.findByIdIn(bookIds, sort);

        // Map Books to BookResponses containing borrow count and book creator details
        Map<Long, Long> copyBorrowCountMap = getCopyBorrowCountMap(bookIds);
        Map<Long, Long> authorBorrowCountMap = getAuthorBorrowCountMap(bookIds);
        Map<Long, Long> bookUserBorrowMap = getBookUserBorrowMap(currentUser, bookIds);
        Map<Long, User> creatorMap = getBookCreatorMap(books);

        List<BookResponse> bookResponses = books.stream().map(book -> {
            return ModelMapper.mapBookToBookResponse(book,
                    copyBorrowCountMap,
                    authorBorrowCountMap,
                    creatorMap.get(book.getCreatedBy()),
                    bookUserBorrowMap == null ? null : bookUserBorrowMap.getOrDefault(book.getId(), null));
        }).collect(Collectors.toList());

        return new PagedResponse<>(bookResponses, userBorrowedBookIds.getNumber(), userBorrowedBookIds.getSize(), userBorrowedBookIds.getTotalElements(), userBorrowedBookIds.getTotalPages(), userBorrowedBookIds.isLast());
    }

    public Book createBook(BookRequest bookRequest) {
        Book book = new Book();
        book.setIsbn(bookRequest.getIsbn());
        book.setTitle(bookRequest.getTitle());
        book.setGenre(bookRequest.getGenre());
        book.setContent(book.getContent());
        book.setImageUrl(book.getImageUrl());

        bookRequest.getCopies().forEach(copyRequest -> {
            book.addACopy(new Copy(copyRequest.getStatus()));
        });

        bookRequest.getAuthors().forEach(authorRequest -> {
            book.addAuthor(new Author(authorRequest.getName()));
        });

        return bookRepository.save(book);
    }

    public BookResponse getBookById(Long bookId, UserPrincipal currentUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new ResourceNotFoundException("Book", "id", bookId)
        );

        // Retrieve Borrow Counts of every copy belonging to the current book
        List<CopyBorrowCount> borrows = borrowRepository.countByBookIdGroupByCopyId(bookId);

        Map<Long, Long> copyBorrowsMap = borrows.stream()
                .collect(Collectors.toMap(CopyBorrowCount::getCopyId, CopyBorrowCount::getBorrowCount));

        // Retrieve Borrow Counts of every author belonging to the current book
        List<AuthorBorrowCount> authorBorrowCounts = borrowRepository.countByBookIdInGroupByAuthorId(bookId);

        Map<Long, Long> authorBorrowsMap = authorBorrowCounts.stream()
                .collect(Collectors.toMap(AuthorBorrowCount::getAuthorId, AuthorBorrowCount::getBorrowCount));

        // Retrieve book creator details
        User creator = userRepository.findById(book.getCreatedBy())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", book.getCreatedBy()));

        // Retrieve borrows done by logged in user
        Borrow userBorrow = null;
        if (currentUser != null) {
            userBorrow = borrowRepository.findByUserIdAndBookId(currentUser.getId(), bookId);
        }

        return ModelMapper.mapBookToBookResponse(book, copyBorrowsMap, authorBorrowsMap,
                creator, userBorrow != null ? userBorrow.getCopy().getId(): null);
    }

    public BookResponse castBorrowAndGetUpdatedBook(Long bookId, BorrowRequest borrowRequest, UserPrincipal currentUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));

//        if ()

        User user = userRepository.getOne(currentUser.getId());

        Copy selectedCopy = book.getCopies().stream()
                .filter(copy -> copy.getId().equals(borrowRequest.getCopyId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Copy", "id", borrowRequest.getCopyId()));

        Borrow borrow = new Borrow();
        borrow.setBook(book);
        borrow.setUser(user);
        borrow.setCopy(selectedCopy);

        try {
            borrow = borrowRepository.save(borrow);
        } catch (DataIntegrityViolationException ex) {
            logger.info("User {} has already borrowed the Book {}", currentUser.getId(), bookId );
            throw new BadRequestException("Sorry! You have already borrowed this book");
        }

        //-- Borrow Saved, Return the updated Book Response now --

        // Retrieve Borrow Counts of every copy belonging to the current book
        List<CopyBorrowCount> copyBorrowCounts = borrowRepository.countByBookIdGroupByCopyId(bookId);

        Map<Long, Long> copyBorrowsMap = copyBorrowCounts.stream()
                .collect(Collectors.toMap(CopyBorrowCount::getCopyId, CopyBorrowCount::getBorrowCount));

        // Retrieve Borrow Counts of every author belonging to the current book
        List<AuthorBorrowCount> authorBorrowCounts = borrowRepository.countByBookIdInGroupByAuthorId(bookId);

        Map<Long, Long> authorBorrowsMap = authorBorrowCounts.stream()
                .collect(Collectors.toMap(AuthorBorrowCount::getAuthorId, AuthorBorrowCount::getBorrowCount));


        // Retrieve book creator details
        User creator = userRepository.findById(book.getCreatedBy())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", book.getCreatedBy()));

        return ModelMapper.mapBookToBookResponse(book, copyBorrowsMap, authorBorrowsMap, creator, borrow.getCopy().getId());
    }

    private void validatePageNumberAndSize(int page, int size) {
        if(page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if(size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }

    private Map<Long, Long> getCopyBorrowCountMap(List<Long> bookIds) {
        // Retrieve Borrow Counts of every copy belonging to the given bookIds
        List<CopyBorrowCount> borrows = borrowRepository.countByBookIdInGroupByCopyId(bookIds);

        Map<Long, Long> copyBorrowsMap = borrows.stream()
                .collect(Collectors.toMap(CopyBorrowCount::getCopyId, CopyBorrowCount::getBorrowCount));

        return copyBorrowsMap;
    }

    private Map<Long, Long> getAuthorBorrowCountMap(List<Long> bookIds) {
        // Retrieve Borrow Counts of every author belonging to the given bookIds
        List<AuthorBorrowCount> borrows = borrowRepository.countByBookIdInGroupByAuthorId(bookIds);

        Map<Long, Long> authorBorrowsMap = borrows.stream()
                .collect(Collectors.toMap(AuthorBorrowCount::getAuthorId, AuthorBorrowCount::getBorrowCount));

        return authorBorrowsMap;
    }

    private Map<Long, Long> getBookUserBorrowMap(UserPrincipal currentUser, List<Long> bookIds) {
        // Retrieve Borrows done by the logged in user to the given bookIds
        Map<Long, Long> bookUserBorrowMap = null;
        if (currentUser != null) {
            List<Borrow> userBorrows = borrowRepository.findByUserIdAndBookIdIn(currentUser.getId(), bookIds);

            bookUserBorrowMap = userBorrows.stream()
                    .collect(Collectors.toMap(borrow -> borrow.getCopy().getId(), borrow -> borrow.getCopy().getId()));
        }
        return bookUserBorrowMap;
    }

    Map<Long, User> getBookCreatorMap(List<Book> books) {
        // Get Book Creator details of the given list of books
        List<Long> creatorIds = books.stream()
                .map(Book::getCreatedBy)
                .distinct()
                .collect(Collectors.toList());

        List<User> creators = userRepository.findByIdIn(creatorIds);
        Map<Long, User> creatorMap = creators.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return creatorMap;
    }
}
