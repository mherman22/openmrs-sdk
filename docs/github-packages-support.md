# GitHub Packages Support in OpenMRS SDK

The OpenMRS SDK now supports downloading artifacts from GitHub Packages repositories. This feature allows you to use modules, content packages, and other artifacts hosted on GitHub Packages without manually configuring Maven settings.

## Overview

GitHub Packages is a package registry that allows you to host your packages alongside your code. The OpenMRS SDK can now automatically configure Maven to authenticate with GitHub Packages and download artifacts from your private or public repositories.

## Configuration

### Method 1: Using Distro Properties (Recommended)

Add GitHub Packages configuration to your `openmrs-distro.properties` file:

```properties
# GitHub Packages Configuration
github.packages.url=https://maven.pkg.github.com/YOUR_ORG/YOUR_REPO
github.packages.username=YOUR_GITHUB_USERNAME
github.packages.token=YOUR_GITHUB_TOKEN

# Your artifacts from GitHub Packages
omod.my-module=1.0.0
omod.my-module.groupId=com.example
omod.my-module.type=jar

owa.my-owa=1.0.0
owa.my-owa.groupId=com.example
owa.my-owa.type=zip

content.my-content=1.0.0
content.my-content.groupId=com.example
content.my-content.type=zip
```

### Method 2: Using Command Line Parameters (Fetch Plugin)

When using the `fetch` goal, you can provide GitHub Packages credentials as parameters:

```bash
mvn openmrs-sdk:fetch \
  -DartifactId=my-module \
  -Dversion=1.0.0 \
  -Dgithub.packages.url=https://maven.pkg.github.com/YOUR_ORG/YOUR_REPO \
  -Dgithub.packages.username=YOUR_GITHUB_USERNAME \
  -Dgithub.packages.token=YOUR_GITHUB_TOKEN
```

## GitHub Token Setup

To use GitHub Packages, you need a GitHub Personal Access Token with the appropriate permissions:

1. Go to GitHub Settings → Developer settings → Personal access tokens
2. Generate a new token with the following scopes:
   - `read:packages` - to download packages
   - `repo` - if accessing private repositories

## Repository URL Format

The repository URL should follow this format:
```
https://maven.pkg.github.com/OWNER/REPOSITORY
```

For example:
```
https://maven.pkg.github.com/openmrs/openmrs-module-example
```

## Usage Examples

### Building a Distribution with GitHub Packages

1. Create an `openmrs-distro.properties` file with GitHub Packages configuration
2. Run the build-distro goal:

```bash
mvn openmrs-sdk:build-distro
```

The SDK will automatically:
- Configure Maven settings with GitHub Packages authentication
- Download artifacts from both GitHub Packages and standard repositories
- Build your distribution

### Setting Up a Server with GitHub Packages

1. Create an `openmrs-distro.properties` file with GitHub Packages configuration
2. Run the setup goal:

```bash
mvn openmrs-sdk:setup
```

### Fetching Individual Artifacts

```bash
mvn openmrs-sdk:fetch \
  -DartifactId=my-module \
  -Dversion=1.0.0 \
  -Dgithub.packages.url=https://maven.pkg.github.com/YOUR_ORG/YOUR_REPO \
  -Dgithub.packages.username=YOUR_GITHUB_USERNAME \
  -Dgithub.packages.token=YOUR_GITHUB_TOKEN
```

## Security Considerations

- **Token Security**: Never commit your GitHub token to version control
- **Environment Variables**: Consider using environment variables for sensitive data
- **Token Permissions**: Use the minimum required permissions for your token
- **Token Rotation**: Regularly rotate your GitHub tokens

## Troubleshooting

### Common Issues

1. **Authentication Failed**
   - Verify your GitHub token has the correct permissions
   - Check that the repository URL is correct
   - Ensure the token hasn't expired

2. **Artifact Not Found**
   - Verify the artifact exists in the specified repository
   - Check the groupId, artifactId, and version are correct
   - Ensure the artifact is published to GitHub Packages

3. **Repository Access Denied**
   - Verify your GitHub account has access to the repository
   - Check that the token has the necessary scopes

### Debug Information

The SDK will log information about GitHub Packages configuration:
- ✅ Success messages when repositories are configured
- Error messages if configuration fails
- Repository URLs being used

## Example Configuration Files

See `examples/github-packages-example.properties` for a complete example configuration.

## Migration from Manual Maven Settings

If you previously configured GitHub Packages manually in your `~/.m2/settings.xml`, you can now remove those configurations and use the SDK's built-in support instead.

The SDK will automatically:
- Add the repository to the active Maven profile
- Configure authentication
- Handle repository cleanup when no longer needed 