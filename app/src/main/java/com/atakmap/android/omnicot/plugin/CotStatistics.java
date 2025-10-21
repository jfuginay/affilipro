package com.atakmap.android.omnicot.plugin;

import android.util.Log;

import com.atakmap.android.maps.MapGroup;
import com.atakmap.android.maps.MapItem;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.maps.Marker;

import java.util.Collection;

/**
 * Tracks and calculates statistics about Cursor on Target (CoT) items
 */
public class CotStatistics {
    private static final String TAG = "CotStatistics";
    private static final String OMNICOT_UPDATED_KEY = "omnicot_updated";
    
    private int totalCot = 0;
    private int updatedCot = 0;
    private int unknownCount = 0;
    private int neutralCount = 0;
    private int friendlyCount = 0;
    private int hostileCount = 0;

    /**
     * Calculate statistics from all CoT items on the map
     */
    public static CotStatistics calculateStatistics() {
        CotStatistics stats = new CotStatistics();
        MapView mapView = MapView.getMapView();
        
        if (mapView == null) {
            Log.w(TAG, "MapView is null");
            return stats;
        }

        try {
            MapGroup rootGroup = mapView.getRootGroup();
            if (rootGroup != null) {
                stats.processMapGroup(rootGroup);
            }
            
            Log.d(TAG, String.format("CoT Statistics - Total: %d, Updated: %d, U:%d, N:%d, F:%d, H:%d",
                    stats.totalCot, stats.updatedCot, stats.unknownCount, 
                    stats.neutralCount, stats.friendlyCount, stats.hostileCount));
        } catch (Exception e) {
            Log.e(TAG, "Error calculating CoT statistics", e);
        }

        return stats;
    }

    /**
     * Recursively process map groups to count CoT items
     */
    private void processMapGroup(MapGroup group) {
        if (group == null) return;

        // Process items in this group
        Collection<MapItem> items = group.getItems();
        for (MapItem item : items) {
            processMapItem(item);
        }

        // Recursively process child groups
        Collection<MapGroup> childGroups = group.getChildGroups();
        for (MapGroup childGroup : childGroups) {
            processMapGroup(childGroup);
        }
    }

    /**
     * Process a single map item and update statistics
     */
    private void processMapItem(MapItem item) {
        if (item == null) return;

        // Only count markers (CoT items)
        if (!(item instanceof Marker)) return;

        String type = item.getType();
        if (type == null || type.length() < 3) return;

        // Ignore certain types (like drawing tools, routes, etc.)
        if (isNonCotType(type)) return;

        totalCot++;

        // Check if this item was updated by OmniCOT
        boolean wasUpdated = item.getMetaBoolean(OMNICOT_UPDATED_KEY, false);
        if (wasUpdated) {
            updatedCot++;
        }

        // Count by affiliation (character at index 2 in CoT type)
        char affiliation = type.charAt(2);
        switch (affiliation) {
            case 'u':
                unknownCount++;
                break;
            case 'n':
                neutralCount++;
                break;
            case 'f':
                friendlyCount++;
                break;
            case 'h':
                hostileCount++;
                break;
        }
    }

    /**
     * Check if a type string represents a non-CoT item (drawing tools, etc.)
     */
    private boolean isNonCotType(String type) {
        // Exclude drawing tools and other non-CoT items
        return type.startsWith("u-d-") || // Drawing tools
               type.startsWith("b-m-p-s-p-loc") || // Route waypoints
               type.startsWith("u-rb-") || // Radial menu items
               type.contains("drawing") ||
               type.contains("shape");
    }

    // Getters
    public int getTotalCot() {
        return totalCot;
    }

    public int getUpdatedCot() {
        return updatedCot;
    }

    public int getUnknownCount() {
        return unknownCount;
    }

    public int getNeutralCount() {
        return neutralCount;
    }

    public int getFriendlyCount() {
        return friendlyCount;
    }

    public int getHostileCount() {
        return hostileCount;
    }

    /**
     * Mark a MapItem as updated by OmniCOT
     */
    public static void markAsUpdated(MapItem item) {
        if (item != null) {
            item.setMetaBoolean(OMNICOT_UPDATED_KEY, true);
            Log.d(TAG, "Marked item as updated: " + item.getUID());
        }
    }
}
