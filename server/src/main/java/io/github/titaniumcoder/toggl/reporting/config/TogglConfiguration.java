package io.github.titaniumcoder.toggl.reporting.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;

@ConfigurationProperties("application")
@Data
public class TogglConfiguration {
    private String apiToken = "";
    private Long workspaceId = -1L;
    private SecurityEncodingConfiguration security = new SecurityEncodingConfiguration();

    @Data
    @ConfigurationProperties("security")
    public static class SecurityEncodingConfiguration {
        private String secret = "";
        private String username = "";
        private String password = "";
        private Long expiration = -1L;
    }
}
