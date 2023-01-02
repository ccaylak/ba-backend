package de.caylak.babackend.service;

import de.caylak.babackend.PDFService;
import de.caylak.babackend.controller.AcknowledgementData;
import de.caylak.babackend.dto.CourseDTO;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OCRService {

    private final PDFService pdfService;
    private final CourseService courseService;
    private final AcknowledgementService acknowledgementService;

    public CourseDTO getDataFromCourseDocument(File pdfFile) throws IOException, TesseractException {
        List<BufferedImage> pdfImages = pdfService.convertToBufferedImages(pdfFile, 300);

        return courseService.createDTOAndImages(pdfImages);
    }

    public AcknowledgementData getDataFromAcknowledgementDocument(File pdfFile) throws IOException, TesseractException {
        List<BufferedImage> pdfImages = pdfService.convertToBufferedImages(pdfFile, 300);

        return acknowledgementService.createAcknowledgementDataAndImages(pdfImages);
    }
}
