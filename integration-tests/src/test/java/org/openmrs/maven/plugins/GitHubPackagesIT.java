package org.openmrs.maven.plugins;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import org.openmrs.maven.plugins.model.DistroProperties;
import org.openmrs.maven.plugins.model.GitHubPackagesConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Integration test for GitHub Packages support
 */
public class GitHubPackagesIT extends AbstractSdkIT {

    @Test
    public void testGitHubPackagesConfigurationInDistroProperties() throws Exception {
        // Create a test distro properties file with GitHub Packages configuration
        DistroProperties distroProperties = createTestDistroProperties();
        
        // Test that GitHub Packages configuration is correctly parsed
        GitHubPackagesConfig config = distroProperties.getGitHubPackagesConfig();
        assertNotNull("GitHub Packages configuration should be present", config);
        assertTrue("GitHub Packages configuration should be valid", config.isValid());
        assertEquals("Repository URL should match", "https://maven.pkg.github.com/test-org/test-repo", config.getRepositoryUrl());
        assertEquals("Username should match", "testuser", config.getUsername());
        assertEquals("Token should match", "testtoken", config.getToken());
        assertEquals("Repository ID should be generated correctly", "github-test-org-test-repo", config.getRepositoryId());
    }

    @Test
    public void testGitHubPackagesConfigurationMissing() throws Exception {
        // Create a test distro properties file without GitHub Packages configuration
        DistroProperties distroProperties = createDistroPropertiesWithoutGitHubPackages();
        
        // Test that GitHub Packages configuration is null when not present
        GitHubPackagesConfig config = distroProperties.getGitHubPackagesConfig();
        assertNull("GitHub Packages configuration should be null when not present", config);
        assertFalse("Should not have GitHub Packages configuration", distroProperties.hasGitHubPackagesConfig());
    }

    @Test
    public void testGitHubPackagesConfigurationPartial() throws Exception {
        // Create a test distro properties file with partial GitHub Packages configuration
        DistroProperties distroProperties = createDistroPropertiesWithPartialGitHubPackages();
        
        // Test that GitHub Packages configuration is null when incomplete
        GitHubPackagesConfig config = distroProperties.getGitHubPackagesConfig();
        assertNull("GitHub Packages configuration should be null when incomplete", config);
        assertFalse("Should not have valid GitHub Packages configuration", distroProperties.hasGitHubPackagesConfig());
    }

    @Test
    public void testGitHubPackagesConfigurationSetAndGet() throws Exception {
        DistroProperties distroProperties = new DistroProperties("test", "2.5.0");
        
        // Create a GitHub Packages configuration
        GitHubPackagesConfig config = new GitHubPackagesConfig(
            "https://maven.pkg.github.com/test-org/test-repo",
            "testuser",
            "testtoken"
        );
        
        // Set the configuration
        distroProperties.setGitHubPackagesConfig(config);
        
        // Verify the configuration was set correctly
        assertTrue("Should have GitHub Packages configuration", distroProperties.hasGitHubPackagesConfig());
        
        GitHubPackagesConfig retrievedConfig = distroProperties.getGitHubPackagesConfig();
        assertNotNull("Retrieved configuration should not be null", retrievedConfig);
        assertEquals("Repository URL should match", config.getRepositoryUrl(), retrievedConfig.getRepositoryUrl());
        assertEquals("Username should match", config.getUsername(), retrievedConfig.getUsername());
        assertEquals("Token should match", config.getToken(), retrievedConfig.getToken());
    }

    private DistroProperties createTestDistroProperties() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("name", "test-distro");
        properties.setProperty("version", "1.0.0");
        properties.setProperty("war.openmrs", "2.5.0");
        
        // GitHub Packages configuration
        properties.setProperty("github.packages.url", "https://maven.pkg.github.com/test-org/test-repo");
        properties.setProperty("github.packages.username", "testuser");
        properties.setProperty("github.packages.token", "testtoken");
        
        // Some test artifacts
        properties.setProperty("omod.test-module", "1.0.0");
        properties.setProperty("omod.test-module.groupId", "com.test");
        
        return new DistroProperties(properties);
    }

    private DistroProperties createDistroPropertiesWithoutGitHubPackages() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("name", "test-distro");
        properties.setProperty("version", "1.0.0");
        properties.setProperty("war.openmrs", "2.5.0");
        
        // No GitHub Packages configuration
        
        return new DistroProperties(properties);
    }

    private DistroProperties createDistroPropertiesWithPartialGitHubPackages() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("name", "test-distro");
        properties.setProperty("version", "1.0.0");
        properties.setProperty("war.openmrs", "2.5.0");
        
        // Partial GitHub Packages configuration (missing token)
        properties.setProperty("github.packages.url", "https://maven.pkg.github.com/test-org/test-repo");
        properties.setProperty("github.packages.username", "testuser");
        // Missing github.packages.token
        
        return new DistroProperties(properties);
    }
} 