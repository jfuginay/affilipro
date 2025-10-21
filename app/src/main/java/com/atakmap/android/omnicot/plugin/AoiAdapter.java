package com.atakmap.android.omnicot.plugin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * ListView adapter for displaying AOI cards
 */
public class AoiAdapter extends BaseAdapter {
    private static final String TAG = "AoiAdapter";

    private Context context;
    private List<AoiInfo> aoiList = new ArrayList<>();
    private OnAoiActionListener listener;

    /**
     * Interface for handling AOI card actions
     */
    public interface OnAoiActionListener {
        void onZoomToAoi(AoiInfo aoiInfo);
        void onDeleteAoi(AoiInfo aoiInfo);
    }

    public AoiAdapter(Context context, OnAoiActionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     * Update the list of AOIs and refresh the view
     */
    public void setAoiList(List<AoiInfo> aoiList) {
        this.aoiList = aoiList != null ? aoiList : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * Get current AOI list
     */
    public List<AoiInfo> getAoiList() {
        return aoiList;
    }

    @Override
    public int getCount() {
        return aoiList.size();
    }

    @Override
    public AoiInfo getItem(int position) {
        return aoiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.aoi_card_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final AoiInfo aoiInfo = getItem(position);
        holder.bind(aoiInfo, listener);

        return convertView;
    }

    /**
     * ViewHolder for AOI cards
     */
    static class ViewHolder {
        private final TextView nameTextView;
        private final TextView shapeTypeTextView;
        private final TextView areaTextView;
        private final TextView coordinatesTextView;
        private final Button zoomButton;
        private final Button deleteButton;
        private final View itemView;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
            nameTextView = itemView.findViewById(R.id.aoi_name);
            shapeTypeTextView = itemView.findViewById(R.id.aoi_shape_type);
            areaTextView = itemView.findViewById(R.id.aoi_area);
            coordinatesTextView = itemView.findViewById(R.id.aoi_coordinates);
            zoomButton = itemView.findViewById(R.id.btn_zoom_to_aoi);
            deleteButton = itemView.findViewById(R.id.btn_delete_aoi);
        }

        public void bind(final AoiInfo aoiInfo, final OnAoiActionListener listener) {
            // Set text values
            nameTextView.setText(aoiInfo.getName());
            shapeTypeTextView.setText(aoiInfo.getShapeType());
            areaTextView.setText(aoiInfo.getFormattedArea());
            coordinatesTextView.setText(aoiInfo.getFormattedCoordinates());

            // Set click listeners
            zoomButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onZoomToAoi(aoiInfo);
                    }
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onDeleteAoi(aoiInfo);
                    }
                }
            });

            // Make the whole card clickable to zoom
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onZoomToAoi(aoiInfo);
                    }
                }
            });
        }
    }
}
