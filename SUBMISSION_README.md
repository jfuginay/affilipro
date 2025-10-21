# OmniCOT - TAK Third-Party Plugin Submission

## Plugin Information
- **Plugin Name**: OmniCOT
- **Version**: 0.1
- **Target ATAK Version**: 5.5.0 (CIV, MIL, GOV compatible)
- **Package Name**: com.atakmap.android.omnicot.plugin
- **Description**: Allows users to change COT marker affiliations (Unknown, Neutral, Friendly, Hostile) with real-time broadcasting to all team members

## Build Instructions

### Prerequisites
- Java 17 (OpenJDK or equivalent)
- Android SDK
- ATAK SDK 5.5.0+ (fetched via atak-gradle-takdev plugin)

### Important Build Notes
**Typst documentation build has been disabled** to comply with TAK pipeline requirements (no external downloads during build). The plugin functionality is complete and tested without documentation generation.

### Build Commands
```bash
# Navigate to plugin directory
cd omnicot

# Build debug version
./gradlew assembleCivDebug

# Build release version (for all flavors)
./gradlew assembleCivRelease
./gradlew assembleMilRelease
./gradlew assembleGovRelease

# Output APK locations:
# - Debug: app/build/outputs/apk/civ/debug/ATAK-Plugin-omnicot-0.1--5.5.0-civ-debug.apk
# - Release: app/build/outputs/apk/civ/release/ATAK-Plugin-omnicot-0.1--5.5.0-civ-release.apk
```

## Key Features
1. **Radial Menu Integration**: Adds "Affiliation" button to native ATAK marker radial menus
2. **COT Broadcasting**: Uses `CotMapComponent.getInternalDispatcher()` to broadcast changes to all connected clients
3. **Multi-Affiliation Support**: Unknown (u), Neutral (n), Friendly (f), Hostile (h)
4. **Visual Feedback**: Color-coded icons for each affiliation type

## Technical Implementation

### Source Files
- `AffiliationUpdater.java` - Core COT event handling and broadcasting logic
- `AffiliationMenuFactory.java` - Radial menu factory implementation
- `OmniCOTPlugin.java` - Main plugin lifecycle management
- Icon resources in `app/src/main/res/drawable/`

### ATAK APIs Used
- `MapMenuReceiver` - For radial menu registration
- `MapMenuFactory` - For creating custom menu items
- `CotEventFactory` - For creating COT events from map items
- `CotMapComponent.getInternalDispatcher()` - For broadcasting COT events
- `MapDataRef` - For icon resource handling

### COT Type Modification
The plugin modifies the affiliation character at index 2 of the COT type string:
- Original: `a-f-G-E-V` (Friendly ground equipment vehicle)
- Modified: `a-h-G-E-V` (Hostile ground equipment vehicle)

All other type information (dimension, function, status) is preserved.

## Security Considerations
- No network connections initiated by plugin
- Uses standard ATAK COT dispatcher for all communications
- No sensitive data storage
- No special permissions required beyond standard ATAK plugin permissions

## Testing Performed
- [x] Plugin loads successfully in ATAK CIV 5.5.0
- [x] Radial menu button appears on all COT markers
- [x] Affiliation submenu displays correctly
- [x] COT events are created and dispatched properly
- [x] Local marker updates reflect immediately
- [ ] Multi-device broadcast testing (requires multiple ATAK instances)

## Known Limitations
1. Only supports basic affiliations (u/n/f/h), not exercise or simulated variants
2. No undo functionality for affiliation changes
3. No permission controls - any user can change any marker affiliation

## Dependencies
All dependencies are provided by the ATAK SDK. No external libraries required.

## Support
For questions or issues with this plugin submission:
- Review `PLUGIN_SUMMARY.md` for detailed usage instructions
- Check ATAK logs for tags: `AffiliationPlugin`, `AffiliationUpdater`, `AffiliationMenuFactory`

## Signing Instructions
Please sign with TAK standard plugin signing certificate. No special signing requirements.

---
**Submission Date**: October 18, 2025
**Built Against**: ATAK-CIV-5.5.1.6-SDK
**Tested On**: ATAK CIV 5.5.0
