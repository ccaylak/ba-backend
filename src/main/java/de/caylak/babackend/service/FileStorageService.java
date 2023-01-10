package de.caylak.babackend.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService() {
        this.fileStorageLocation = Paths.get("src/main/resources/files")
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new IllegalStateException("could not create directory for uploaded files", e);
        }
    }

    public String storeFile(MultipartFile file) {
        String fileName = new Date().getTime() + "-file." + getFileExtension(file.getOriginalFilename());

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException e) {
            throw new IllegalStateException("Could not store file" + fileName, e);
        }
    }

    private String getFileExtension(String fileName) {

        if (StringUtils.isBlank(fileName)) {
            throw new IllegalStateException("file with filename: " + fileName + " not found");
        }

        String[] fileNameParts = fileName.split("\\.");
        return fileNameParts[fileNameParts.length - 1];
    }

    public File serveFile(String fileName) throws IOException {
        return findByFileName(fileName).toFile();
    }

    public Path findByFileName(String fileName)
            throws IOException {

        List<Path> result;
        try (Stream<Path> pathStream = Files.find(this.fileStorageLocation,
                Integer.MAX_VALUE,
                (p, basicFileAttributes) ->
                        p.getFileName().toString().equalsIgnoreCase(fileName))
        ) {
            result = pathStream.toList();
        }
        return result.get(0);

    }
}
