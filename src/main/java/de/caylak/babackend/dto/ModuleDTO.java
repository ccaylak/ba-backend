package de.caylak.babackend.dto;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.math.NumberUtils;

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
                .ects(editEcts(matcher.group("ects")))
                .build();
    }


    public static ModuleDTO createOriginModule(Matcher matcher) {
        String grade = matcher.group("grade");
        String ackGrade = matcher.group("ackGrade");


        return ModuleDTO.builder()
                .name(matcher.group("name"))
                .ects(editEcts(matcher.group("ects")))
                .grade(editGrade(grade))
                .acknowledgedGrade(editAckGrade(ackGrade))
                .build();
    }

    private static String editAckGrade(String ackGrade) {
        if (ackGrade.equals("BE")) {
            ackGrade = "bestanden";
        }

        if (NumberUtils.isParsable(ackGrade)) {
            char[] chars = ackGrade.toCharArray();
            ackGrade = chars[0] + "," + chars[1];
        } else {
            ackGrade = ackGrade.replaceAll("[^A-Za-z]+", "");
        }

        return ackGrade;
    }

    private static String editEcts(String ects) {

        if (ects.contains(".")) {
            ects = ects.replace(".", ",");
        }

        if (!ects.contains(",")) {
            ects = ects.concat(",0");
        }


        return ects;
    }

    private static String editGrade(String grade) {
        if (!grade.contains(",")) {
            grade = grade.replace(".", ",");
        }

        return grade;
    }
}
