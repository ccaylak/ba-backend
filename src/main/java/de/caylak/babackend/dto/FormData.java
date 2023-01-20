package de.caylak.babackend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class FormData {
    private String originUniversity;
    private String originCourse;

    private String requestedCourse;
    private List<ResponseModule> regularModules;
    private List<ResponseModule> electiveModules;
}
