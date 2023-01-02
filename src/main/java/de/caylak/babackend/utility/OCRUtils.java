package de.caylak.babackend.utility;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class OCRUtils {

    private OCRUtils() throws IllegalAccessException {
        throw new IllegalAccessException("This is a Utility class and cannot be instantiated");
    }

    public static List<String> extractValues(String ocrResult) {
        return Arrays.stream(ocrResult.split(System.lineSeparator()))
                .filter(StringUtils::isNotBlank)
                .map(s -> s.replace(">", "5"))
                .toList();
    }

    public static Pattern modulePattern() {
        return Pattern.compile("^(?<id>\\d+) (?<name>.+) (?<ects>\\d+(,\\d+)?)$");
    }

    public static Pattern originModulePattern() {
        return Pattern.compile("(?<name>.+) (?<ects>\\d{1,2}.\\d.?) (?<grade>\\d{1,2}.\\d.?) (?<ackGrade>.+)");
    }
}
