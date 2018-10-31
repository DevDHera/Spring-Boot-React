package com.devin.bhsb.repository;

import com.devin.bhsb.model.AuthorBorrowCount;
import com.devin.bhsb.model.Book;
import com.devin.bhsb.model.Borrow;
import com.devin.bhsb.model.CopyBorrowCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRepository extends JpaRepository<Book, Long> {
    @Query("SELECT NEW com.example.bhsb.model.CopyBorrowCount(b.copy.id, count(b.id)) FROM Book b WHERE b.book.id in :bookIds GROUP BY b.copy.id")
    List<CopyBorrowCount> countByBookIdInGroupByCopyId(@Param("pollIds") List<Long> pollIds);

    @Query("SELECT NEW com.example.bhsb.model.CopyBorrowCount(b.copy.id, count(b.id)) FROM Book b WHERE b.book.id = :bookId GROUP BY b.copy.id")
    List<CopyBorrowCount> countByBookIdGroupByCopyId(@Param("pollId") Long pollId);

    @Query("SELECT NEW com.example.bhsb.model.AuthorBorrowCount(b.author.id, count(b.id)) FROM Book b WHERE b.book.id in :bookIds GROUP BY b.author.id")
    List<AuthorBorrowCount> countByBookIdInGroupByAuthorId(@Param("pollIds") List<Long> pollIds);

    @Query("SELECT NEW com.example.bhsb.model.AuthorBorrowCount(b.author.id, count(b.id)) FROM Book b WHERE b.book.id = :bookId GROUP BY b.author.id")
    List<AuthorBorrowCount> countByBookIdGroupByAuthorId(@Param("pollId") Long pollId);

    @Query("SELECT b FROM Borrow b WHERE b.user.id = :userId AND b.book.id IN :bookIds")
    List<Borrow> findByUserIdAndBookIdIn(@Param("userId") Long userId, @Param("bookIds") List<Long> bookIds);

    @Query("SELECT b FROM Borrow b WHERE b.user.id = :userId AND b.book.id = :bookId")
    Borrow findByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Query("SELECT COUNT(b.id) FROM Borrow b WHERE b.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT b.book.id FROM Borrow b WHERE b.user.id = :userId")
    Page<Long> findBorrowedBooksByUserId(@Param("userId") Long userId, Pageable pageable);
}
