package io.github.titaniumcoder.toggl.reporting.transformers;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ReportingModel {
    private String client;
    private Long clientId;
    private LocalDate from;
    private LocalDate to;
    private List<Project> projects;
    private List<List<TimeEntry>> timeEntries;
}
