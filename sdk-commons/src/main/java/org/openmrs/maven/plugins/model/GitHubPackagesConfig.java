package org.openmrs.maven.plugins.model;

import lombok.Data;

/**
 * Configuration for GitHub Packages repository access
 */
@Data
public class GitHubPackagesConfig {
    
    private String repositoryUrl;
    private String username;
    private String token;
    private String repositoryId;
    
    public GitHubPackagesConfig() {}
    
    public GitHubPackagesConfig(String repositoryUrl, String username, String token) {
        this.repositoryUrl = repositoryUrl;
        this.username = username;
        this.token = token;
        this.repositoryId = "github-" + extractRepositoryId(repositoryUrl);
    }
    
    private String extractRepositoryId(String url) {
        // Extract repository ID from URL like https://maven.pkg.github.com/OWNER/REPOSITORY
        if (url != null && url.contains("maven.pkg.github.com/")) {
            String[] parts = url.split("/");
            if (parts.length >= 2) {
                return parts[parts.length - 2] + "-" + parts[parts.length - 1];
            }
        }
        return "packages";
    }
    
    public boolean isValid() {
        return repositoryUrl != null && !repositoryUrl.trim().isEmpty() &&
               username != null && !username.trim().isEmpty() &&
               token != null && !token.trim().isEmpty();
    }
} 