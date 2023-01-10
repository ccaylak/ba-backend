package de.caylak.babackend.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class AcknowledgementData {

    RequestData requestData;

    List<ModulePairDTO> regularModules;
    List<ModulePairDTO> electiveModules;
}
