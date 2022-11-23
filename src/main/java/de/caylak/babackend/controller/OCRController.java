package de.caylak.babackend.controller;

import de.caylak.babackend.dto.CourseDTO;
import de.caylak.babackend.service.OCRService;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequestMapping("/ocr")
@RequiredArgsConstructor
public class OCRController {
    private final OCRService ocrService;

    @GetMapping("/university")
    public CourseDTO getUniversity() throws TesseractException, IOException, ParseException {
        return ocrService.getDataFromCourseDocument();
    }
}
