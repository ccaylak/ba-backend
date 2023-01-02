package de.caylak.babackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

@ExtendWith(MockitoExtension.class)
class PDFServiceTest {

    private PDFService testSubject;

    @BeforeEach
    void setup() {
        this.testSubject = new PDFService();
    }

    @Test
    void convertToBufferedImagesShouldThrowIllegalStateException() {
        // given
        File pdfFile = new File("");

        // when
        // then
        assertThatIllegalStateException()
                .isThrownBy(() -> testSubject.convertToBufferedImages(pdfFile, 300))
                .withMessage("PDF does not exist");
    }

    @Test
    void convertToBufferedImages() throws IOException {
        // given
        File pdfFile = new File("src/main/resources/data/example.pdf");

        // when
        List<BufferedImage> actualBufferedImages = testSubject.convertToBufferedImages(pdfFile, 300);

        // then
        assertThat(actualBufferedImages)
                .hasSize(4);
    }
}
