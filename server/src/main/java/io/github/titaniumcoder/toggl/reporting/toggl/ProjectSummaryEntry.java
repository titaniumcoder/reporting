package io.github.titaniumcoder.toggl.reporting.toggl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProjectSummaryEntry {
    private TogglIClient title;
    private Long time;
    @JsonProperty("cur")
    private String currency;
    private Double sum;
    private Double rate;
}
