package com.college.library.service;

import com.college.library.entity.Student;
import com.college.library.exception.DuplicateResourceException;
import com.college.library.exception.ResourceNotFoundException;
import com.college.library.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public Student registerStudent(Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new DuplicateResourceException("Email " + student.getEmail() + " is already registered.");
        }
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
    }

    public Student updateStudent(Long id, Student updated) {
        Student existing = getStudentById(id);
        if (studentRepository.existsByEmailAndStudentIdNot(updated.getEmail(), id)) {
            throw new DuplicateResourceException("Email " + updated.getEmail() + " is already in use.");
        }
        existing.setStudentName(updated.getStudentName());
        existing.setEmail(updated.getEmail());
        existing.setDepartment(updated.getDepartment());
        existing.setPhoneNumber(updated.getPhoneNumber());
        return studentRepository.save(existing);
    }

    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        studentRepository.delete(student);
    }
}