package de.caylak.babackend.entity.persistance;

import de.caylak.babackend.entity.Module;
import de.caylak.babackend.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    Optional<Module> findByModuleNumberAndNameAndEctsAndUniversity(
            String moduleNumber,
            String name,
            String ects,
            University university
    );

    Optional<Module> findByNameAndEctsAndGradeAndAckGradeAndUniversity(
            String name,
            String ects,
            String grade,
            String ackGrade,
            University university
    );
}
