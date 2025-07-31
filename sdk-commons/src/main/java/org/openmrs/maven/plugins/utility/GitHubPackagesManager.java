package org.openmrs.maven.plugins.utility;

import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.openmrs.maven.plugins.model.GitHubPackagesConfig;

import java.util.List;

/**
 * Manages GitHub Packages repository configuration in Maven settings
 */
@Slf4j
public class GitHubPackagesManager {
    
    private final Settings settings;
    
    public GitHubPackagesManager(Settings settings) {
        this.settings = settings;
    }
    
    /**
     * Configures GitHub Packages repository in Maven settings
     * @param config GitHub Packages configuration
     * @throws MojoExecutionException if configuration fails
     */
    public void configureGitHubPackages(GitHubPackagesConfig config) throws MojoExecutionException {
        if (!config.isValid()) {
            throw new MojoExecutionException("Invalid GitHub Packages configuration. Repository URL, username, and token are required.");
        }
        
        try {
            // Add server configuration for authentication
            addServer(config);
            
            // Add repository to active profile
            addRepository(config);
            
            log.info("✅ GitHub Packages repository '{}' configured successfully", config.getRepositoryId());
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to configure GitHub Packages repository: " + e.getMessage(), e);
        }
    }
    
    private void addServer(GitHubPackagesConfig config) {
        // Remove existing server if it exists
        if (settings.getServers() != null) {
            settings.getServers().removeIf(server -> server.getId().equals(config.getRepositoryId()));
        }
        
        // Create new server configuration
        Server server = new Server();
        server.setId(config.getRepositoryId());
        server.setUsername(config.getUsername());
        server.setPassword(config.getToken());
        
        settings.addServer(server);
    }
    
    private void addRepository(GitHubPackagesConfig config) {
        // Find the active profile (usually "openmrs")
        Profile activeProfile = findActiveProfile();
        if (activeProfile == null) {
            throw new RuntimeException("No active profile found in Maven settings");
        }
        
        // Remove existing repository if it exists
        if (activeProfile.getRepositories() != null) {
            activeProfile.getRepositories().removeIf(repo -> repo.getId().equals(config.getRepositoryId()));
        }
        
        // Create new repository configuration
        Repository repository = new Repository();
        repository.setId(config.getRepositoryId());
        repository.setName("GitHub Packages - " + config.getRepositoryId());
        repository.setUrl(config.getRepositoryUrl());
        
        // Enable snapshots
        org.apache.maven.settings.RepositoryPolicy snapshots = new org.apache.maven.settings.RepositoryPolicy();
        snapshots.setEnabled(true);
        repository.setSnapshots(snapshots);
        
        // Enable releases
        org.apache.maven.settings.RepositoryPolicy releases = new org.apache.maven.settings.RepositoryPolicy();
        releases.setEnabled(true);
        repository.setReleases(releases);
        
        activeProfile.addRepository(repository);
    }
    
    private Profile findActiveProfile() {
        if (settings.getActiveProfiles() == null || settings.getActiveProfiles().isEmpty()) {
            return null;
        }
        
        String activeProfileId = settings.getActiveProfiles().get(0);
        
        if (settings.getProfiles() != null) {
            for (Profile profile : settings.getProfiles()) {
                if (profile.getId().equals(activeProfileId)) {
                    return profile;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Removes GitHub Packages configuration from Maven settings
     * @param repositoryId the repository ID to remove
     */
    public void removeGitHubPackages(String repositoryId) {
        // Remove server
        if (settings.getServers() != null) {
            settings.getServers().removeIf(server -> server.getId().equals(repositoryId));
        }
        
        // Remove repository from active profile
        Profile activeProfile = findActiveProfile();
        if (activeProfile != null && activeProfile.getRepositories() != null) {
            activeProfile.getRepositories().removeIf(repo -> repo.getId().equals(repositoryId));
        }
        
        log.info("✅ GitHub Packages repository '{}' removed from Maven settings", repositoryId);
    }
} 