package io.github.titaniumcoder.toggl.reporting.toggl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TogglIClient {
    @JsonProperty("client")
    private String name;
}
