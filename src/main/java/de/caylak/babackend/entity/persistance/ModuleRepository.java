package de.caylak.babackend.entity.persistance;

import de.caylak.babackend.entity.Course;
import de.caylak.babackend.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, String> {
    Optional<Module> findByModuleNumber(String moduleNumber);

    List<Module> findByNameIgnoreCaseContaining(String name);

    List<Module> findByNameIgnoreCaseContainingAndEquivalentModulesIsNotEmpty(String name);

    Optional<Module> findByModuleNumberAndNameAndEctsAndCoursesContains(
            String moduleNumber,
            String name,
            String ects,
            Course course
    );

    Optional<Module> findByNameAndEctsAndGradeAndAckGradeAndCoursesContaining(
            String name,
            String ects,
            String grade,
            String ackGrade,
            Course course
    );

    List<Module> findAllByModuleNumberIn(List<String> moduleNumbers);

    Optional<Module> findByModuleNumberAndUnknownEquivalentModulesNameAndUnknownEquivalentModulesEcts(
            String moduleNumber,
            String name,
            String ects
    );
}
