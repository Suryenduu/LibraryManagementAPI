package com.college.library.service;

import com.college.library.entity.Book;
import com.college.library.entity.IssueStatus;
import com.college.library.exception.BusinessRuleException;
import com.college.library.exception.DuplicateResourceException;
import com.college.library.exception.ResourceNotFoundException;
import com.college.library.repository.BookIssueRepository;
import com.college.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookIssueRepository bookIssueRepository;

    public Book addBook(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new DuplicateResourceException("Book with ISBN " + book.getIsbn() + " already exists.");
        }
        if (book.getAvailableCopies() > book.getTotalCopies()) {
            throw new BusinessRuleException("Available copies cannot exceed total copies.");
        }
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
    }

    public Book updateBook(Long id, Book updatedBook) {
        Book existing = getBookById(id);
        if (bookRepository.existsByIsbnAndBookIdNot(updatedBook.getIsbn(), id)) {
            throw new DuplicateResourceException("ISBN " + updatedBook.getIsbn() + " is already assigned to another book.");
        }
        if (updatedBook.getAvailableCopies() > updatedBook.getTotalCopies()) {
            throw new BusinessRuleException("Available copies cannot exceed total copies.");
        }
        existing.setTitle(updatedBook.getTitle());
        existing.setAuthor(updatedBook.getAuthor());
        existing.setIsbn(updatedBook.getIsbn());
        existing.setCategory(updatedBook.getCategory());
        existing.setTotalCopies(updatedBook.getTotalCopies());
        existing.setAvailableCopies(updatedBook.getAvailableCopies());
        return bookRepository.save(existing);
    }

    public void deleteBook(Long id) {
        Book book = getBookById(id);
        if (bookIssueRepository.existsByBook_BookIdAndStatus(id, IssueStatus.ISSUED)) {
            throw new BusinessRuleException("Cannot delete book with active issued copies.");
        }
        bookRepository.delete(book);
    }

    public List<Book> getBooksByCategory(String category) {
        return bookRepository.findByCategoryIgnoreCase(category);
    }
}