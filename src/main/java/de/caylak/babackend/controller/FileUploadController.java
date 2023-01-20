package de.caylak.babackend.controller;

import de.caylak.babackend.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

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

    @GetMapping(value = "/image/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable String imageName) throws IOException {

        return encodeBase64(getClass().getResourceAsStream("/images/" + imageName));
    }

    private byte[] encodeBase64(InputStream inputStream) throws IOException {
        return Base64.getEncoder().encode(IOUtils.toByteArray(inputStream));
    }
}
