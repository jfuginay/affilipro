# OmniCOT Plugin for ATAK

## Overview
The OmniCOT plugin allows users to change the affiliation of any COT marker on the map with changes broadcasted to all BlueTeam members in real-time.

## Features
- **Radial Menu Integration**: Tap any COT marker to see an "Affiliation" button in the native ATAK radial menu
- **Secondary Menu**: Tap the Affiliation button to see 4 affiliation options (Unknown, Neutral, Friendly, Hostile)
- **COT Broadcasting**: Affiliation changes are immediately broadcasted to all connected ATAK clients via COT events
- **Visual Feedback**: Color-coded icons for each affiliation type
  - Unknown: Yellow
  - Neutral: Green
  - Friendly: Cyan
  - Hostile: Red

## How to Use

### Installation
1. Install ATAK on your device
2. Install the plugin APK: `ATAK-Plugin-omnicot-0.1--5.5.0-civ-debug.apk`
3. Start ATAK and enable the OmniCOT plugin

### Changing Marker Affiliation
1. **Tap** any COT marker on the map
2. The native ATAK radial menu appears
3. **Tap** the "Affiliation" button (orange circle icon)
4. A secondary radial menu appears with 4 colored squares
5. **Tap** the desired affiliation:
   - Yellow square = Unknown
   - Green square = Neutral
   - Cyan square = Friendly
   - Red square = Hostile
6. The affiliation change is immediately broadcasted to all team members

## Technical Details

### Files Created/Modified
1. **AffiliationUpdater.java** - Core logic for COT event creation and broadcasting
2. **AffiliationMenuFactory.java** - Radial menu integration
3. **OmniCOTPlugin.java** - Main plugin class with lifecycle management
4. **strings.xml** - Updated with affiliation labels
5. **Drawable resources** - Color-coded icons for each affiliation

### COT Broadcasting
The plugin uses `CotMapComponent.getInternalDispatcher()` to broadcast COT events to:
- All connected TAK servers
- All peer-to-peer connections
- All team members on the same network

### Affiliation Encoding
Affiliations are encoded in the COT type string at position 2:
- `a-f-G` = Friendly ground unit
- `a-h-G` = Hostile ground unit
- `a-n-G` = Neutral ground unit
- `a-u-G` = Unknown ground unit

The plugin preserves all other COT type information (dimension, function, etc.).

## Build Information
- **Plugin Version**: 0.1
- **ATAK Version**: 5.5.0 CIV
- **Build Type**: Debug
- **APK Location**: `app/build/outputs/apk/civ/debug/ATAK-Plugin-omnicot-0.1--5.5.0-civ-debug.apk`

## Future Enhancements (Optional)
1. Details panel integration for alternative UI
2. Custom PNG icons instead of XML drawables
3. Support for exercise and simulated affiliations
4. Affiliation history tracking
5. Permission controls for who can change affiliations

## Testing
To test the plugin:
1. Install on multiple ATAK devices on the same network
2. Create a marker on device A
3. Change its affiliation using the plugin on device A
4. Verify the affiliation change appears on device B
5. Check ATAK logs for "Affiliation update broadcasted" messages

## Troubleshooting
- If the affiliation button doesn't appear, check that the plugin is loaded in ATAK settings
- If changes don't broadcast, verify network connectivity between devices
- Check ATAK logs (tag: AffiliationPlugin, AffiliationUpdater, AffiliationMenuFactory)
