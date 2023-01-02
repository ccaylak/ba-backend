package de.caylak.babackend.service;

import de.caylak.babackend.controller.AcknowledgementData;
import de.caylak.babackend.dto.ModuleDTO;
import de.caylak.babackend.utility.OCRUtils;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static java.awt.Color.WHITE;
import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
public class AcknowledgementService {

    private final Tesseract tesseract;

    public AcknowledgementData createAcknowledgementDataAndImages(List<BufferedImage> pdfImages) throws TesseractException {
        pdfImages = pdfImages.subList(0, 3);

        return AcknowledgementData.builder()
                .requestedCourse(extractRequestedCourseName(pdfImages.get(0)))
                .originData(extractOriginData(pdfImages.get(0)))
                .modules(extractModules(List.of(pdfImages.get(1), pdfImages.get(2))))
                .build();
    }

    private Pair<BufferedImage, String> extractRequestedCourseName(BufferedImage pdfImage) throws TesseractException {
        BufferedImage requestedCourseNameImage = pdfImage.getSubimage(1010, 208, 1000, 100);

        tesseract.setPageSegMode(7);
        return Pair.of(requestedCourseNameImage, tesseract.doOCR(requestedCourseNameImage).replace("Prüfungsausschuss", ""));
    }

    private Triple<BufferedImage, String, String> extractOriginData(BufferedImage pdfImage) throws TesseractException {
        BufferedImage originDataImage = pdfImage.getSubimage(175, 1405, 2195, 133);

        tesseract.setPageSegMode(6);
        List<String> ocrResult = OCRUtils.extractValues(tesseract.doOCR(originDataImage));

        return Triple.of(
                originDataImage,
                ocrResult.get(0).replace("Studiengang: ", ""),
                ocrResult.get(1).replace("Hochschule/Berufsakademie und Ort: ", "")
        );
    }

    private Triple<List<BufferedImage>, Map<ModuleDTO, ModuleDTO>, Map<ModuleDTO, ModuleDTO>> extractModules(List<BufferedImage> pdfImages) throws TesseractException {

        BufferedImage regularModulesImage = pdfImages.get(0).getSubimage(290, 421, 1110 + 901, 2340);
        BufferedImage electiveModulesImage = pdfImages.get(0).getSubimage(290, 2805, 1110 + 901, 540);
        BufferedImage electiveModulesImage2 = pdfImages.get(1).getSubimage(290, 190, 1110 + 901, 1750);

        List<BufferedImage> modulesImages = new ArrayList<>(List.of(regularModulesImage, electiveModulesImage, electiveModulesImage2));

        removeAllBoxes(regularModulesImage);
        Map<String, String> regularModulesOCR = doCustomOCR(regularModulesImage);
        Map<ModuleDTO, ModuleDTO> regularModulesMap = createModuleMap(
                getLeft(regularModulesOCR),
                getRight(regularModulesOCR)
        );

        removeAllBoxes(electiveModulesImage);
        Map<String, String> electiveModulesOCR = doCustomOCR(electiveModulesImage);
        Map<ModuleDTO, ModuleDTO> electiveModulesMap = createModuleMap(
                getLeft(electiveModulesOCR),
                getRight(electiveModulesOCR)
        );

        removeAllBoxes(electiveModulesImage2);
        Map<String, String> electiveModulesOCR2 = doCustomOCR(electiveModulesImage2);
        electiveModulesMap.putAll(createModuleMap(
                getLeft(electiveModulesOCR2),
                getRight(electiveModulesOCR2))
        );

        return Triple.of(modulesImages, regularModulesMap, electiveModulesMap);
    }

    private Map<String, String> doCustomOCR(BufferedImage modulesImage) throws TesseractException {
        tesseract.setPageSegMode(6);
        return OCRUtils.extractValues(tesseract.doOCR(modulesImage)).stream()
                .filter(s -> s.contains("[") && !s.contains("—"))
                .map(s -> s.split("\\["))
                .collect(toMap(strings -> strings[0], strings -> strings[1]));
    }

    private List<ModuleDTO> getRight(Map<String, String> moduleOCRResult) {
        return moduleOCRResult.values().stream()
                .map(s -> s.replace("_", ""))
                .map(s -> s.replaceAll("\\s+", " "))
                .map(OCRUtils.originModulePattern()::matcher)
                .filter(Matcher::find)
                .map(ModuleDTO::createOriginModule)
                .toList();
    }

    private List<ModuleDTO> getLeft(Map<String, String> moduleOCRResult) {
        return moduleOCRResult.keySet().stream()
                .map(String::trim)
                .map(OCRUtils.modulePattern()::matcher)
                .filter(Matcher::find)
                .map(ModuleDTO::createModule)
                .toList();
    }

    private Map<ModuleDTO, ModuleDTO> createModuleMap(List<ModuleDTO> left, List<ModuleDTO> right) {
        if (left.size() != right.size()) {
            throw new IllegalStateException("lists differ in size");
        }

        Map<ModuleDTO, ModuleDTO> moduleDTOMap = new LinkedHashMap<>();
        for (int i = 0; i < left.size(); i++) {
            moduleDTOMap.put(left.get(i), right.get(i));
        }
        return moduleDTOMap;
    }

    private void removeAllBoxes(BufferedImage bufferedImage) {
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(WHITE);
        graphics.fillRect(1522, 0, 22, bufferedImage.getHeight());
        graphics.fillRect(1663, 0, 22, bufferedImage.getHeight());
        graphics.fillRect(1791, 0, 22, bufferedImage.getHeight());
    }
}
