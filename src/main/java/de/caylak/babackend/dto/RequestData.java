package de.caylak.babackend.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class RequestData {
    String requestedCourse;
    String originCourse;
    String originUniversity;
}
