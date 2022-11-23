package de.caylak.babackend.dto;

import lombok.Builder;

@Builder
public class ModuleDTO {
    private int id;
    private String name;
    private float ects;
}
