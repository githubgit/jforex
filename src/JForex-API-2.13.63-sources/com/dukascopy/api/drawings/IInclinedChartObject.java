package com.dukascopy.api.drawings;

import java.awt.Point;
import java.math.BigDecimal;
import java.math.RoundingMode;

public interface IInclinedChartObject {

    String DEGREE_SIGN = "\u00B0";

    /**
     * Sets angle for chart object drawing.
     * If it is not Double.NaN (default value)
     * then object's second point is calculated from first point using this angle.
     * @param angle Angle for chart object.
     */
    void setAngle(double angle);

    /**
     * Returns angle for chart object drawing.
     * @return Angle for chart object.
     */
    double getAngle();

    static double calculateAngle(int x1, double y1, int x2, double y2) {

        double angle = Math.atan((y1 - y2) / Math.abs(x1 - x2)) * 180 / Math.PI;
        if (x2 < x1) {
            angle = (angle < 0 ? -1 : 1) * 180 - angle;
        }

        angle = BigDecimal.valueOf(angle).setScale(1, RoundingMode.HALF_EVEN).doubleValue();

        return angle;
    }

    static Point calculatePoint(int x1, double y1, int x2, double angle) {

        if (Math.abs(angle) > 90) {
            angle = (angle < 0 ? -1 : 1) * 180 - angle;
            if (x2 > x1) {
                x2 = x1 - (x2 - x1);
            }
        } else if (Math.abs(angle) < 90) {
            if (x2 < x1) {
                x2 = x1 + (x1 - x2);
            }
        }

        double y2 = y1 - Math.abs(x1 - x2) * Math.tan(angle * Math.PI / 180);

        if (y2 > Integer.MAX_VALUE) {
            y2 = Integer.MAX_VALUE;
        } else if (y2 < Integer.MIN_VALUE) {
            y2 = Integer.MIN_VALUE;
        }
        y2 = Math.round(y2);

        return new Point(x2, (int) y2);
    }

}
