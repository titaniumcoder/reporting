package io.github.titaniumcoder.toggl.reporting.transformers;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class TimeEntry {
    private long id;
    @NotNull
    private LocalDate day;
    private String project;
    @NotNull
    private ZonedDateTime startdate;
    @NotNull
    private ZonedDateTime enddate;
    private int minutes;
    private String description;
    private List<String> tags;
}
