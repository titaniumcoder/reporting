package io.github.titaniumcoder.toggl.reporting.toggl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class TimeEntryReporting {
    private Long id;
    private Long pid;
    private String project;
    private String client;
    private String task;
    private String description;
    @NotNull
    private ZonedDateTime start;
    @JsonProperty("end")
    @NotNull
    private ZonedDateTime end;
    @JsonProperty("dur")
    private int duraion; // duration ms duration
    private float billable;
    @JsonProperty("is_billable")
    private boolean isBillable;
    @JsonProperty("cur")
    private String currency;
    private List<String> tags;
}
