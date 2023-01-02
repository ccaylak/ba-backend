package de.caylak.babackend.dto;

import lombok.Builder;
import lombok.Value;

import java.util.regex.Matcher;

@Builder
@Value
public class ModuleDTO {
    String id;
    String name;
    String ects;
    String grade;
    String acknowledgedGrade;

    public static ModuleDTO createModule(Matcher matcher) {

        return ModuleDTO.builder()
                .id(matcher.group("id"))
                .name(matcher.group("name"))
                .ects(matcher.group("ects"))
                .build();
    }

    public static ModuleDTO createOriginModule(Matcher matcher) {

        return ModuleDTO.builder()
                .name(matcher.group("name"))
                .ects(matcher.group("ects"))
                .grade(matcher.group("grade"))
                .acknowledgedGrade(matcher.group("ackGrade"))
                .build();
    }
}
