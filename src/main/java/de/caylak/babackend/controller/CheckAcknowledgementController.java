package de.caylak.babackend.controller;

import de.caylak.babackend.dto.FormData;
import de.caylak.babackend.dto.ResponseModule;
import de.caylak.babackend.entity.Course;
import de.caylak.babackend.entity.Module;
import de.caylak.babackend.entity.University;
import de.caylak.babackend.entity.persistance.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/check")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CheckAcknowledgementController {

    private final ModuleRepository moduleRepository;

    @PostMapping("/acknowledgment")
    public List<ResponseModule> getAllModules(@RequestBody FormData formData) {

        List<ResponseModule> requestedModules = Stream.concat(
                        formData.getRegularModules().stream(),
                        formData.getElectiveModules().stream())
                .toList();

        List<String> moduleIds = requestedModules.stream()
                .map(ResponseModule::getRequestedModuleId)
                .toList();

        List<ResponseModule> originResponseModules = createRequestedResponseModules(requestedModules);

        University originUniversity = new University(formData.getOriginUniversity());
        Course originCourse = new Course(formData.getOriginCourse(), originUniversity);

        List<Module> originModuleList = createOriginModules(originResponseModules, originCourse);

        List<Module> foundModules = moduleRepository.findAllByModuleNumberIn(moduleIds);

        List<String> matchingModuleIds = foundModules.stream()
                .filter(foundModule -> containsEntry(foundModule.getEquivalentModules(), originModuleList))
                .map(Module::getModuleNumber)
                .toList();

        List<String> unknownModuleIds = foundModules.stream()
                .filter(foundModule -> containsEntry(foundModule.getUnknownEquivalentModules(), originModuleList))
                .map(Module::getModuleNumber)
                .toList();

        List<String> nonMatchingIds = foundModules.stream()
                .filter(foundModule -> containsEntry(foundModule.getNonEquivalentModules(), originModuleList))
                .map(Module::getModuleNumber)
                .toList();

        List<ResponseModule> acknowledgedModules = new ArrayList<>();
        for (ResponseModule responseModule : requestedModules) {
            for (String matchingModuleId : matchingModuleIds) {
                if (responseModule.getRequestedModuleId().equals(matchingModuleId)) {
                    responseModule.setOriginAckGrade("✓");
                    acknowledgedModules.add(responseModule);
                }
            }
            for (String unknownModuleId : unknownModuleIds) {
                if (responseModule.getRequestedModuleId().equals(unknownModuleId)) {
                    responseModule.setOriginAckGrade("?");
                    acknowledgedModules.add(responseModule);
                }
            }
            for (String nonMatchingId : nonMatchingIds) {
                if (responseModule.getRequestedModuleId().equals(nonMatchingId)) {
                    responseModule.setOriginAckGrade("✗");
                    acknowledgedModules.add(responseModule);
                }
            }
        }


        acknowledgedModules.addAll(getMissingModules(requestedModules, acknowledgedModules));

        return acknowledgedModules;
    }

    private List<ResponseModule> getMissingModules(List<ResponseModule> requestedModules, List<ResponseModule> acknowledgedModules) {
        List<ResponseModule> missingModules = new ArrayList<>();

        requestedModules.forEach(requestedModule -> {
            if (!acknowledgedModules.contains(requestedModule)) {
                requestedModule.setOriginAckGrade("unbekannt");
                missingModules.add(requestedModule);
            }
        });

        return missingModules;
    }

    private List<ResponseModule> createRequestedResponseModules(List<ResponseModule> requestedResponseModules) {
        return requestedResponseModules.stream()
                .map(requestedModule -> ResponseModule.builder()
                        .originModule(requestedModule.getOriginModule())
                        .originEcts(requestedModule.getOriginEcts())
                        .originGrade(requestedModule.getOriginGrade())
                        .build())
                .toList();
    }

    private List<Module> createOriginModules(List<ResponseModule> originResponseModules, Course originCourse) {
        return originResponseModules.stream()
                .map(originModule ->
                        new Module(
                                null,
                                originModule.getOriginModule(),
                                originModule.getOriginEcts(),
                                originModule.getOriginGrade(),
                                null,
                                originCourse
                        )
                )
                .toList();
    }

    private boolean containsEntry(List<Module> equivalentModules, List<Module> originModules) {
        return equivalentModules.stream()
                .anyMatch(originModules::contains);
    }
}
