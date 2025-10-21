
package com.atakmap.android.omnicot.plugin;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.atak.plugins.impl.PluginContextProvider;
import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.menu.MapMenuReceiver;

import java.util.List;

import gov.tak.api.plugin.IPlugin;
import gov.tak.api.plugin.IServiceController;
import gov.tak.api.ui.IHostUIService;
import gov.tak.api.ui.Pane;
import gov.tak.api.ui.PaneBuilder;
import gov.tak.api.ui.ToolbarItem;
import gov.tak.api.ui.ToolbarItemAdapter;
import gov.tak.platform.marshal.MarshalManager;

/**
 * OmniCOT Plugin - Allows users to change marker affiliations with COT broadcasting
 * and manage Areas of Interest (AOI)
 */
public class OmniCOTPlugin implements IPlugin {
    private static final String TAG = "OmniCOTPlugin";

    IServiceController serviceController;
    Context pluginContext;
    IHostUIService uiService;
    ToolbarItem toolbarItem;
    Pane mainDashboardPane;
    Pane aoiPane;
    Pane cotDashboardPane;

    // Plugin components
    private AffiliationMenuFactory menuFactory;
    
    // AOI management components
    private ListView aoiListView;
    private AoiAdapter aoiAdapter;
    private LinearLayout emptyStateContainer;
    
    // CoT Dashboard components
    private TextView cotTotalCount;
    private TextView cotUpdatedCount;
    private TextView cotUnknownCount;
    private TextView cotNeutralCount;
    private TextView cotFriendlyCount;
    private TextView cotHostileCount;

    public OmniCOTPlugin(IServiceController serviceController) {
        this.serviceController = serviceController;
        final PluginContextProvider ctxProvider = serviceController
                .getService(PluginContextProvider.class);
        if (ctxProvider != null) {
            pluginContext = ctxProvider.getPluginContext();
            pluginContext.setTheme(R.style.ATAKPluginTheme);
        }

        // obtain the UI service
        uiService = serviceController.getService(IHostUIService.class);

        // initialize the toolbar button for the plugin
        // create the button
        toolbarItem = new ToolbarItem.Builder(
                pluginContext.getString(R.string.app_name),
                MarshalManager.marshal(
                        pluginContext.getResources().getDrawable(R.drawable.ic_launcher),
                        android.graphics.drawable.Drawable.class,
                        gov.tak.api.commons.graphics.Bitmap.class))
                .setListener(new ToolbarItemAdapter() {
                    @Override
                    public void onClick(ToolbarItem item) {
                        showPane();
                    }
                })
                .build();

        // Initialize menu factory
        menuFactory = new AffiliationMenuFactory(pluginContext);
        Log.d(TAG, "OmniCOT plugin initialized");
    }

    @Override
    public void onStart() {
        Log.d(TAG, "OmniCOT plugin starting");

        // the plugin is starting, add the button to the toolbar
        if (uiService != null) {
            uiService.addToolbarItem(toolbarItem);
        }

        // Register radial menu factory
        if (menuFactory != null) {
            MapMenuReceiver.getInstance().registerMapMenuFactory(menuFactory);
            Log.i(TAG, "Affiliation menu factory registered");
        }
    }

    @Override
    public void onStop() {
        Log.d(TAG, "OmniCOT plugin stopping");

        // the plugin is stopping, remove the button from the toolbar
        if (uiService != null) {
            uiService.removeToolbarItem(toolbarItem);
        }

        // Unregister radial menu factory
        if (menuFactory != null) {
            MapMenuReceiver.getInstance().unregisterMapMenuFactory(menuFactory);
            Log.i(TAG, "Affiliation menu factory unregistered");
        }
    }

    private void showPane() {
        // Show the main dashboard
        if(mainDashboardPane == null) {
            View mainView = PluginLayoutInflater.inflate(pluginContext,
                    R.layout.main_dashboard_layout, null);
            
            // Setup navigation cards
            LinearLayout cotCard = mainView.findViewById(R.id.card_cot_dashboard);
            LinearLayout aoiCard = mainView.findViewById(R.id.card_aoi_management);
            
            TextView previewTotalCot = mainView.findViewById(R.id.preview_total_cot);
            TextView previewUpdatedCot = mainView.findViewById(R.id.preview_updated_cot);
            TextView previewAoiCount = mainView.findViewById(R.id.preview_aoi_count);
            
            // Update preview stats
            updatePreviewStats(previewTotalCot, previewUpdatedCot, previewAoiCount);
            
            // Setup click listeners for navigation
            cotCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCotDashboard();
                }
            });
            
            aoiCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAoiManagement();
                }
            });
            
            mainDashboardPane = new PaneBuilder(mainView)
                    .setMetaValue(Pane.RELATIVE_LOCATION, Pane.Location.Default)
                    .setMetaValue(Pane.PREFERRED_WIDTH_RATIO, 0.5D)
                    .setMetaValue(Pane.PREFERRED_HEIGHT_RATIO, 0.5D)
                    .build();
        }

        if(!uiService.isPaneVisible(mainDashboardPane)) {
            uiService.showPane(mainDashboardPane, null);
        }
    }
    
    /**
     * Show the CoT Dashboard pane
     */
    private void showCotDashboard() {
        if(cotDashboardPane == null) {
            View cotView = PluginLayoutInflater.inflate(pluginContext,
                    R.layout.cot_dashboard_layout, null);
            
            // Initialize CoT Dashboard components
            cotTotalCount = cotView.findViewById(R.id.cot_total_count);
            cotUpdatedCount = cotView.findViewById(R.id.cot_updated_count);
            cotUnknownCount = cotView.findViewById(R.id.cot_unknown_count);
            cotNeutralCount = cotView.findViewById(R.id.cot_neutral_count);
            cotFriendlyCount = cotView.findViewById(R.id.cot_friendly_count);
            cotHostileCount = cotView.findViewById(R.id.cot_hostile_count);
            
            Button backButton = cotView.findViewById(R.id.btn_back_cot);
            Button refreshButton = cotView.findViewById(R.id.btn_refresh_cot);
            
            // Setup back button
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (uiService != null && cotDashboardPane != null && uiService.isPaneVisible(cotDashboardPane)) {
                        uiService.closePane(cotDashboardPane);
                        Log.d(TAG, "CoT Dashboard closed via back button");
                    }
                }
            });
            
            // Setup refresh button
            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateCotStatistics();
                    AffiliationUpdater.showToast("CoT statistics refreshed");
                }
            });
            
            cotDashboardPane = new PaneBuilder(cotView)
                    .setMetaValue(Pane.RELATIVE_LOCATION, Pane.Location.Default)
                    .setMetaValue(Pane.PREFERRED_WIDTH_RATIO, 0.5D)
                    .setMetaValue(Pane.PREFERRED_HEIGHT_RATIO, 0.5D)
                    .build();
        }
        
        // Update stats when showing
        updateCotStatistics();
        
        if(!uiService.isPaneVisible(cotDashboardPane)) {
            uiService.showPane(cotDashboardPane, null);
        }
    }
    
    /**
     * Show the AOI Management pane
     */
    private void showAoiManagement() {
        if(aoiPane == null) {
            // Inflate the AOI management layout
            View aoiView = PluginLayoutInflater.inflate(pluginContext,
                    R.layout.aoi_management_layout, null);
            
            // Initialize UI components
            aoiListView = aoiView.findViewById(R.id.aoi_list_view);
            emptyStateContainer = aoiView.findViewById(R.id.empty_state_container);
            Button backButton = aoiView.findViewById(R.id.btn_back);
            Button refreshButton = aoiView.findViewById(R.id.btn_refresh);
            
            // Setup back button to return to main dashboard
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Close the pane when back button is clicked
                    if (uiService != null && aoiPane != null && uiService.isPaneVisible(aoiPane)) {
                        uiService.closePane(aoiPane);
                        Log.d(TAG, "AOI pane closed via back button");
                    }
                }
            });
            
            // Setup ListView
            aoiAdapter = new AoiAdapter(pluginContext, new AoiAdapter.OnAoiActionListener() {
                @Override
                public void onZoomToAoi(AoiInfo aoiInfo) {
                    Log.d(TAG, "Zooming to AOI: " + aoiInfo.getName());
                    AoiManager.zoomToAoi(aoiInfo);
                    AffiliationUpdater.showToast("Zooming to " + aoiInfo.getName());
                }

                @Override
                public void onDeleteAoi(AoiInfo aoiInfo) {
                    Log.d(TAG, "Deleting AOI: " + aoiInfo.getName());
                    AoiManager.deleteAoi(aoiInfo);
                    AffiliationUpdater.showToast("Deleted " + aoiInfo.getName());
                    // Refresh the list
                    refreshAoiList();
                }
            });
            aoiListView.setAdapter(aoiAdapter);
            
            // Setup refresh button
            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshAoiList();
                    AffiliationUpdater.showToast("AOI list refreshed");
                }
            });
            
            // Create the pane
            aoiPane = new PaneBuilder(aoiView)
                    // relative location is set to default; pane will switch location dependent on
                    // current orientation of device screen
                    .setMetaValue(Pane.RELATIVE_LOCATION, Pane.Location.Default)
                    // pane will take up 50% of screen width in landscape mode
                    .setMetaValue(Pane.PREFERRED_WIDTH_RATIO, 0.5D)
                    // pane will take up 50% of screen height in portrait mode
                    .setMetaValue(Pane.PREFERRED_HEIGHT_RATIO, 0.5D)
                    .build();
        }

        // Load AOI data when showing pane
        refreshAoiList();

        // if the plugin pane is not visible, show it!
        if(!uiService.isPaneVisible(aoiPane)) {
            uiService.showPane(aoiPane, null);
        }
    }
    
    /**
     * Refresh the AOI list from the map
     */
    private void refreshAoiList() {
        Log.d(TAG, "Refreshing AOI list");
        
        // Update CoT statistics
        updateCotStatistics();
        
        // Get all AOIs from the map
        List<AoiInfo> aois = AoiManager.getAllAois();
        
        // Update adapter
        if (aoiAdapter != null) {
            aoiAdapter.setAoiList(aois);
        }
        
        // Show/hide empty state
        if (emptyStateContainer != null && aoiListView != null) {
            if (aois.isEmpty()) {
                emptyStateContainer.setVisibility(View.VISIBLE);
                aoiListView.setVisibility(View.GONE);
            } else {
                emptyStateContainer.setVisibility(View.GONE);
                aoiListView.setVisibility(View.VISIBLE);
            }
        }
        
        Log.d(TAG, "AOI list updated with " + aois.size() + " items");
    }
    
    /**
     * Update CoT statistics dashboard
     */
    private void updateCotStatistics() {
        if (cotTotalCount == null) return;
        
        try {
            // Calculate statistics
            CotStatistics stats = CotStatistics.calculateStatistics();
            
            // Update UI
            cotTotalCount.setText(String.valueOf(stats.getTotalCot()));
            cotUpdatedCount.setText(String.valueOf(stats.getUpdatedCot()));
            cotUnknownCount.setText(String.valueOf(stats.getUnknownCount()));
            cotNeutralCount.setText(String.valueOf(stats.getNeutralCount()));
            cotFriendlyCount.setText(String.valueOf(stats.getFriendlyCount()));
            cotHostileCount.setText(String.valueOf(stats.getHostileCount()));
            
            Log.d(TAG, "CoT statistics updated - Total: " + stats.getTotalCot() + 
                  ", Updated: " + stats.getUpdatedCot());
        } catch (Exception e) {
            Log.e(TAG, "Error updating CoT statistics", e);
        }
    }
    
    /**
     * Update preview statistics on main dashboard
     */
    private void updatePreviewStats(TextView previewTotalCot, TextView previewUpdatedCot, TextView previewAoiCount) {
        try {
            // Calculate CoT statistics
            CotStatistics stats = CotStatistics.calculateStatistics();
            
            // Get AOI count
            List<AoiInfo> aois = AoiManager.getAllAois();
            
            // Update preview text views
            previewTotalCot.setText(String.valueOf(stats.getTotalCot()));
            previewUpdatedCot.setText(String.valueOf(stats.getUpdatedCot()));
            previewAoiCount.setText(String.valueOf(aois.size()));
            
            Log.d(TAG, "Preview stats updated - CoT: " + stats.getTotalCot() + 
                  ", Updated: " + stats.getUpdatedCot() + ", AOIs: " + aois.size());
        } catch (Exception e) {
            Log.e(TAG, "Error updating preview statistics", e);
        }
    }
}
