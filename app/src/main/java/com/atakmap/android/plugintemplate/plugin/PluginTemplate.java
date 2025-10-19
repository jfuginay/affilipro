
package com.atakmap.android.plugintemplate.plugin;

import android.content.Context;
import android.util.Log;

import com.atak.plugins.impl.PluginContextProvider;
import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.menu.MapMenuReceiver;

import gov.tak.api.plugin.IPlugin;
import gov.tak.api.plugin.IServiceController;
import gov.tak.api.ui.IHostUIService;
import gov.tak.api.ui.Pane;
import gov.tak.api.ui.PaneBuilder;
import gov.tak.api.ui.ToolbarItem;
import gov.tak.api.ui.ToolbarItemAdapter;
import gov.tak.platform.marshal.MarshalManager;

/**
 * Affiliation Pro Plugin - Allows users to change marker affiliations with COT broadcasting
 */
public class PluginTemplate implements IPlugin {
    private static final String TAG = "AffiliationPlugin";

    IServiceController serviceController;
    Context pluginContext;
    IHostUIService uiService;
    ToolbarItem toolbarItem;
    Pane templatePane;

    // Plugin components
    private AffiliationMenuFactory menuFactory;

    public PluginTemplate(IServiceController serviceController) {
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
        Log.d(TAG, "Affiliation Pro plugin initialized");
    }

    @Override
    public void onStart() {
        Log.d(TAG, "Affiliation Pro plugin starting");

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
        Log.d(TAG, "Affiliation Pro plugin stopping");

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
        // instantiate the plugin view if necessary
        if(templatePane == null) {
            // Remember to use the PluginLayoutInflator if you are actually inflating a custom view
            // In this case, using it is not necessary - but I am putting it here to remind
            // developers to look at this Inflator

            templatePane = new PaneBuilder(PluginLayoutInflater.inflate(pluginContext,
                    R.layout.main_layout, null))
                    // relative location is set to default; pane will switch location dependent on
                    // current orientation of device screen
                    .setMetaValue(Pane.RELATIVE_LOCATION, Pane.Location.Default)
                    // pane will take up 50% of screen width in landscape mode
                    .setMetaValue(Pane.PREFERRED_WIDTH_RATIO, 0.5D)
                    // pane will take up 50% of screen height in portrait mode
                    .setMetaValue(Pane.PREFERRED_HEIGHT_RATIO, 0.5D)
                    .build();
        }

        // if the plugin pane is not visible, show it!
        if(!uiService.isPaneVisible(templatePane)) {
            uiService.showPane(templatePane, null);
        }
    }
}
