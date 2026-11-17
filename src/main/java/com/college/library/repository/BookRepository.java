package com.college.library.repository;

import com.college.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByCategoryIgnoreCase(String category);
    boolean existsByIsbn(String isbn);
    boolean existsByIsbnAndBookIdNot(String isbn, Long bookId);
}