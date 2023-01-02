package de.caylak.babackend.dto;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.image.BufferedImage;
import java.util.List;

@Builder
@Value
public class CourseDTO {
    String name;
    Pair<BufferedImage, List<ModuleDTO>> regularModules;
    Pair<List<BufferedImage>, List<ModuleDTO>> electiveModules;
}
