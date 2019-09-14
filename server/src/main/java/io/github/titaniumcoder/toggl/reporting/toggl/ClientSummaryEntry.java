package io.github.titaniumcoder.toggl.reporting.toggl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ClientSummaryEntry {
    private TogglIClient title;
    private long time;
    @JsonProperty("total_currencies")
    private List<TogglCurrency> totalCurrencies;
}
