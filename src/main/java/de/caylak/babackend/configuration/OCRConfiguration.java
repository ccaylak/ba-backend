package de.caylak.babackend.configuration;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class OCRConfiguration {

    private static final String LANGUAGE = "deu";

    @Bean
    public Tesseract tesseract() throws URISyntaxException {
        System.setProperty("java.library.path", "/usr/local/Cellar/tesseract/5.2.0/lib");

        Tesseract tesseract = new Tesseract();

        tesseract.setLanguage(LANGUAGE);
        tesseract.setOcrEngineMode(1);

        Path dataPath = Paths.get(ClassLoader.getSystemResource("data").toURI());
        tesseract.setDatapath(dataPath.toString());

        return tesseract;
    }
}
