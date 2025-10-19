package com.atakmap.android.plugintemplate.plugin;

import android.content.Context;
import android.util.Log;

import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapDataRef;
import com.atakmap.android.maps.MapItem;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.maps.assets.MapAssets;
import com.atakmap.android.menu.MapMenuButtonWidget;
import com.atakmap.android.menu.MapMenuFactory;
import com.atakmap.android.menu.MapMenuWidget;
import com.atakmap.android.menu.MenuMapAdapter;
import com.atakmap.android.menu.MenuResourceFactory;
import com.atakmap.android.menu.PluginMenuParser;
import com.atakmap.android.widgets.WidgetIcon;

import java.io.IOException;

import gov.tak.api.widgets.IMapMenuButtonWidget;

/**
 * Factory that adds affiliation controls to map item radial menus
 */
public class AffiliationMenuFactory implements MapMenuFactory {
    private static final String TAG = "AffiliationMenuFactory";

    private final Context atakContext;
    private final Context pluginContext;
    private final MenuResourceFactory resourceFactory;

    public AffiliationMenuFactory(Context pluginContext) {
        final MapView mapView = MapView.getMapView();
        this.atakContext = mapView.getContext();
        this.pluginContext = pluginContext;

        // Set up resource factory for default menu creation
        final MapAssets mapAssets = new MapAssets(atakContext);
        final MenuMapAdapter adapter = new MenuMapAdapter();
        try {
            adapter.loadMenuFilters(mapAssets, "filters/menu_filters.xml");
        } catch (IOException e) {
            Log.w(TAG, "Could not load menu filters", e);
        }

        this.resourceFactory = new MenuResourceFactory(mapView,
            mapView.getMapData(), mapAssets, adapter);
    }

    @Override
    public MapMenuWidget create(MapItem item) {
        if (item == null) return null;

        // Get the default menu from ATAK
        final MapMenuWidget menuWidget = resourceFactory.create(item);
        if (menuWidget == null) return null;

        try {
            // Create our affiliation button
            MapMenuButtonWidget affiliationButton = createAffiliationButton(item);

            // Add to the main menu
            menuWidget.addWidget(affiliationButton);

            Log.d(TAG, "Added affiliation button to menu for " + item.getUID());
            return menuWidget;

        } catch (Exception e) {
            Log.e(TAG, "Error adding affiliation button to menu", e);
            return menuWidget; // Return default menu on error
        }
    }

    /**
     * Create the main affiliation button with submenu
     */
    private MapMenuButtonWidget createAffiliationButton(MapItem item) {
        MapMenuButtonWidget button = new MapMenuButtonWidget(atakContext);

        // Set icon
        button.setIcon(createWidgetIconFromDrawable(R.drawable.icon_affiliation));

        // Create submenu with 4 affiliation options
        float radius = 100f; // Default radius, will be adjusted
        MapMenuWidget submenu = createAffiliationSubmenu(item, radius);

        // Set submenu
        button.setSubmenu(submenu);

        // Set click handler - if no submenu shown, default action
        button.setOnButtonClickHandler(new IMapMenuButtonWidget.OnButtonClickHandler() {
            @Override
            public boolean isSupported(Object o) {
                return o == null || o instanceof MapItem;
            }

            @Override
            public void performAction(Object o) {
                // The submenu will handle the actual selections
                // This is called if button is clicked without submenu showing
                Log.d(TAG, "Affiliation button clicked for " + item.getUID());
            }
        });

        return button;
    }

    /**
     * Create the submenu with 4 affiliation options
     */
    private MapMenuWidget createAffiliationSubmenu(MapItem item, float radius) {
        MapMenuWidget submenu = new MapMenuWidget();

        // Create buttons for each affiliation
        submenu.addWidget(createAffiliationOptionButton(
            item, AffiliationUpdater.Affiliation.UNKNOWN, R.drawable.icon_aff_unknown, radius));
        submenu.addWidget(createAffiliationOptionButton(
            item, AffiliationUpdater.Affiliation.NEUTRAL, R.drawable.icon_aff_neutral, radius));
        submenu.addWidget(createAffiliationOptionButton(
            item, AffiliationUpdater.Affiliation.FRIENDLY, R.drawable.icon_aff_friendly, radius));
        submenu.addWidget(createAffiliationOptionButton(
            item, AffiliationUpdater.Affiliation.HOSTILE, R.drawable.icon_aff_hostile, radius));

        return submenu;
    }

    /**
     * Create a button for a specific affiliation option
     */
    private MapMenuButtonWidget createAffiliationOptionButton(
            MapItem item, AffiliationUpdater.Affiliation affiliation,
            int drawableResId, float radius) {

        MapMenuButtonWidget button = new MapMenuButtonWidget(atakContext);

        // Set icon
        button.setIcon(createWidgetIconFromDrawable(drawableResId));

        // Set radius for submenu positioning
        button.setOrientation(button.getOrientationAngle(), radius);

        // Set click handler
        button.setOnButtonClickHandler(new IMapMenuButtonWidget.OnButtonClickHandler() {
            @Override
            public boolean isSupported(Object o) {
                return o == null || o instanceof MapItem;
            }

            @Override
            public void performAction(Object o) {
                Log.d(TAG, "Changing affiliation to " + affiliation.label +
                      " for " + item.getUID());

                // Update affiliation and broadcast
                boolean success = AffiliationUpdater.updateAffiliation(item, affiliation);

                if (success) {
                    AffiliationUpdater.showToast("Affiliation changed to " +
                                                affiliation.label);
                    hideMenus();
                } else {
                    AffiliationUpdater.showToast("Failed to update affiliation");
                }
            }
        });

        return button;
    }

    /**
     * Create a WidgetIcon from a drawable resource
     */
    private WidgetIcon createWidgetIconFromDrawable(int drawableResId) {
        // Create URI for the drawable resource
        String uri = "android.resource://" + pluginContext.getPackageName() + "/" + drawableResId;

        // Parse URI to MapDataRef
        MapDataRef mapDataRef = MapDataRef.parseUri(uri);

        return new WidgetIcon.Builder()
            .setImageRef(0, mapDataRef)
            .setAnchor(16, 16)
            .setSize(32, 32)
            .build();
    }

    /**
     * Send hide intents to close menus
     */
    private void hideMenus() {
        AtakBroadcast.getInstance().sendBroadcast(
            new android.content.Intent("com.atakmap.android.maps.HIDE_MENU"));
        AtakBroadcast.getInstance().sendBroadcast(
            new android.content.Intent("com.atakmap.android.maps.UNFOCUS"));
    }
}
