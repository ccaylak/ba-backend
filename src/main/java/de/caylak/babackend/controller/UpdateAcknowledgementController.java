package de.caylak.babackend.controller;

import de.caylak.babackend.dto.ResponseModule;
import de.caylak.babackend.entity.Module;
import de.caylak.babackend.entity.persistance.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/update/")
@CrossOrigin("http://localhost:3000")
public class UpdateAcknowledgementController {
    private final ModuleRepository moduleRepository;

    @GetMapping("/all")
    public List<ResponseModule> getAllUnknownModules() {
        List<Module> allModules = moduleRepository.findAll();

        List<Module> allUnknownModules = allModules.stream()
                .filter(module -> !module.getUnknownEquivalentModules().isEmpty())
                .toList();

        return createResponseModules(allUnknownModules);
    }

    private List<ResponseModule> createResponseModules(List<Module> allUnknownModules) {
        List<ResponseModule> unknownModules = new ArrayList<>();

        for (Module module : allUnknownModules) {
            for (Module unknownModule : module.getUnknownEquivalentModules()) {
                unknownModules.add(
                        ResponseModule.builder()
                                .requestedModuleId(module.getModuleNumber())
                                .requestedModule(module.getName())
                                .requestedEcts(module.getEcts())
                                .originModule(unknownModule.getName())
                                .originEcts(unknownModule.getEcts())
                                .originGrade(unknownModule.getGrade())
                                .build()

                );
            }
        }

        return unknownModules;
    }

    @PostMapping("/module")
    public boolean updateAcknowledgment(@RequestBody ResponseModule responseModule) {
        Optional<Module> requestedModule = moduleRepository.findByModuleNumber(responseModule.getRequestedModuleId());

        Optional<Module> unknownModule = moduleRepository.findByModuleNumberAndUnknownEquivalentModulesNameAndUnknownEquivalentModulesEcts(
                responseModule.getRequestedModuleId(), responseModule.getOriginModule(), responseModule.getOriginEcts()
        );


        if (requestedModule.isPresent() && unknownModule.isPresent()) {
            unknownModule.get().setAckGrade(responseModule.getOriginAckGrade());
            requestedModule.get().getEquivalentModules().add(unknownModule.get());

            return true;
        }

        return false;
    }
}
