package com.atakmap.android.plugintemplate.plugin;

import android.util.Log;
import android.widget.Toast;

import com.atakmap.android.cot.CotMapComponent;
import com.atakmap.android.importexport.CotEventFactory;
import com.atakmap.android.maps.MapItem;
import com.atakmap.android.maps.MapView;
import com.atakmap.coremap.cot.event.CotDetail;
import com.atakmap.coremap.cot.event.CotEvent;

/**
 * Utility class to update marker affiliations and broadcast changes via COT
 */
public class AffiliationUpdater {
    private static final String TAG = "AffiliationUpdater";

    public enum Affiliation {
        UNKNOWN('u', "Unknown"),
        NEUTRAL('n', "Neutral"),
        FRIENDLY('f', "Friendly"),
        HOSTILE('h', "Hostile");

        public final char code;
        public final String label;

        Affiliation(char code, String label) {
            this.code = code;
            this.label = label;
        }

        public static Affiliation fromCode(char code) {
            for (Affiliation aff : values()) {
                if (aff.code == code) return aff;
            }
            return UNKNOWN;
        }
    }

    /**
     * Update a marker's affiliation and broadcast to all team members
     * @param mapItem The map item to update
     * @param newAffiliation The new affiliation
     * @return true if successful, false otherwise
     */
    public static boolean updateAffiliation(MapItem mapItem, Affiliation newAffiliation) {
        if (mapItem == null) {
            Log.w(TAG, "Cannot update null MapItem");
            return false;
        }

        try {
            // Get current COT type (e.g., "a-f-G-E-V")
            String currentType = mapItem.getType();
            if (currentType == null || currentType.length() < 3) {
                Log.w(TAG, "Invalid COT type: " + currentType);
                return false;
            }

            // Parse and update affiliation character at index 2
            char[] typeChars = currentType.toCharArray();
            char oldAffiliation = typeChars.length > 2 ? typeChars[2] : 'u';
            typeChars[2] = newAffiliation.code;
            String newType = new String(typeChars);

            Log.d(TAG, "Updating affiliation from '" + oldAffiliation +
                  "' to '" + newAffiliation.code + "' for " + mapItem.getUID());

            // Create COT event from existing map item
            CotEvent cotEvent = CotEventFactory.createCotEvent(mapItem);
            if (cotEvent == null) {
                Log.w(TAG, "Failed to create COT event from map item");
                return false;
            }

            // Update the type to reflect new affiliation
            cotEvent.setType(newType);
            cotEvent.setHow("h-e"); // Human entry

            // Add remark about affiliation change
            CotDetail detail = cotEvent.getDetail();
            if (detail != null) {
                CotDetail remarks = detail.getFirstChildByName(0, "remarks");
                if (remarks == null) {
                    remarks = new CotDetail("remarks");
                    detail.addChild(remarks);
                }
                String existingRemarks = remarks.getInnerText();
                String newRemarks = "Affiliation changed to " + newAffiliation.label +
                                  " via Affiliation Pro plugin";
                if (existingRemarks != null && !existingRemarks.isEmpty()) {
                    newRemarks = existingRemarks + "; " + newRemarks;
                }
                remarks.setInnerText(newRemarks);
            }

            // CRITICAL: Broadcast to all team members via COT dispatcher
            // Use static method to get dispatcher
            CotMapComponent.getInternalDispatcher().dispatch(cotEvent);
            Log.i(TAG, "Affiliation update broadcasted for " + mapItem.getUID());

            // Update local metadata for immediate feedback
            mapItem.setType(newType);
            mapItem.setMetaString("affiliation", newAffiliation.label);
            mapItem.persist(MapView.getMapView().getMapEventDispatcher(), null,
                          AffiliationUpdater.class);

            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error updating affiliation", e);
            return false;
        }
    }


    /**
     * Get current affiliation from a marker's type
     */
    public static Affiliation getCurrentAffiliation(MapItem mapItem) {
        if (mapItem == null) return Affiliation.UNKNOWN;

        String type = mapItem.getType();
        if (type != null && type.length() > 2) {
            return Affiliation.fromCode(type.charAt(2));
        }
        return Affiliation.UNKNOWN;
    }

    /**
     * Show a toast notification for affiliation update
     */
    public static void showToast(String message) {
        final MapView mapView = MapView.getMapView();
        if (mapView != null && mapView.getContext() != null) {
            mapView.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mapView.getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
