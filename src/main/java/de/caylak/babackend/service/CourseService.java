package de.caylak.babackend.service;

import de.caylak.babackend.dto.CourseDTO;
import de.caylak.babackend.dto.ModuleDTO;
import de.caylak.babackend.utility.OCRUtils;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.regex.Matcher;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final Tesseract tesseract;

    public CourseDTO createDTOAndImages(List<BufferedImage> pdfImages) throws TesseractException {
        pdfImages = pdfImages.subList(1, 3);

        return CourseDTO.builder()
                .name(extractCourseName(pdfImages.get(0)))
                .regularModules(extractRegularModules(pdfImages.get(0)))
                .electiveModules(extractElectiveModules(pdfImages))
                .build();
    }

    public String extractCourseName(BufferedImage pdfImage) throws TesseractException {
        BufferedImage courseNameImage = pdfImage.getSubimage(146, 168, 1900, 90);

        tesseract.setPageSegMode(7);
        return tesseract.doOCR(courseNameImage)
                .replace("Prüfungsleistung im eingeschriebenen Studiengang ", "")
                .trim();
    }

    public Pair<BufferedImage, List<ModuleDTO>> extractRegularModules(BufferedImage pdfImage) throws TesseractException {

        BufferedImage regularModulesImage = pdfImage.getSubimage(290, 410, 1110, 2330);

        tesseract.setPageSegMode(6);
        List<String> ocrResult = OCRUtils.extractValues(tesseract.doOCR(regularModulesImage));

        List<ModuleDTO> regularModuleDTOs = ocrResult.stream()
                .map(OCRUtils.modulePattern()::matcher)
                .filter(Matcher::find)
                .map(ModuleDTO::createModule)
                .toList();

        return Pair.of(regularModulesImage, regularModuleDTOs);
    }

    public Pair<List<BufferedImage>, List<ModuleDTO>> extractElectiveModules(List<BufferedImage> bufferedImages) throws TesseractException {

        BufferedImage firstPageImage = bufferedImages.get(0).getSubimage(285, 2800, 1115, 550);
        BufferedImage secondPageImage = bufferedImages.get(1).getSubimage(285, 200, 1115, 1750);

        BufferedImage blankImage = new BufferedImage(1115, 550 + 1750, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = blankImage.createGraphics();
        graphics.drawImage(firstPageImage, 0, 0, null);
        graphics.drawImage(secondPageImage, 0, 550, null);
        graphics.dispose();

        tesseract.setPageSegMode(6);
        List<String> ocrResult = OCRUtils.extractValues(tesseract.doOCR(blankImage));

        List<ModuleDTO> electiveModuleDTOs = ocrResult.stream()
                .map(OCRUtils.modulePattern()::matcher)
                .filter(Matcher::find)
                .map(ModuleDTO::createModule)
                .toList();

        return Pair.of(List.of(firstPageImage, secondPageImage), electiveModuleDTOs);
    }
}
