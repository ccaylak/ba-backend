package de.caylak.babackend.controller;

import de.caylak.babackend.dto.ModuleDTO;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

@Builder
@Value
public class AcknowledgementData {

    Pair<BufferedImage, String> requestedCourse;
    Triple<BufferedImage, String, String> originData;

    Triple<List<BufferedImage>, Map<ModuleDTO, ModuleDTO>, Map<ModuleDTO, ModuleDTO>> modules;
}
