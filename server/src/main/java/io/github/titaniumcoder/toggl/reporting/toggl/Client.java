package io.github.titaniumcoder.toggl.reporting.toggl;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Client {
    private long id;
    private long wid;

    @NotNull
    private String name;

    private String notes;
}
