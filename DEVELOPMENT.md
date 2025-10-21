# OmniCOT - Development and Submission Guide

## Quick Start for Development

### Option 1: Clone from GitHub (Recommended)
```bash
# Navigate to ATAK SDK plugins directory
cd /path/to/ATAK-CIV-5.5.1.6-SDK/plugins/

# Clone the repository
git clone https://github.com/jfuginay/omnicot.git

# Enter directory
cd omnicot

# Copy and configure local properties
cp template.local.properties local.properties
# Edit local.properties with your SDK paths

# Build
./gradlew assembleCivDebug
```

### Option 2: Download ZIP from GitHub
1. Go to https://github.com/jfuginay/omnicot
2. Click **Code** → **Download ZIP**
3. Extract to ATAK SDK plugins directory
4. Rename `omnicot-main` to `omnicot` (GitHub adds `-main` suffix)
5. Continue with configuration and build as above

## TAK Third Party Submission

### Method 1: Download from GitHub (Simple)
1. Go to https://github.com/jfuginay/omnicot
2. Click **Code** → **Download ZIP**
3. Rename `omnicot-main.zip` to `omnicot.zip` (optional)
4. **Submit directly to TAK pipeline** - this zip contains everything needed!

### Method 2: Create Custom Submission ZIP
```bash
cd /path/to/ATAK-CIV-5.5.1.6-SDK/plugins/

zip -r omnicot-submission.zip omnicot/ \
  -x "*/build/*" \
     "*/.git/*" \
     "*/.gradle/*" \
     "*/local.properties" \
     "*/.DS_Store" \
     "*/screenshots/*.png"
```

## What's Included in GitHub Repository

### ✅ Required for Build
- `gradle/wrapper/gradle-wrapper.jar` - Gradle wrapper executable
- `gradle/wrapper/gradle-wrapper.properties` - Gradle version config
- `gradlew` & `gradlew.bat` - Wrapper scripts
- `build.gradle` & `settings.gradle` - Build configuration
- `app/build.gradle` - App module build config
- All source code (`.java` files)
- All resources (layouts, drawables, strings, etc.)
- `AndroidManifest.xml`
- ProGuard configuration files

### ✅ Documentation
- `README.md` - Professional README with features and installation
- `LICENSE` - Apache 2.0 license
- `CONTRIBUTING.md` - Contribution guidelines
- `PLUGIN_SUMMARY.md` - Plugin description for ATAK
- `SUBMISSION_README.md` - Submission documentation
- `.github/ISSUE_TEMPLATE/` - Bug report and feature request templates

### ❌ Excluded (via .gitignore)
- `build/` - Build outputs (generated during build)
- `.gradle/` - Gradle cache (downloaded during build)
- `local.properties` - Local SDK paths (user-specific)
- `.idea/` - IDE settings
- `*.apk` - Built APK files
- Screenshots (kept in repo but can be excluded from submission)

## Build Verification

### Test GitHub Download
```bash
# Download and test
cd /tmp
wget https://github.com/jfuginay/omnicot/archive/refs/heads/main.zip
unzip main.zip
cd omnicot-main

# Verify Gradle wrapper
ls -la gradle/wrapper/
# Should see: gradle-wrapper.jar, gradle-wrapper.properties

# Test Gradle execution
./gradlew --version
# Should output: Gradle 8.13
```

### Expected Build Process
1. TAK pipeline extracts your zip
2. Runs `./gradlew assembleCivRelease` (or similar)
3. Gradle wrapper downloads Gradle 8.13 (if not cached)
4. Gradle downloads dependencies
5. Builds APK
6. Runs security scans (Fortify)
7. Produces final plugin APK

## Troubleshooting

### "Could not find or load main class org.gradle.wrapper.GradleWrapperMain"
- **Cause**: Gradle wrapper JAR is missing
- **Fix**: Ensure `gradle/wrapper/gradle-wrapper.jar` is included in submission
- **Verify**: `unzip -l your-submission.zip | grep gradle-wrapper.jar`

### "SDK location not found"
- **Cause**: `local.properties` is missing or incorrect
- **Fix**: Copy `template.local.properties` to `local.properties` and configure paths
- **Note**: This file is user-specific and excluded from git/submissions

### Build fails with dependency errors
- **Cause**: Offline build or network issues
- **Fix**: Ensure build machine has internet access to download dependencies
- **TAK Pipeline**: Should have proxy configured (proxy:3128 as shown in logs)

## File Size Comparison

| Package | Size | Includes | Use Case |
|---------|------|----------|----------|
| GitHub Download | ~850 KB | All source + wrapper + docs | Development & Submission ✅ |
| Custom ZIP (no screenshots) | ~800 KB | All source + wrapper | Minimal submission |
| Custom ZIP (with screenshots) | ~9 MB | Source + wrapper + 3 PNG screenshots | Documentation |

## Recommended Workflow

### For Development
```bash
git clone https://github.com/jfuginay/omnicot.git
cd omnicot
cp template.local.properties local.properties
# Edit local.properties
./gradlew assembleCivDebug
adb install -r app/build/outputs/apk/civ/debug/*.apk
```

### For TAK Submission
1. Go to https://github.com/jfuginay/omnicot
2. Click **Code** → **Download ZIP**
3. Submit the downloaded zip directly (or rename if desired)
4. TAK pipeline will build successfully ✅

### For Collaborative Development
```bash
# Make changes
git add .
git commit -m "feat: description of changes"
git push origin main

# On another computer
git pull origin main
./gradlew assembleCivDebug
```

## Notes

- The GitHub repository is ready for TAK submission **as-is**
- No need to create custom submission zips - GitHub's download works perfectly
- All required files (including Gradle wrapper) are tracked in git
- Build artifacts are excluded via `.gitignore` to keep repo clean
- Screenshots are included in repo for README but excluded from recommended submissions

---

**Repository**: https://github.com/jfuginay/omnicot  
**Issues**: https://github.com/jfuginay/omnicot/issues  
**License**: Apache 2.0
