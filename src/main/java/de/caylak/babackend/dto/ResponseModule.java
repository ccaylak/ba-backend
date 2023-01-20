package de.caylak.babackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ResponseModule {
    private String requestedModuleId;
    private String requestedModule;
    private String requestedEcts;

    private String originModule;
    private String originEcts;
    private String originGrade;
    private String originAckGrade;
}
