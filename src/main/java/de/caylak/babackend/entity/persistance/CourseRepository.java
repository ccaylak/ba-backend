package de.caylak.babackend.entity.persistance;

import de.caylak.babackend.entity.Course;
import de.caylak.babackend.entity.CourseId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, CourseId> {
}
