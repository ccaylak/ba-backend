package de.caylak.babackend.service;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@NoArgsConstructor
public class UploadService {
    public static File getPDFFile() {
        return new File("src/main/resources/data/example.pdf");
    }
}
