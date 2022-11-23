package de.caylak.babackend.service;

import de.caylak.babackend.PDFConverter;
import de.caylak.babackend.dto.CourseDTO;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OCRService {

    private final PDFConverter pdfConverter;

    private final CourseService courseService;
    private final AcknoledmentService acknoledmentService;

    public CourseDTO getDataFromCourseDocument() throws IOException, TesseractException, ParseException {

        List<BufferedImage> pdfImages = pdfConverter.convertPDFToImages(new File("src/main/resources/data/example.pdf"), ActionType.UPLOAD_COURSE);

        courseService.createImagesForCourse(pdfImages);

        pdfConverter.convertPDFToImages(new File("src/main/resources/data/example.pdf"), ActionType.UPLOAD_COURSE);

        return null;
    }

    public enum ActionType {
        UPLOAD_COURSE,
        UPLOAD_ACKNOWLEDGEMENT,
        EXECUTE_ACKNOWLEDGEMENT
    }
}
