package com.dukascopy.api.drawings;

public interface IGannGridChartObject extends IChartDependentChartObject {
    
    double getPipsPerBar();
    void setPipsPerBar(double pipsPerBar);
    
    int getCellWidthInBars();
    void setCellWidthInBars(int cellWidthInBars);

    /**
     * Sets show width label option.
     *
     * System default value: <b>true</b>
     * @param showWidthLabel show width label option
     */
    void setShowWidthLabel(boolean showWidthLabel);

    /**
     * Returns show width label option.
     *
     * System default value: <b>true</b>
     * @return show width label option
     */
    boolean isWidthLabelShown();
}
