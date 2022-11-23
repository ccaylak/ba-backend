package de.caylak.babackend;

import de.caylak.babackend.service.OCRService.ActionType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.pdfbox.pdmodel.PDDocument.load;

@Service
public class PDFConverter {

    private static final int DPI = 300;

    public List<BufferedImage> convertPDFToImages(File pdfFile, ActionType actionType) throws IOException {
        List<BufferedImage> bufferedImages = new ArrayList<>();

        if (pdfFile.exists() && actionType != null) {
            try (PDDocument pdfDocument = load(pdfFile)) {
                PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);

                int numberOfPages = pdfDocument.getNumberOfPages();

                for (int i = 0; i < numberOfPages; ++i) {
                    bufferedImages.add(pdfRenderer.renderImageWithDPI(i, DPI));
                }
            }
        }
        return bufferedImages;
    }

    private BufferedImage extractCourseAndUniversity(BufferedImage image) {
        //für 300dpi
        return image.getSubimage(200, 1395, 2185, 153);
    }
}
