package com.hari.lms.repository;

import com.hari.lms.entity.Course;
import com.hari.lms.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Lesson entity operations.
 * 
 * @author Hari Parthu
 */
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    /**
     * Find all lessons by course.
     */
    List<Lesson> findByCourse(Course course);

    /**
     * Find all lessons by course ID.
     */
    List<Lesson> findByCourseId(Long courseId);

    /**
     * Find lessons by course ordered by sequence number.
     */
    List<Lesson> findByCourseOrderBySequenceNumberAsc(Course course);

    /**
     * Find lessons by course ID ordered by sequence number.
     */
    List<Lesson> findByCourseIdOrderBySequenceNumberAsc(Long courseId);

    /**
     * Find lesson by course and sequence number.
     */
    Optional<Lesson> findByCourseAndSequenceNumber(Course course, Integer sequenceNumber);

    /**
     * Find lesson by course ID and sequence number.
     */
    Optional<Lesson> findByCourseIdAndSequenceNumber(Long courseId, Integer sequenceNumber);

    /**
     * Check if lesson exists with course and sequence number.
     */
    boolean existsByCourseAndSequenceNumber(Course course, Integer sequenceNumber);

    /**
     * Check if lesson exists with course ID and sequence number.
     */
    boolean existsByCourseIdAndSequenceNumber(Long courseId, Integer sequenceNumber);

    /**
     * Find maximum sequence number for a course.
     */
    @Query("SELECT MAX(l.sequenceNumber) FROM Lesson l WHERE l.course = :course")
    Optional<Integer> findMaxSequenceNumberByCourse(@Param("course") Course course);

    /**
     * Find maximum sequence number for a course by ID.
     */
    @Query("SELECT MAX(l.sequenceNumber) FROM Lesson l WHERE l.course.id = :courseId")
    Optional<Integer> findMaxSequenceNumberByCourseId(@Param("courseId") Long courseId);

    /**
     * Find lessons by course with pagination.
     */
    Page<Lesson> findByCourse(Course course, Pageable pageable);

    /**
     * Find lessons by course ID with pagination.
     */
    Page<Lesson> findByCourseId(Long courseId, Pageable pageable);

    /**
     * Count lessons by course.
     */
    long countByCourse(Course course);

    /**
     * Count lessons by course ID.
     */
    long countByCourseId(Long courseId);

    /**
     * Find lessons by title containing text.
     */
    List<Lesson> findByTitleContainingIgnoreCase(String title);

    /**
     * Find lessons by content type.
     */
    List<Lesson> findByContentType(com.hari.lms.enums.ContentType contentType);
}