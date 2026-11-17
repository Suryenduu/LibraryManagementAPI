package com.college.library.service;

import com.college.library.entity.Book;
import com.college.library.entity.BookIssue;
import com.college.library.entity.IssueStatus;
import com.college.library.entity.Student;
import com.college.library.exception.BusinessRuleException;
import com.college.library.exception.ResourceNotFoundException;
import com.college.library.repository.BookIssueRepository;
import com.college.library.repository.BookRepository;
import com.college.library.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookIssueService {

    private final BookIssueRepository issueRepository;
    private final StudentRepository studentRepository;
    private final BookRepository bookRepository;

    private static final int MAX_ACTIVE_ISSUES = 3;
    private static final int DEFAULT_LOAN_DAYS = 14;

    @Transactional
    public BookIssue issueBook(Long studentId, Long bookId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + bookId));

        if (book.getAvailableCopies() <= 0) {
            throw new BusinessRuleException("No available copies left for book: " + book.getTitle());
        }

        long activeIssues = issueRepository.countByStudent_StudentIdAndStatus(studentId, IssueStatus.ISSUED);
        if (activeIssues >= MAX_ACTIVE_ISSUES) {
            throw new BusinessRuleException("Student has reached the maximum limit of 3 active book issues.");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        BookIssue issue = new BookIssue();
        issue.setStudent(student);
        issue.setBook(book);
        issue.setIssueDate(LocalDate.now());
        issue.setDueDate(LocalDate.now().plusDays(DEFAULT_LOAN_DAYS));
        issue.setStatus(IssueStatus.ISSUED);

        return issueRepository.save(issue);
    }

    @Transactional
    public BookIssue returnBook(Long issueId) {
        BookIssue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue record not found with ID: " + issueId));

        if (issue.getStatus() == IssueStatus.RETURNED) {
            throw new BusinessRuleException("This book has already been returned.");
        }

        issue.setReturnDate(LocalDate.now());
        issue.setStatus(IssueStatus.RETURNED);

        Book book = issue.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return issueRepository.save(issue);
    }

    public List<BookIssue> getAllIssues() {
        return issueRepository.findAll();
    }

    public List<BookIssue> getIssuesByStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }
        return issueRepository.findByStudent_StudentId(studentId);
    }

    public List<BookIssue> getOverdueBooks() {
        return issueRepository.findByStatusAndDueDateBefore(IssueStatus.ISSUED, LocalDate.now());
    }
}