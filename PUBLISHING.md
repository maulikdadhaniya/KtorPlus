# KtorPlus Publishing Guide

This guide explains how to publish the KtorPlus library to various repositories.

---

## Table of Contents
1. [Quick Start - Local Publishing](#quick-start---local-publishing)
2. [Publishing to Maven Local](#publishing-to-maven-local)
3. [Publishing to GitHub Packages](#publishing-to-github-packages)
4. [Publishing to Maven Central](#publishing-to-maven-central)
5. [Version Management](#version-management)
6. [Troubleshooting](#troubleshooting)

---

## Quick Start - Local Publishing

### Publish to Local Repository

The easiest way to test your library is to publish it locally:

```bash
# Publish to build/repo directory
./gradlew :KtorPlus:publishAllPublicationsToLocalRepository

# Or publish to Maven Local (~/.m2/repository)
./gradlew :KtorPlus:publishToMavenLocal
```

### Use the Published Library

After publishing locally, you can use it in other projects:

**In settings.gradle.kts:**
```kotlin
dependencyResolutionManagement {
    repositories {
        // For build/repo
        maven { url = uri("file:///path/to/TestKMP/build/repo") }
        
        // For Maven Local
        mavenLocal()
        
        mavenCentral()
    }
}
```

**In build.gradle.kts:**
```kotlin
dependencies {
    implementation("com.maulik.ktorplus:ktorplus:1.0.0")
}
```

---

## Publishing to Maven Local

Maven Local is perfect for testing your library locally before publishing publicly.

### Step 1: Publish

```bash
./gradlew :KtorPlus:publishToMavenLocal
```

This publishes to `~/.m2/repository/com/maulik/ktorplus/ktorplus/1.0.0/`

### Step 2: Verify

```bash
ls ~/.m2/repository/com/maulik/ktorplus/ktorplus/1.0.0/
```

You should see:
- `ktorplus-1.0.0.aar` (Android)
- `ktorplus-1.0.0.jar` (JVM)
- `ktorplus-1.0.0.pom`
- `ktorplus-1.0.0-sources.jar`
- Various metadata files

### Step 3: Use in Another Project

**settings.gradle.kts:**
```kotlin
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}
```

**build.gradle.kts:**
```kotlin
dependencies {
    implementation("com.maulik.ktorplus:ktorplus:1.0.0")
}
```

---

## Publishing to GitHub Packages

GitHub Packages is a good option for private or organization-specific libraries.

### Step 1: Configure GitHub Token

Create a Personal Access Token (PAT) with `write:packages` permission:
1. Go to GitHub Settings → Developer settings → Personal access tokens
2. Generate new token (classic)
3. Select `write:packages` and `read:packages` scopes
4. Copy the token

### Step 2: Add Credentials

**Option A: In gradle.properties (NOT recommended for public repos)**
```properties
gpr.user=your-github-username
gpr.token=your-github-token
```

**Option B: Environment Variables (Recommended)**
```bash
export GITHUB_ACTOR=your-github-username
export GITHUB_TOKEN=your-github-token
```

**Option C: In ~/.gradle/gradle.properties (Recommended)**
```properties
gpr.user=your-github-username
gpr.token=your-github-token
```

### Step 3: Update build.gradle.kts

Uncomment the GitHub Packages section in `KtorPlus/build.gradle.kts`:

```kotlin
maven {
    name = "GitHubPackages"
    url = uri("https://maven.pkg.github.com/yourusername/ktorplus")
    credentials {
        username = findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
        password = findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")
    }
}
```

Replace `yourusername/ktorplus` with your actual GitHub username and repository name.

### Step 4: Publish

```bash
./gradlew :KtorPlus:publishAllPublicationsToGitHubPackagesRepository
```

### Step 5: Use in Another Project

**settings.gradle.kts:**
```kotlin
dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/yourusername/ktorplus")
            credentials {
                username = findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
        mavenCentral()
    }
}
```

**build.gradle.kts:**
```kotlin
dependencies {
    implementation("com.maulik.ktorplus:ktorplus:1.0.0")
}
```

---

## Publishing to Maven Central

Maven Central is the standard repository for public Java/Kotlin libraries.

### Prerequisites

1. **Sonatype Account**: Register at https://issues.sonatype.org/
2. **Domain Verification**: Verify ownership of `com.maulik` domain or use `io.github.yourusername`
3. **GPG Key**: Generate and publish a GPG key for signing

### Step 1: Generate GPG Key

```bash
# Generate key
gpg --gen-key

# List keys
gpg --list-keys

# Export public key
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID

# Export private key (keep this secure!)
gpg --export-secret-keys YOUR_KEY_ID > secring.gpg
```

### Step 2: Configure Credentials

**In ~/.gradle/gradle.properties:**
```properties
# Sonatype credentials
ossrhUsername=your-sonatype-username
ossrhPassword=your-sonatype-password

# GPG signing
signing.keyId=YOUR_KEY_ID
signing.password=YOUR_GPG_PASSWORD
signing.secretKeyRingFile=/path/to/secring.gpg
```

### Step 3: Update build.gradle.kts

Uncomment the Maven Central and signing sections in `KtorPlus/build.gradle.kts`:

```kotlin
// Publishing
maven {
    name = "MavenCentral"
    url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
    credentials {
        username = findProperty("ossrhUsername") as String? ?: System.getenv("OSSRH_USERNAME")
        password = findProperty("ossrhPassword") as String? ?: System.getenv("OSSRH_PASSWORD")
    }
}

// Signing
signing {
    useGpgCmd()
    sign(publishing.publications)
}
```

### Step 4: Publish

```bash
# Publish to staging repository
./gradlew :KtorPlus:publishAllPublicationsToMavenCentralRepository

# Or use the Gradle Portal plugin for easier publishing
./gradlew :KtorPlus:publishToSonatype closeAndReleaseSonatypeStagingRepository
```

### Step 5: Release

1. Go to https://s01.oss.sonatype.org/
2. Login with your credentials
3. Find your staging repository
4. Click "Close" to validate
5. Click "Release" to publish to Maven Central

### Step 6: Wait for Sync

It can take 2-4 hours for your library to appear on Maven Central and up to 24 hours to be searchable.

### Step 7: Use in Projects

**build.gradle.kts:**
```kotlin
dependencies {
    implementation("com.maulik.ktorplus:ktorplus:1.0.0")
}
```

---

## Version Management

### Update Version

Edit `KtorPlus/gradle.properties`:

```properties
LIBRARY_VERSION=1.0.1
```

### Semantic Versioning

Follow semantic versioning (MAJOR.MINOR.PATCH):

- **MAJOR**: Breaking changes
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes

Examples:
- `1.0.0` - Initial release
- `1.0.1` - Bug fix
- `1.1.0` - New feature
- `2.0.0` - Breaking changes

### Version Naming Conventions

```properties
# Stable release
LIBRARY_VERSION=1.0.0

# Beta release
LIBRARY_VERSION=1.0.0-beta01

# Alpha release
LIBRARY_VERSION=1.0.0-alpha01

# Release candidate
LIBRARY_VERSION=1.0.0-rc01

# Snapshot (development)
LIBRARY_VERSION=1.0.0-SNAPSHOT
```

---

## Publishing Checklist

Before publishing, ensure:

- [ ] All tests pass: `./gradlew :KtorPlus:test`
- [ ] Code is properly documented
- [ ] README.md is up to date
- [ ] CHANGELOG.md is updated
- [ ] Version number is correct
- [ ] License file exists
- [ ] No sensitive information in code
- [ ] All dependencies are properly declared
- [ ] Build succeeds: `./gradlew :KtorPlus:build`

---

## Gradle Tasks Reference

### Publishing Tasks

```bash
# List all publishing tasks
./gradlew :KtorPlus:tasks --group=publishing

# Publish to Maven Local
./gradlew :KtorPlus:publishToMavenLocal

# Publish to specific repository
./gradlew :KtorPlus:publishAllPublicationsToLocalRepository
./gradlew :KtorPlus:publishAllPublicationsToGitHubPackagesRepository
./gradlew :KtorPlus:publishAllPublicationsToMavenCentralRepository

# Publish specific platform
./gradlew :KtorPlus:publishAndroidReleasePublicationToMavenLocal
./gradlew :KtorPlus:publishJvmPublicationToMavenLocal
./gradlew :KtorPlus:publishIosArm64PublicationToMavenLocal
```

### Build Tasks

```bash
# Clean build
./gradlew :KtorPlus:clean

# Build all platforms
./gradlew :KtorPlus:build

# Build specific platform
./gradlew :KtorPlus:assembleDebug
./gradlew :KtorPlus:jvmJar
```

---

## Troubleshooting

### Issue: "Could not find com.maulik.ktorplus:ktorplus:1.0.0"

**Solution**: Ensure the library is published and the repository is configured correctly.

```bash
# Verify publication
./gradlew :KtorPlus:publishToMavenLocal
ls ~/.m2/repository/com/maulik/ktorplus/ktorplus/1.0.0/
```

### Issue: "Signing failed"

**Solution**: Check GPG configuration:

```bash
# Verify GPG key
gpg --list-keys

# Test signing
echo "test" | gpg --clearsign
```

### Issue: "401 Unauthorized" when publishing

**Solution**: Check credentials:

```bash
# For GitHub Packages
echo $GITHUB_TOKEN

# For Maven Central
cat ~/.gradle/gradle.properties | grep ossrh
```

### Issue: "Publication not found"

**Solution**: Ensure the publication is created:

```bash
./gradlew :KtorPlus:tasks --group=publishing
```

### Issue: Build fails with "Duplicate class"

**Solution**: Check for conflicting dependencies:

```bash
./gradlew :KtorPlus:dependencies
```

---

## CI/CD Integration

### GitHub Actions

Create `.github/workflows/publish.yml`:

```yaml
name: Publish Library

on:
  release:
    types: [created]

jobs:
  publish:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
      
      - name: Publish to GitHub Packages
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: ./gradlew :KtorPlus:publishAllPublicationsToGitHubPackagesRepository
      
      - name: Publish to Maven Central
        env:
          OSSRH_USERNAME: ${{ secrets.OSSRH_USERNAME }}
          OSSRH_PASSWORD: ${{ secrets.OSSRH_PASSWORD }}
          SIGNING_KEY_ID: ${{ secrets.SIGNING_KEY_ID }}
          SIGNING_PASSWORD: ${{ secrets.SIGNING_PASSWORD }}
        run: ./gradlew :KtorPlus:publishAllPublicationsToMavenCentralRepository
```

---

## Next Steps

1. **Test Locally**: Publish to Maven Local and test in another project
2. **Choose Repository**: Decide between GitHub Packages or Maven Central
3. **Set Up Credentials**: Configure authentication
4. **Publish**: Run the publish task
5. **Document**: Update README with installation instructions
6. **Announce**: Share your library with the community!

---

## Additional Resources

- [Maven Central Guide](https://central.sonatype.org/publish/publish-guide/)
- [GitHub Packages Documentation](https://docs.github.com/en/packages)
- [Gradle Publishing Plugin](https://docs.gradle.org/current/userguide/publishing_maven.html)
- [Semantic Versioning](https://semver.org/)

---

**Happy Publishing! 🚀**
