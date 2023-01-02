import de.caylak.babackend.dto.CourseDTO;
import de.caylak.babackend.dto.ModuleDTO;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ExamplePDFUtils {

    public CourseDTO getExampleCourseDTO() {
        return CourseDTO.builder()
                .name("Informatik")
                .regularModules(Pair.of(null, getRegularModules()))
                .electiveModules(Pair.of(null, getElectiveModules()))
                .build();
    }

    private List<ModuleDTO> getRegularModules() {
        return List.of(
                ModuleDTO.builder()
                        .id("45281")
                        .name("BWL")
                        .ects("5")
                        .build(),
                ModuleDTO.builder()
                        .id("45182")
                        .name("Seminar Inhalt")
                        .ects("2.5F")
                        .build()
        );
    }

    private List<ModuleDTO> getElectiveModules() {
        return List.of(
                ModuleDTO.builder()
                        .id("46901")
                        .name("Adaptive Systeme")
                        .ects("5")
                        .build(),
                ModuleDTO.builder()
                        .id("46810")
                        .name("Virtualisierung und Cloud Computing")
                        .ects("5F")
                        .build()
        );
    }
}
