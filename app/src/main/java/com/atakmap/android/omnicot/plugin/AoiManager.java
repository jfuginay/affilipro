package com.atakmap.android.omnicot.plugin;

import android.util.Log;

import com.atakmap.android.drawing.mapItems.DrawingCircle;
import com.atakmap.android.drawing.mapItems.DrawingRectangle;
import com.atakmap.android.maps.MapGroup;
import com.atakmap.android.maps.MapItem;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.maps.Shape;
import com.atakmap.coremap.conversions.Span;
import com.atakmap.coremap.conversions.SpanUtilities;
import com.atakmap.coremap.maps.coords.GeoPoint;
import com.atakmap.coremap.maps.coords.GeoPointMetaData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility class to find and manage Areas of Interest (AOI) on the ATAK map
 */
public class AoiManager {
    private static final String TAG = "AoiManager";

    /**
     * Get all AOI shapes currently on the map
     * @return List of AoiInfo objects representing shapes
     */
    public static List<AoiInfo> getAllAois() {
        List<AoiInfo> aois = new ArrayList<>();
        MapView mapView = MapView.getMapView();
        if (mapView == null) {
            Log.w(TAG, "MapView is null");
            return aois;
        }

        try {
            // Get the root map group
            MapGroup rootGroup = mapView.getRootGroup();
            if (rootGroup == null) {
                Log.w(TAG, "Root group is null");
                return aois;
            }

            // Recursively find all shapes
            findShapesInGroup(rootGroup, aois);
            
            Log.d(TAG, "Found " + aois.size() + " AOI shapes on map");
        } catch (Exception e) {
            Log.e(TAG, "Error getting AOIs", e);
        }

        return aois;
    }

    /**
     * Recursively search for shapes in a map group and its children
     */
    private static void findShapesInGroup(MapGroup group, List<AoiInfo> aois) {
        if (group == null) return;

        // Check all items in this group
        Collection<MapItem> items = group.getItems();
        for (MapItem item : items) {
            AoiInfo aoiInfo = createAoiInfo(item);
            if (aoiInfo != null) {
                aois.add(aoiInfo);
            }
        }

        // Recursively check child groups
        Collection<MapGroup> childGroups = group.getChildGroups();
        for (MapGroup childGroup : childGroups) {
            findShapesInGroup(childGroup, aois);
        }
    }

    /**
     * Create AoiInfo from a MapItem if it's a supported shape type
     */
    private static AoiInfo createAoiInfo(MapItem item) {
        if (item == null) return null;

        try {
            // Check for DrawingCircle
            if (item instanceof DrawingCircle) {
                DrawingCircle circle = (DrawingCircle) item;
                GeoPoint center = circle.getCenterPoint();
                double radius = circle.getRadius();
                double area = Math.PI * radius * radius; // Area of circle

                return new AoiInfo(item, "Circle", center, area);
            }

            // Check for DrawingRectangle
            if (item instanceof DrawingRectangle) {
                DrawingRectangle rect = (DrawingRectangle) item;
                GeoPoint center = rect.getCenter().get();
                
                // Calculate approximate area from bounds
                GeoPoint[] points = rect.getPoints();
                double area = calculatePolygonArea(points);

                return new AoiInfo(item, "Rectangle", center, area);
            }

            // Check for generic Shape (includes polygons, freehand, etc.)
            if (item instanceof Shape) {
                Shape shape = (Shape) item;
                GeoPoint center = shape.getCenter().get();
                
                // Get points and calculate area
                GeoPoint[] points = shape.getPoints();
                double area = calculatePolygonArea(points);

                // Determine shape type from metadata or type string
                String shapeType = determineShapeType(shape);

                return new AoiInfo(item, shapeType, center, area);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error creating AOI info for item " + item.getUID(), e);
        }

        return null;
    }

    /**
     * Determine the specific type of shape
     */
    private static String determineShapeType(Shape shape) {
        String type = shape.getType();
        
        if (type != null) {
            if (type.contains("u-d-r")) return "Rectangle";
            if (type.contains("u-d-c-c")) return "Circle";
            if (type.contains("u-d-f")) return "Freehand";
            if (type.contains("shape")) return "Polygon";
        }

        // Fallback to checking metadata
        String metaType = shape.getMetaString("shapeType", null);
        if (metaType != null) return metaType;

        return "Polygon";
    }

    /**
     * Calculate area of polygon using shoelace formula (approximate for geographic coordinates)
     */
    private static double calculatePolygonArea(GeoPoint[] points) {
        if (points == null || points.length < 3) return 0.0;

        double area = 0.0;
        int n = points.length;

        for (int i = 0; i < n; i++) {
            GeoPoint p1 = points[i];
            GeoPoint p2 = points[(i + 1) % n];
            
            // Convert to approximate meters using Haversine-like calculation
            // This is a simplified calculation - for precise areas, use proper geographic projection
            double lat1 = Math.toRadians(p1.getLatitude());
            double lat2 = Math.toRadians(p2.getLatitude());
            double lon1 = Math.toRadians(p1.getLongitude());
            double lon2 = Math.toRadians(p2.getLongitude());
            
            area += (lon2 - lon1) * (2 + Math.sin(lat1) + Math.sin(lat2));
        }

        area = Math.abs(area * 6378137.0 * 6378137.0 / 2.0); // Earth radius squared
        return area;
    }

    /**
     * Zoom the map to show a specific AOI
     * @param aoiInfo The AOI to zoom to
     */
    public static void zoomToAoi(AoiInfo aoiInfo) {
        if (aoiInfo == null) return;

        MapView mapView = MapView.getMapView();
        if (mapView == null) return;

        try {
            MapItem item = aoiInfo.getMapItem();
            if (item instanceof Shape) {
                Shape shape = (Shape) item;
                
                // Get the bounds and zoom to fit
                GeoPoint[] points = shape.getPoints();
                if (points != null && points.length > 0) {
                    // Calculate bounds
                    double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
                    double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;
                    
                    for (GeoPoint point : points) {
                        minLat = Math.min(minLat, point.getLatitude());
                        maxLat = Math.max(maxLat, point.getLatitude());
                        minLon = Math.min(minLon, point.getLongitude());
                        maxLon = Math.max(maxLon, point.getLongitude());
                    }

                    // Calculate center and scale
                    double centerLat = (minLat + maxLat) / 2.0;
                    double centerLon = (minLon + maxLon) / 2.0;
                    GeoPoint center = new GeoPoint(centerLat, centerLon);

                    // Calculate appropriate scale based on bounds
                    double latDelta = maxLat - minLat;
                    double lonDelta = maxLon - minLon;
                    double maxDelta = Math.max(latDelta, lonDelta);
                    
                    // Convert to map scale (approximate)
                    // Smaller scale value = more zoomed in
                    double scale = mapView.getMapScale();
                    if (maxDelta > 0.001) { // If AOI is reasonably sized
                        scale = 100000.0 / (maxDelta * 100000.0); // Adjust zoom level
                    }

                    // Animate to the AOI
                    mapView.getMapController().panTo(center, true);
                    
                    Log.d(TAG, "Zoomed to AOI: " + aoiInfo.getName());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error zooming to AOI", e);
        }
    }

    /**
     * Delete an AOI from the map
     * @param aoiInfo The AOI to delete
     */
    public static void deleteAoi(AoiInfo aoiInfo) {
        if (aoiInfo == null) return;

        try {
            MapItem item = aoiInfo.getMapItem();
            if (item != null) {
                item.removeFromGroup();
                Log.d(TAG, "Deleted AOI: " + aoiInfo.getName());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting AOI", e);
        }
    }
}
