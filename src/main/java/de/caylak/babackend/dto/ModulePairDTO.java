package de.caylak.babackend.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ModulePairDTO {

    String requestedModuleId;
    String requestedModule;
    String requestedEcts;

    String originModule;
    String originEcts;
    String originGrade;
    String originAckGrade;
}
