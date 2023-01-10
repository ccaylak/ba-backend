package de.caylak.babackend.controller;

import de.caylak.babackend.dto.AcknowledgementData;
import de.caylak.babackend.dto.CourseDTO;
import de.caylak.babackend.service.FileStorageService;
import de.caylak.babackend.service.OCRService;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/ocr")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class OCRController {

    private final OCRService ocrService;
    private final FileStorageService fileStorageService;

    @GetMapping("/university/{fileName}")
    public CourseDTO getCourseData(@PathVariable String fileName) throws TesseractException, IOException {
        File pdfFile = fileStorageService.findByFileName(fileName).toFile();
        return ocrService.getDataFromCourseDocument(pdfFile);
    }

    @GetMapping("/acknowledgment/{fileName}")
    public AcknowledgementData getAcknowledgementData(@PathVariable String fileName) throws IOException, TesseractException {
        File pdfFile = fileStorageService.findByFileName(fileName).toFile();
        return ocrService.getDataFromAcknowledgementDocument(pdfFile);
    }
}
