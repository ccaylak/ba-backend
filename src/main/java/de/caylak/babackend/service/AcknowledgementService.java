package de.caylak.babackend.service;

import de.caylak.babackend.dto.AcknowledgementData;
import de.caylak.babackend.dto.ModuleDTO;
import de.caylak.babackend.dto.ModulePairDTO;
import de.caylak.babackend.dto.RequestData;
import de.caylak.babackend.utility.OCRUtils;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
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
                .requestData(extractRequestedData(pdfImages.get(0)))
                .regularModules(extractModules(List.of(pdfImages.get(1))))
                .electiveModules(extractModules(List.of(pdfImages.get(1), pdfImages.get(2))))
                .build();

    }

    private synchronized RequestData extractRequestedData(BufferedImage pdfImage) throws TesseractException {

        BufferedImage requestedCourseNameImage = pdfImage.getSubimage(1010, 208, 1000, 100);

        tesseract.setPageSegMode(7);
        String requestedCourse = tesseract.doOCR(requestedCourseNameImage).replace("Prüfungsausschuss", "");
        BufferedImage originDataImage = pdfImage.getSubimage(175, 1405, 2195, 133);

        tesseract.setPageSegMode(6);
        List<String> ocrResults = OCRUtils.extractValues(tesseract.doOCR(originDataImage));
        String originCourse = ocrResults.get(0).replace("Studiengang: ", "");
        String originUniversity = ocrResults.get(1).replace("Hochschule/Berufsakademie und Ort: ", "");

        return RequestData.builder()
                .requestedCourse(requestedCourse)
                .originCourse(originCourse)
                .originUniversity(originUniversity)
                .build();
    }

    private List<ModulePairDTO> extractModules(List<BufferedImage> pdfImages) throws TesseractException {

        if (pdfImages.size() == 1) {
            BufferedImage regularModulesImage = pdfImages.get(0).getSubimage(290, 421, 1110 + 901, 2340);
            return extractRegularModules(regularModulesImage);
        }

        BufferedImage electiveModulesImage = pdfImages.get(0).getSubimage(290, 2805, 1110 + 901, 540);
        BufferedImage electiveModulesImage2 = pdfImages.get(1).getSubimage(290, 190, 1110 + 901, 1750);
        return extractElectiveModules(List.of(electiveModulesImage, electiveModulesImage2));

    }

    private List<ModulePairDTO> extractElectiveModules(List<BufferedImage> electiveModulesImage) throws TesseractException {

        removeAllBoxes(electiveModulesImage.get(0));
        Map<String, String> electiveModulesOCR = doCustomOCR(electiveModulesImage.get(0));

        List<ModuleDTO> rightModules = new ArrayList<>(getRight(electiveModulesOCR));
        List<ModuleDTO> leftModules = new ArrayList<>(getLeft(electiveModulesOCR));

        removeAllBoxes(electiveModulesImage.get(1));
        Map<String, String> electiveModulesOCR2 = doCustomOCR(electiveModulesImage.get(1));
        rightModules.addAll(getLeft(electiveModulesOCR2));
        leftModules.addAll(getRight(electiveModulesOCR2));

        if (leftModules.size() != rightModules.size()) {
            throw new IllegalStateException("lists differ in size");
        }


        List<ModulePairDTO> modulePair = new ArrayList<>();
        for (int i = 0; i < rightModules.size(); i++) {
            modulePair.add(
                    ModulePairDTO.builder()
                            .requestedModuleId(leftModules.get(i).getId())
                            .requestedModule(leftModules.get(i).getName())
                            .requestedEcts(leftModules.get(i).getEcts())
                            .originModule(rightModules.get(i).getName())
                            .originEcts(rightModules.get(i).getEcts())
                            .originGrade(rightModules.get(i).getGrade())
                            .originAckGrade(rightModules.get(i).getAcknowledgedGrade())
                            .build()
            );
        }

        return modulePair;
    }

    private List<ModulePairDTO> extractRegularModules(BufferedImage regularModulesImage) throws TesseractException {
        removeAllBoxes(regularModulesImage);
        Map<String, String> regularModulesOCR = doCustomOCR(regularModulesImage);

        List<ModuleDTO> leftModules = getLeft(regularModulesOCR);
        List<ModuleDTO> rightModules = getRight(regularModulesOCR);

        if (leftModules.size() != rightModules.size()) {
            throw new IllegalStateException("lists differ in size");
        }

        List<ModulePairDTO> modulePair = new ArrayList<>();
        for (int i = 0; i < leftModules.size(); i++) {
            modulePair.add(
                    ModulePairDTO.builder()
                            .requestedModuleId(leftModules.get(i).getId())
                            .requestedModule(leftModules.get(i).getName())
                            .requestedEcts(leftModules.get(i).getEcts())
                            .originModule(rightModules.get(i).getName())
                            .originEcts(rightModules.get(i).getEcts())
                            .originGrade(rightModules.get(i).getGrade())
                            .originAckGrade(rightModules.get(i).getAcknowledgedGrade())
                            .build()
            );
        }

        return modulePair;
    }

    private synchronized Map<String, String> doCustomOCR(BufferedImage modulesImage) throws TesseractException {
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

    private void removeAllBoxes(BufferedImage bufferedImage) {
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(WHITE);
        graphics.fillRect(1522, 0, 22, bufferedImage.getHeight());
        graphics.fillRect(1663, 0, 22, bufferedImage.getHeight());
        graphics.fillRect(1791, 0, 22, bufferedImage.getHeight());

        graphics.dispose();
    }
}
