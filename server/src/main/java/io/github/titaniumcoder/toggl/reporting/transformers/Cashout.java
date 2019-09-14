package io.github.titaniumcoder.toggl.reporting.transformers;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Cashout {
    @NotNull
    private String client;
    private double amount;
}
