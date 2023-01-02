package de.caylak.babackend.controller;

import de.caylak.babackend.dto.CourseDTO;
import de.caylak.babackend.service.OCRService;
import de.caylak.babackend.service.UploadService;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/ocr")
@RequiredArgsConstructor
public class OCRController {

    private final OCRService ocrService;

    @GetMapping("/university")
    public CourseDTO getCourseData() throws TesseractException, IOException {
        return ocrService.getDataFromCourseDocument(UploadService.getPDFFile());
    }

    @GetMapping("/exec-ack")
    public AcknowledgementData getAcknowledgementData() throws IOException, TesseractException {
        return ocrService.getDataFromAcknowledgementDocument(UploadService.getPDFFile());
    }
}
