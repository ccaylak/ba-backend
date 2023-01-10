package de.caylak.babackend.controller;

import de.caylak.babackend.entity.Module;
import de.caylak.babackend.entity.University;
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
import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("/save")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AcknowledgementController {

    private final ModuleRepository moduleRepository;
    private final UniversityRepository universityRepository;

    @PostMapping("/acknowledgment")
    @Transactional
    public List<Module> saveAcknowledgment(@RequestBody Map<String, Object> payload) {
        String originUniversity = payload.get("originUniversity").toString();

        List<Map<String, String>> allModules = new ArrayList<>((List<Map<String, String>>) payload.get("regularModules"));
        allModules.addAll((List<Map<String, String>>) payload.get("regularModules"));

        List<Module> savedModules = createModules(allModules, originUniversity);
        moduleRepository.saveAll(savedModules);

        return savedModules;
    }

    private List<Module> createModules(List<Map<String, String>> allModules, String originUniversity) {

        List<Module> modules = new ArrayList<>();

        for (Map<String, String> module : allModules) {
            if (!module.get("originAckGrade").equals("abgelehnt")) {
                Module requestedModule = createRequestedModule(module);
                Module originModule = createOriginModule(module, originUniversity);
                requestedModule.getEquivalentModules().add(originModule);

                modules.add(requestedModule);
            }
        }

        return modules;
    }

    private Module createRequestedModule(Map<String, String> module) {
        String requestedModuleId = module.get("requestedModuleId");
        String requestedModule = module.get("requestedModule");
        String requestedEcts = module.get("requestedEcts");
        University university = getUniversity("Fachhochschule Dortmund");

        return moduleRepository.findByModuleNumberAndNameAndEctsAndUniversity(
                requestedModuleId,
                requestedModule,
                requestedEcts,
                university
        ).orElseGet(() -> moduleRepository.save(
                new Module(requestedModuleId, requestedModule, requestedEcts, university)
        ));
    }

    private Module createOriginModule(Map<String, String> module, String originUniversity) {
        String originModule = module.get("originModule");
        String originEcts = module.get("originEcts");
        String originGrade = module.get("originGrade");
        String originAkGrade = module.get("originAckGrade");

        University university = getUniversity(originUniversity);

        return moduleRepository.findByNameAndEctsAndGradeAndAckGradeAndUniversity(
                originModule, originEcts, originGrade, originAkGrade, university
        ).orElseGet(() -> moduleRepository.save(new Module(originModule, originEcts, originGrade, originAkGrade, university)));
    }

    private University getUniversity(String universityName) {
        return universityRepository.findByName(universityName)
                .orElseGet(() -> universityRepository.save(new University(universityName)));

    }
}
