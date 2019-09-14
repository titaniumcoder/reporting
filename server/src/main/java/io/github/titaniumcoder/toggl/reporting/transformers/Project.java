package io.github.titaniumcoder.toggl.reporting.transformers;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Project {
    @NotNull
    private String name;
    private int minutes;
}
