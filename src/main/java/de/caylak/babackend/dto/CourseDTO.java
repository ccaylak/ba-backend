package de.caylak.babackend.dto;

import lombok.Builder;

import java.util.List;

@Builder
public class CourseDTO {
    private String name;
    private List<ModuleDTO> regularModules;
    private List<ModuleDTO> electiveModules;
}
