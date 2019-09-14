package io.github.titaniumcoder.toggl.reporting.toggl;

import io.github.titaniumcoder.toggl.reporting.config.TogglConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class TogglWebClient {
    private final TogglConfiguration config;
    private final String apiToken;
    private final Long workspaceId;

    private final static Logger LOGGER = LoggerFactory.getLogger(TogglWebClient.class);

    public TogglWebClient(TogglConfiguration config) {
        this.config = config;

        this.apiToken = config.getApiToken();
        this.workspaceId = config.getWorkspaceId();
    }
}
