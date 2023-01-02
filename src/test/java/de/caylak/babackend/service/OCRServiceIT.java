package de.caylak.babackend.service;

import de.caylak.babackend.controller.AcknowledgementData;
import de.caylak.babackend.dto.CourseDTO;
import de.caylak.babackend.dto.ModuleDTO;
import net.sourceforge.tess4j.TesseractException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class OCRServiceIT {

    @Autowired
    private OCRService testSubject;

    @Test
    void getDataFromCourseDocument() throws TesseractException, IOException {
        // given
        File pdfFile = UploadService.getPDFFile();

        // when
        CourseDTO actualCourseDTO = testSubject.getDataFromCourseDocument(pdfFile);

        // then
        Assertions.assertThat(actualCourseDTO)
                .extracting(
                        CourseDTO::getName,
                        courseDTO -> courseDTO.getRegularModules().getRight().get(0),
                        courseDTO -> courseDTO.getRegularModules().getRight().get(30),
                        courseDTO -> courseDTO.getElectiveModules().getRight().get(0),
                        courseDTO -> courseDTO.getElectiveModules().getRight().get(30)
                ).contains(
                        "Informatik (Vertiefungsrichtung PI) StgPO 2019",
                        ModuleDTO.builder()
                                .id("45281")
                                .name("BWL")
                                .ects("5")
                                .build(),
                        ModuleDTO.builder()
                                .id("45182")
                                .name("Seminar Inhalt")
                                .ects("2.5")
                                .build(),
                        ModuleDTO.builder()
                                .id("46901")
                                .name("Adaptive Systeme")
                                .ects("5")
                                .build(),
                        ModuleDTO.builder()
                                .id("46810")
                                .name("Virtualisierung und Cloud Computing")
                                .ects("5")
                                .build()
                )
        ;
    }

    @Test
    void getDataFromAcknowledgementDocument() throws TesseractException, IOException {
        // given
        File pdfFile = UploadService.getPDFFile();

        // when
        AcknowledgementData actualAcknowledgementData = testSubject.getDataFromAcknowledgementDocument(pdfFile);

        // then
        assertThat(actualAcknowledgementData)
                .extracting(
                        acknowledgementData -> acknowledgementData.getRequestedCourse().getRight(),
                        acknowledgementData -> acknowledgementData.getOriginData().getMiddle(),
                        acknowledgementData -> acknowledgementData.getOriginData().getRight(),
                        acknowledgementData -> acknowledgementData.getModules().getMiddle().size(),
                        acknowledgementData -> acknowledgementData.getModules().getRight().size()
                ).contains(
                        "Informatik",
                        "Informatik",
                        "Technische Universität Dortmund",
                        17,
                        1
                )
        ;
    }
}
