package de.caylak.babackend.service;

import de.caylak.babackend.dto.CourseDTO;
import de.caylak.babackend.dto.ModuleDTO;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final Tesseract tesseract;

    public CourseDTO createDTOAndImages(List<BufferedImage> pdfImages) throws TesseractException, IOException, ParseException {
        pdfImages = pdfImages.subList(1, 3);

        //Bilder noch zurückgeben
        return CourseDTO.builder()
                .name(extractCourseName(pdfImages.get(0)))
                .regularModules(extractRegularModules(pdfImages.get(0)))
                .electiveModules(extractElectiveModules(pdfImages))
                .build();
    }

    public String extractCourseName(BufferedImage bufferedImage) throws TesseractException {
        BufferedImage courseNameImage = bufferedImage.getSubimage(146, 168, 1900, 90);

        return tesseract.doOCR(courseNameImage).replace("Prüfungsleistung im eingeschriebenen Studiengang ", "");
    }

    public List<ModuleDTO> extractRegularModules(BufferedImage bufferedImage) throws TesseractException, IOException, ParseException {

        BufferedImage moduleIdsImage = bufferedImage.getSubimage(286, 415, 200, 2345);
        BufferedImage moduleNamesImage = bufferedImage.getSubimage(481, 415, 784, 2345);
        BufferedImage moduleEctsImage = bufferedImage.getSubimage(1280, 415, 141, 2345);

        List<String> moduleIds = getValuesWithoutNewLinesAndEmptyValues(tesseract.doOCR(moduleIdsImage));
        List<String> moduleNames = getValuesWithoutNewLinesAndEmptyValues(tesseract.doOCR(moduleNamesImage));
        List<String> moduleEcts = getValuesWithoutNewLinesAndEmptyValues(tesseract.doOCR(moduleEctsImage));

        NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);

        List<ModuleDTO> regularModules = new ArrayList<>();
        for (int i = 0; i < moduleIds.size(); i++) {

            Number number = format.parse(moduleEcts.get(i));

            regularModules.add(
                    ModuleDTO.builder()
                            .id(Integer.parseInt(moduleIds.get(i)))
                            .name(moduleNames.get(i))
                            .ects(number.floatValue())
                            .build()
            );
        }

        return regularModules;
    }

    private List<String> getValuesWithoutNewLinesAndEmptyValues(String ocrResult) {

        return Arrays.stream(ocrResult.split(System.lineSeparator()))
                .filter(StringUtils::isNotBlank)
                .toList();
    }

    public List<ModuleDTO> extractElectiveModules(List<BufferedImage> bufferedImages) throws TesseractException, ParseException {
        BufferedImage firstImage = bufferedImages.get(0);
        BufferedImage firstModuleIdsImage = firstImage.getSubimage(286, 2822, 150, 510);
        BufferedImage firstModuleNamesImage = firstImage.getSubimage(481, 2822, 784, 510);
        BufferedImage firstModuleEctsImage = firstImage.getSubimage(1280, 2822, 141, 510);

        BufferedImage lastImage = bufferedImages.get(1);
        BufferedImage lastModuleIdsImage = lastImage.getSubimage(300, 185, 150, 1755);
        BufferedImage lastModuleNamesImage = lastImage.getSubimage(465, 185, 784, 1755);
        BufferedImage lastModuleEctsImage = lastImage.getSubimage(1275, 185, 100, 1755);

        List<String> moduleIds = new ArrayList<>(getValuesWithoutNewLinesAndEmptyValues(tesseract.doOCR(firstModuleIdsImage)));
        List<String> moduleNames = new ArrayList<>(getValuesWithoutNewLinesAndEmptyValues(tesseract.doOCR(firstModuleNamesImage)));
        List<String> moduleEcts = new ArrayList<>(getValuesWithoutNewLinesAndEmptyValues(tesseract.doOCR(firstModuleEctsImage)));

        moduleIds.addAll(getValuesWithoutNewLinesAndEmptyValues(tesseract.doOCR(lastModuleIdsImage)));
        moduleNames.addAll(getValuesWithoutNewLinesAndEmptyValues(tesseract.doOCR(lastModuleNamesImage)));
        moduleEcts.addAll(getValuesWithoutNewLinesAndEmptyValues(tesseract.doOCR(lastModuleEctsImage)));

        NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);

        List<ModuleDTO> electiveModules = new ArrayList<>();
        for (int i = 0; i < moduleIds.size(); i++) {

            Number number = format.parse(moduleEcts.get(i));

            electiveModules.add(
                    ModuleDTO.builder()
                            .id(Integer.parseInt(moduleIds.get(i)))
                            .name(moduleNames.get(i))
                            .ects(number.floatValue())
                            .build()
            );
        }

        return electiveModules;
    }

}
