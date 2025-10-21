package com.atakmap.android.omnicot.plugin;

import com.atakmap.android.maps.MapItem;
import com.atakmap.coremap.maps.coords.GeoPoint;

/**
 * Data class representing an Area of Interest (AOI) shape on the map
 */
public class AoiInfo {
    private final String uid;
    private final String name;
    private final String shapeType;
    private final GeoPoint center;
    private final double area; // in square meters
    private final MapItem mapItem;

    public AoiInfo(MapItem mapItem, String shapeType, GeoPoint center, double area) {
        this.mapItem = mapItem;
        this.uid = mapItem.getUID();
        this.name = mapItem.getMetaString("callsign", mapItem.getTitle());
        this.shapeType = shapeType;
        this.center = center;
        this.area = area;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name != null && !name.isEmpty() ? name : "Unnamed AOI";
    }

    public String getShapeType() {
        return shapeType;
    }

    public GeoPoint getCenter() {
        return center;
    }

    public double getArea() {
        return area;
    }

    public MapItem getMapItem() {
        return mapItem;
    }

    /**
     * Get formatted area string
     */
    public String getFormattedArea() {
        if (area < 1000) {
            return String.format("%.1f m²", area);
        } else if (area < 1000000) {
            return String.format("%.2f km²", area / 1000000.0);
        } else {
            return String.format("%.1f km²", area / 1000000.0);
        }
    }

    /**
     * Get formatted coordinates string
     */
    public String getFormattedCoordinates() {
        if (center == null) return "No location";
        return String.format("%.5f, %.5f", center.getLatitude(), center.getLongitude());
    }
}
