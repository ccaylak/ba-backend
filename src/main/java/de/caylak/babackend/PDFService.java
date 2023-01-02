package de.caylak.babackend;

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
public class PDFService {

    public List<BufferedImage> convertToBufferedImages(File pdfFile, int dpi) throws IOException {

        List<BufferedImage> bufferedImages = new ArrayList<>();

        if (pdfFile.exists()) {
            try (PDDocument pdfDocument = load(pdfFile)) {
                PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);

                int pages = pdfDocument.getNumberOfPages();

                for (int i = 0; i < pages; ++i) {
                    bufferedImages.add(pdfRenderer.renderImageWithDPI(i, dpi));
                }
            }
        } else {
            throw new IllegalStateException("PDF does not exist");
        }

        return bufferedImages;
    }
}
