package de.caylak.babackend.entity.persistance;

import de.caylak.babackend.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UniversityRepository extends JpaRepository<University, String> {
    Optional<University> findByName(String name);
}
