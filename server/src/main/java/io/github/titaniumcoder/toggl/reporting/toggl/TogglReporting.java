package io.github.titaniumcoder.toggl.reporting.toggl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TogglReporting {
    @JsonProperty("total_count")
    private int totalCount;
    @JsonProperty("per_page")
    private int perPage;
    @JsonProperty("total_grand")
    private Long totalGrand;
    @JsonProperty("total_billable")
    private Long totalBillable;
    @JsonProperty("total_currencies")
    private List<TogglCurrency> totalCurrencies;
    private List<TimeEntryReporting> data;
}
