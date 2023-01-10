package de.caylak.babackend.controller;

import de.caylak.babackend.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam(name = "file") MultipartFile file) {
        return fileStorageService.storeFile(file);
    }

    @GetMapping("/file/{fileName:.+}")
    public File getFile(@PathVariable String fileName) throws IOException {
        return fileStorageService.serveFile(fileName);
    }
}
