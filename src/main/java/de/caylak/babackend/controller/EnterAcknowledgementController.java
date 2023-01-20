package de.caylak.babackend.controller;

import de.caylak.babackend.dto.FormData;
import de.caylak.babackend.dto.ResponseModule;
import de.caylak.babackend.entity.Course;
import de.caylak.babackend.entity.CourseId;
import de.caylak.babackend.entity.Module;
import de.caylak.babackend.entity.University;
import de.caylak.babackend.entity.persistance.CourseRepository;
import de.caylak.babackend.entity.persistance.ModuleRepository;
import de.caylak.babackend.entity.persistance.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/save")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class EnterAcknowledgementController {

    private final ModuleRepository moduleRepository;
    private final UniversityRepository universityRepository;
    private final CourseRepository courseRepository;

    @PostMapping("/acknowledgment")
    @Transactional
    public List<Module> saveAcknowledgment(@RequestBody FormData formData) {

        List<ResponseModule> allResponseModules = Stream.concat(
                Optional.ofNullable(formData.getRegularModules()).orElse(Collections.emptyList()).stream(),
                Optional.ofNullable(formData.getElectiveModules()).orElse(Collections.emptyList()).stream()
        ).toList();

        List<Module> allModules = createModules(allResponseModules, formData);
        return moduleRepository.saveAll(allModules);
    }

    private List<Module> createModules(List<ResponseModule> responseModules, FormData formData) {

        List<Module> modules = new ArrayList<>();

        for (ResponseModule module : responseModules) {
            Module originModule = createOriginModule(module, formData.getOriginUniversity(), formData.getOriginCourse());
            Module requestedModule = createRequestedModule(module, formData.getRequestedCourse());

            String originAckGrade = module.getOriginAckGrade();
            if (originAckGrade.equals("abgelehnt")) {
                requestedModule.getNonEquivalentModules().add(originModule);
            } else if (originAckGrade.equals("bestanden") || originAckGrade.matches("^[0-4],\\d$")) {
                requestedModule.getEquivalentModules().add(originModule);
            } else {
                requestedModule.getUnknownEquivalentModules().add(originModule);
            }
            modules.add(requestedModule);
        }

        return modules;
    }

    private Module createRequestedModule(ResponseModule requestedModule, String requestedCourse) {
        String moduleId = requestedModule.getRequestedModuleId();
        String moduleName = requestedModule.getRequestedModule();
        String moduleEcts = requestedModule.getRequestedEcts();

        University university = getUniversity("Fachhochschule Dortmund");
        Course course = getCourse(requestedCourse, university);

        return moduleRepository.findByModuleNumberAndNameAndEctsAndCoursesContains(moduleId, moduleName, moduleEcts, course)
                .orElseGet(() -> moduleRepository.save(new Module(moduleId, moduleName, moduleEcts, null, null, course)));
    }

    private Module createOriginModule(ResponseModule originModule, String originUniversity, String originCourse) {
        String moduleName = originModule.getOriginModule();
        String moduleEcts = originModule.getOriginEcts();
        String moduleGrade = originModule.getOriginGrade();
        String moduleAckGrade = originModule.getOriginAckGrade();

        University university = getUniversity(originUniversity);
        Course course = getCourse(originCourse, university);

        return moduleRepository.findByNameAndEctsAndGradeAndAckGradeAndCoursesContaining(moduleName, moduleEcts, moduleGrade, moduleAckGrade, course)
                .orElseGet(() -> moduleRepository.save(new Module(null, moduleName, moduleEcts, moduleGrade, moduleAckGrade, course)));
    }

    private University getUniversity(String universityName) {
        return universityRepository.findByName(universityName)
                .orElseGet(() -> universityRepository.save(new University(universityName)));
    }

    private Course getCourse(String course, University university) {
        return courseRepository.findById(new CourseId(course, university))
                .orElseGet(() -> courseRepository.save(new Course(course, university)));
    }
}
