# AOI Management - Quick Start Guide

## What You Get

When you click the OmniCOT plugin button, you now see an **AOI Management Dashboard** instead of a simple text view.

## Dashboard Layout

```
┌─────────────────────────────────────┐
│  AOI Management          [Refresh]  │  ← Header
├─────────────────────────────────────┤
│                                     │
│  ┌───────────────────────────────┐ │
│  │ My Target Area        Circle  │ │  ← AOI Card
│  │ Area: 2.5 km²                 │ │
│  │ Center: 38.12345, -77.56789   │ │
│  │              [Zoom To] [Delete]│ │
│  └───────────────────────────────┘ │
│                                     │
│  ┌───────────────────────────────┐ │
│  │ Search Zone      Rectangle    │ │  ← Another AOI
│  │ Area: 1.8 km²                 │ │
│  │ Center: 38.54321, -77.98765   │ │
│  │              [Zoom To] [Delete]│ │
│  └───────────────────────────────┘ │
│                                     │
│  ┌───────────────────────────────┐ │
│  │ Patrol Route      Polygon     │ │  ← Another AOI
│  │ Area: 0.3 km²                 │ │
│  │ Center: 38.99999, -77.11111   │ │
│  │              [Zoom To] [Delete]│ │
│  └───────────────────────────────┘ │
│                                     │
└─────────────────────────────────────┘
```

## If No Shapes Exist

```
┌─────────────────────────────────────┐
│  AOI Management          [Refresh]  │
├─────────────────────────────────────┤
│                                     │
│           [AOI Icon]                │
│                                     │
│        No AOIs Found                │
│                                     │
│  To create an Area of Interest:    │
│                                     │
│  1. Tap the Drawing Tools icon      │
│     on the map                      │
│  2. Select Rectangle, Circle,       │
│     or Freehand                     │
│  3. Draw your AOI on the map        │
│  4. Return here to manage it        │
│                                     │
└─────────────────────────────────────┘
```

## How to Use

### Create an AOI
1. Close the dashboard (or leave it open)
2. On the ATAK map, tap the **Drawing Tools** button
3. Select a shape type:
   - Rectangle
   - Circle  
   - Freehand/Polygon
4. Draw the shape on the map
5. Give it a name/callsign (optional)

### View All AOIs
1. Tap the **OmniCOT plugin button** on toolbar
2. Dashboard shows all shapes as cards
3. Scroll through the list

### Zoom to an AOI
**Two ways:**
- Tap anywhere on the AOI card
- Tap the **"Zoom To"** button

The map will automatically:
- Pan to the AOI's center
- Adjust zoom to show the entire shape

### Delete an AOI
1. Tap the **"Delete"** button on any card
2. The shape is removed from the map immediately
3. The list refreshes automatically
4. Toast message confirms deletion

### Refresh the List
- Tap the **"Refresh"** button in the header
- Useful after drawing new shapes
- Updates the list with latest map data

## Card Information

Each AOI card shows:
- **Name**: The shape's callsign/title (or "Unnamed AOI")
- **Type Badge**: Circle, Rectangle, Polygon, or Freehand
- **Area**: Size in square meters or square kilometers
- **Center**: Latitude/Longitude coordinates
- **Actions**: Zoom To and Delete buttons

## Color Coding

- **Orange badge**: Shape type indicator
- **White text**: Primary information
- **Gray text**: Labels
- **Red text**: Delete button (warning color)
- **Dark cards**: Match ATAK's dark theme

## Tips

✅ **Click the entire card** to quickly zoom to an AOI (fastest method)

✅ **Use Refresh** after drawing multiple shapes to see them all

✅ **Area is approximate** for geographic shapes (good enough for tactical use)

✅ **Empty state instructions** help new users understand how to create AOIs

✅ **Monospace coordinates** make lat/long easier to read

## Integration with Existing Features

The AOI Management feature works alongside:
- ✅ Affiliation changing (still works via radial menu)
- ✅ COT broadcasting (unchanged)
- ✅ All other ATAK functionality

## Architecture Flow

```
User clicks plugin button
         ↓
OmniCOTPlugin.showPane()
         ↓
AoiManager.getAllAois()
         ↓
Searches all MapGroups recursively
         ↓
Finds DrawingCircle, DrawingRectangle, Shape
         ↓
Creates AoiInfo objects
         ↓
AoiAdapter displays in RecyclerView
         ↓
User taps "Zoom To"
         ↓
AoiManager.zoomToAoi()
         ↓
Map pans and zooms to shape
```

## File Structure

```
plugin/
├── AoiInfo.java           ← Data model
├── AoiManager.java        ← Find & manage shapes
├── AoiAdapter.java        ← Display cards
└── OmniCOTPlugin.java     ← Main plugin (updated)

res/layout/
├── aoi_management_layout.xml  ← Dashboard
└── aoi_card_item.xml          ← Individual cards

res/drawable/
└── icon_aoi.xml           ← AOI icon

res/values/
└── strings.xml            ← Text resources (updated)
```

## What Happens Behind the Scenes

1. **When dashboard opens:**
   - Queries ATAK's map group hierarchy
   - Finds all shapes (circles, rectangles, polygons)
   - Calculates area for each shape
   - Creates card for each AOI
   - Shows empty state if none found

2. **When you tap "Zoom To":**
   - Gets shape bounds (min/max lat/long)
   - Calculates center point
   - Determines appropriate zoom level
   - Animates map to position

3. **When you tap "Delete":**
   - Removes shape from its map group
   - Refreshes the list
   - Updates empty state visibility
   - Shows toast confirmation

## Next Steps

You can now:
1. Build the plugin (if needed, run `./gradlew assembleCivDebug`)
2. Install on ATAK device
3. Draw some shapes on the map
4. Open OmniCOT plugin to see the dashboard
5. Interact with your AOIs!

Enjoy managing your Areas of Interest! 🗺️
