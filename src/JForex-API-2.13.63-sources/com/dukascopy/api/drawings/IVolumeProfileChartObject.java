package com.dukascopy.api.drawings;

import java.awt.Color;

public interface IVolumeProfileChartObject extends IFillableChartObject {

    enum RowsLayout {NUMBER_OF_ROWS, POINTS_PER_ROW}

    enum VolumeType {UP_DOWN, TOTAL}

	enum VolumeProfileParams {
	    START_TIME(Long.class),
	    END_TIME(Long.class),
	    NUM_OF_PERIODS(Integer.class),
		OPEN_PRICE(Double.class),
		HIGH_PRICE(Double.class),
		LOW_PRICE(Double.class),
		CLOSE_PRICE(Double.class),
		HIGH_LOW_RANGE(Double.class),
		TOTAL_VOLUME(Double.class),
		VOLUMES(double[][].class);

        private final Class<?> type;

        private VolumeProfileParams(Class<?> type) {
            this.type = type;
        }

        public Class<?> getType() {
            return type;
        }
	}

    void setRowsLayout(RowsLayout rowsLayout);

    RowsLayout getRowsLayout();

    void setRowSize(int rowSize);

    int getRowSize();

    void setVolumeType(VolumeType volumeType);

    VolumeType getVolumeType();

    void setShowValues(boolean showValues);

    boolean getShowValues();

    void setWidthPercent(int percent);

    int getWidthPercent();

    void setVolumeUpColor(Color color);

    Color getVolumeUpColor();

    void setVolumeDownColor(Color color);

    Color getVolumeDownColor();

    void setVolumeOpacity(float opacity);

    float getVolumeOpacity();

}
