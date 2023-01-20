package de.caylak.babackend.controller;

import de.caylak.babackend.entity.Module;
import de.caylak.babackend.entity.persistance.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
public class SearchController {

    private final ModuleRepository moduleRepository;

    @GetMapping("/{moduleName}")
    public List<Module> searchModule(@PathVariable String moduleName) {
        List<Module> searchResult = moduleRepository.findByNameIgnoreCaseContaining(moduleName);

        List<Module> modules = searchResult.stream()
                .filter(module -> !module.getEquivalentModules().isEmpty())
                .toList();

        List<Module> repoModules = moduleRepository.findByNameIgnoreCaseContainingAndEquivalentModulesIsNotEmpty(moduleName);
        return modules;
    }
}
